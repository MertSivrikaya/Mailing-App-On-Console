package model;

import java.sql.Timestamp;

public class Message {
    private final User sender;
    private final User receiver;
    private final String title;
    private final String content;
    private final Timestamp time;

    public Message(User sender, User receiver, String title, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.content = content;
        this.time = new Timestamp(System.currentTimeMillis());
    }

    public Message(User sender, User receiver, String title, String content, Timestamp time) {
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.content = content;
        this.time = time;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTime() {
        return time;
    }
}
