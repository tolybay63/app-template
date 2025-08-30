package kz.kis.kisfilestorage.event;

import org.springframework.context.ApplicationEvent;

public class QueueMessageAdded extends ApplicationEvent {
    public QueueMessageAdded(Object source) {
        super(source);
    }
}
