package com.example.spacechat;

public class Messages {

    private String type,from,Message;

    public Messages(){

    }

    public Messages(String type, String from, String Message) {
        this.type = type;
        this.from = from;
        this.Message = Message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }
}
