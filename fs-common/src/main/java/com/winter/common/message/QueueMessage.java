package com.winter.common.message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class QueueMessage<T> {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String messageId;

    private T messageData;

    private String timestamp;

    public String getMessageId() {
        return messageId;
    }

    public T getMessageData() {
        return messageData;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public QueueMessage(T messageData) {
        this.timestamp = LocalDateTime.now().format(dateTimeFormatter);
        this.messageId = String.valueOf(UUID.randomUUID());
        this.messageData = messageData;
    }
}
