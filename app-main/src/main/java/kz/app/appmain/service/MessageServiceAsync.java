package kz.app.appmain.service;

import com.fasterxml.jackson.databind.*;
import kz.app.appmain.event.*;
import kz.app.appmain.model.*;
import kz.app.appmessagebroker.model.*;
import kz.app.appmessagebroker.service.*;
import kz.app.appmessagebroker.service.impl.*;
import kz.app.appmain.event.*;
import kz.app.appmain.model.*;
import org.slf4j.*;
import org.springframework.context.*;
import org.springframework.context.event.*;
import org.springframework.kafka.core.*;
import org.springframework.scheduling.annotation.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Обеспечивает отложенную (асинхронную) отправку сообщений.
 * Позволяет не дожидаться отправки сообщений при обработке endpoint от клиента.
 * Держит неотправленные сообщения в памяти.
 */
public class MessageServiceAsync extends MessageServiceImpl implements MessageService, IProcessQueue {

    public static Logger log = LoggerFactory.getLogger(MessageServiceAsync.class);

    /**
     * Сюда складываем отложенные сообщения
     */
    private ConcurrentLinkedQueue<QueuedMessage> queue;

    /**
     * Через него уведомляем отложенную отправку
     */
    private ApplicationEventPublisher eventPublisher;

    //
    private final AtomicBoolean isRunning = new AtomicBoolean(false);


    public MessageServiceAsync(ApplicationEventPublisher eventPublisher, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
        //
        this.queue = new ConcurrentLinkedQueue<>();
        this.eventPublisher = eventPublisher;
        super.log = this.log;
    }


    @EventListener(QueueMessageAdded.class)
    @Async
    public void processQueue() {
        if (isRunning.compareAndSet(false, true)) {
            // Запустим цикл отправки
            try {
                while (!queue.isEmpty()) {
                    try {
                        // Берем сообщение (не извлекая)
                        QueuedMessage queuedMessage = queue.peek();
                        //
                        if (queuedMessage == null) {
                            break;
                        }

                        // Отправляем физически
                        super.send(queuedMessage.getTopic(), queuedMessage.getMessage());

                        // Извлекаем сообщение после успешной отправки
                        queue.poll();

                    } catch (Exception e) {
                        log.error("Message send error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

            } finally {
                isRunning.set(false);
            }

        } else {
            // Просто сообщим
            log.info("processQueue busy, queue.size: " + queue.size());
        }
    }

    @Override
    public void send(String topic, Message message) {
        queue.add(new QueuedMessage(topic, message));
        eventPublisher.publishEvent(new QueueMessageAdded(this));
    }

}
