package kz.kis.kisocr.listener;

import kz.kis.kiscore.model.*;
import kz.kis.kiscore.service.*;
import kz.kis.kiscore.utils.*;
import kz.kis.kismessagebroker.model.*;
import kz.kis.kismessagebroker.service.*;
import kz.kis.kisocr.impl.*;
import kz.kis.kisstorage.repository.*;
import kz.kis.kistempstorage.*;
import org.apache.kafka.clients.consumer.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.kafka.annotation.*;
import org.springframework.kafka.support.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

@Component
public class OcrListener {

    private static final Logger logger = LoggerFactory.getLogger(OcrListener.class);
    private final OCRProcessorImpl ocrProcessor;
    private final StorageRepository contentStorage;
    private final StorageRepository pageStorage;
    private final MessageService messageService;
    private final MessageDataStorage messageDataStorage;

    public OcrListener(
            OCRProcessorImpl ocrProcessor,
            @Qualifier("contentStorage") StorageRepository contentStorage,
            @Qualifier("pageStorage") StorageRepository pageStorage,
            MessageService messageService,
            MessageDataStorage messageDataStorage
    ) {
        this.ocrProcessor = ocrProcessor;
        this.contentStorage = contentStorage;
        this.pageStorage = pageStorage;
        this.messageService = messageService;
        this.messageDataStorage = messageDataStorage;
    }

    @KafkaListener(topics = {"kis-ocr-in"}, groupId = "ocr")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        Message msg = messageService.parseMessage(record);
        logger.info("OcrListener - received message: {}", msg);

        // Независимо от результатов второй раз не читаем задачу
        acknowledgment.acknowledge();

        //
        try {
            if (handlePingMsg(msg)) {
                return;
            }

            //
            Long fileId = UtCnv.toLong(msg.get("file"));
            String fileHash = UtCnv.toString(msg.get("hash"));

            // Отправляем state
            messageService.sendState(ServiceTaskPoint.TOPIC_KIS_OCR, WorkState.RUNNING, msg);

            // Выполняем
            List<String> res = onMessage(msg);

            // Отправляем state
            if (res != null) {
                Map<String, Object> resultData = Map.of("file", fileId, "hash", fileHash, "ocr", res);
                messageService.sendStateOk(ServiceTaskPoint.TOPIC_KIS_OCR, msg, resultData);
            } else {
                messageService.sendState(ServiceTaskPoint.TOPIC_KIS_OCR, WorkState.IGNORED, msg);
            }


        } catch (Exception e) {
            logger.error("Error processing message: {}", record.value(), e);
            messageService.sendStateError(ServiceTaskPoint.TOPIC_KIS_OCR, e.getMessage(), msg);


        } finally {
            logger.info("OcrListener - ok");
        }
    }

    private boolean handlePingMsg(Message msg) {
        String task = UtCnv.toString(msg.get("task"), null);
        if (task.equals(ServiceTaskPoint.TASK_PING)) {
            messageService.sendStateOk(ServiceTaskPoint.TOPIC_KIS_OCR, msg, null);
            return true;
        }
        return false;
    }

    public List<String> onMessage(Message msg) throws Exception {
        List<String> res = new ArrayList<>();

        //
        long fileId = UtCnv.toLong(msg.get("file"));
        String fileHash = UtCnv.toString(msg.get("hash"));
        String task = UtCnv.toString(msg.get("task"), null);

        //
        switch (task) {

            case ServiceTaskPoint.TASK_KIS_OCR_PAGES -> {
                int pageCount = UtCnv.toInt(msg.get("totalPages"));
                if (pageCount == 0) {
                    throw new Exception("pageCount == 0");
                }
                for (int pageNumber = 1; pageNumber <= pageCount; pageNumber++) {
                    // Распознаем
                    String pageHash = fileHash + "_" + pageNumber;
                    List<FileData> parsedData = processFile(pageStorage, fileId, pageHash);
                    // Запишем во временное хранилище
                    String messageData = messageDataStorage.write(parsedData, fileId);
                    //
                    res.add(messageData);
                }
                return res;
            }

            case ServiceTaskPoint.TASK_KIS_OCR_FILE -> {
                String fileType = UtCnv.toString(msg.get("fileType"));
                if (UtKisFile.isPictureFile(fileType)) {
                    // Распознаем
                    List<FileData> parsedData = processFile(contentStorage, fileId, fileHash);
                    // Запишем во временное хранилище
                    String messageData = messageDataStorage.write(parsedData, fileId);
                    //
                    res.add(messageData);
                    //
                    return res;
                } else {
                    return null;
                }
            }

            default -> throw new Exception("Unknown task: " + task);
        }
    }

    private List<FileData> processFile(StorageRepository storage, long fileId, String fileHash) throws Exception {

        File file = null;
        try {
            // Скачиваем файл из хранилища
            file = storage.downloadFile(fileHash);
            if (file == null) {
                throw new Exception("File not found in storage, hash: " + fileHash);
            }

            // Вызовем OCR
            List<FileData> tessDataList = ocrProcessor.processFile(file);

            // todo решить, полезно ли держать ОБА getTessData и extractParagraphs, и тогда переделать на возврат ОБОИХ
            return tessDataList;

        } finally {
            // Удаляем скачанный файл
            if (file != null) {
                file.delete();
            }
            // Удаляем временные файлы ocrProcessor
            ocrProcessor.close();
        }
    }

}