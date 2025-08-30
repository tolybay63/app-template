package kz.kis.kisocr.impl;

import kz.kis.kiscore.model.*;
import kz.kis.kiscore.utils.*;
import kz.kis.kisocr.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.rmi.*;
import java.util.*;
import java.util.regex.*;

@Service
public class OCRProcessorImpl implements IOCRProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OCRProcessorImpl.class);

    private String appName = "tesseract";

    @Value("${tesseract.data.path}")
    private String paramTesseractDataPath;
    private String paramFormat = "tessedit_create_tsv=1";
    private String paramLanguageValue = "rus+eng";
    private File tmpFileRes;
    private String tmpFileResName;
    private String sourceFile;
    private String fileName;
    private int paragraphNumber;
    private int blockNum;
    private int paragraph;
    private FileData tessData;
    private Location location;

    public List<FileData> processFile(File file) throws Exception {
        List<FileData> allData = new ArrayList<>();

        int pageNumber = extractPageNumberFromFileName(file.getName());
        allData.addAll(internalProcessFile(file, pageNumber));

        return allData;
    }

    private List<FileData> internalProcessFile(File file, int pageNumber) throws Exception {
        paragraph = 0;
        paragraphNumber = 0;
        blockNum = 0;
        List<FileData> tessDataList = new ArrayList<>();
        tessData = new FileData();
        location = new Location(paragraph);
        location.setPageNumber(pageNumber);
        paragraph++;
        tessData.setLocation(location);
        sourceFile = file.getAbsolutePath();
        fileName = file.getName();
        tmpFileRes = createTempFile("ocr_", ".tsv");
        tmpFileResName = UtFile.removeExt(tmpFileRes.getAbsolutePath());

        execOCR();

        try (BufferedReader br = new BufferedReader(new FileReader(tmpFileRes))) {
            String line;
            while ((line = br.readLine()) != null) {
                mergeTessData(line, tessDataList, pageNumber);
            }
            if (!tessData.isEmpty()) {
                tessDataList.add(tessData);
                paragraph++;
            }
        }

        return tessDataList;
    }

    private int extractPageNumberFromFileName(String fileName) {
        Matcher matcher = Pattern.compile("_(\\d+)-").matcher(fileName);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 1;
    }

    private void execOCR() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(appName, sourceFile, tmpFileResName, "--tessdata-dir", paramTesseractDataPath, "-l", paramLanguageValue, "-c", paramFormat);
        processBuilder.redirectErrorStream(true);
        logger.info("execOCR: {}", processBuilder.command());
        Process process = processBuilder.start();
        process.waitFor();

        if (process.exitValue() != 0) {
            List<String> resLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while (reader.ready()) {
                    resLines.add(reader.readLine());
                }
            }
            logger.error("execOCR, error code: {}, output:", process.exitValue());
            for (String line : resLines) {
                logger.error(">> {}", line);
            }
            throw new RemoteException(resLines.toString());
        }
    }

    private void mergeTessData(String line, List<FileData> tessDataList, int pageNumber) {
        String[] columns = line.split("\t");

        //
        String conf = columns[10];
        if (Objects.equals(conf, "-1") || Objects.equals(conf, "conf")) {
            return;
        }

        //
        String text = columns[11];
        text = text.trim();
        if (text.length() == 0) {
            return;
        }

        //
        int tsvParagraphNumber = Integer.parseInt(columns[3]);
        int tsvBlockNum = Integer.parseInt(columns[2]);
        if (tsvBlockNum != blockNum || tsvParagraphNumber != paragraphNumber) {
            if (!tessData.isEmpty()) {
                tessDataList.add(tessData);
                paragraph++;
            }
            tessData = new FileData();
            location = new Location(paragraph);
            location.setPageNumber(pageNumber);
            tessData.setLocation(location);
            tessData.setSource(fileName);
            paragraphNumber = tsvParagraphNumber;
            blockNum = tsvBlockNum;
        }
        tessData.addText(text);
        float x1 = UtCnv.toFloat(columns[6]);
        float y1 = UtCnv.toFloat(columns[7]);
        float x2 = x1 + UtCnv.toFloat(columns[8]);
        float y2 = y1 + UtCnv.toFloat(columns[9]);
        location.mergeX1(x1);
        location.mergeX2(x2);
        location.mergeY1(y1);
        location.mergeY2(y2);
    }

    private File createTempFile(String prefix, String suffix) {
        String tmpDir = UtFile.getTempdir() + "/";
        try {
            File tempDir = new File(tmpDir);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            return File.createTempFile(prefix, suffix, tempDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void close() {
        if (tmpFileRes != null) {
            tmpFileRes.delete();
            tmpFileRes = null;
        }
    }

}
