package ru.mmaximov.smsalert.biz;

public class Message {
    private final String phone;
    private final String text;

    public Message(String phone, String text) {
        this.phone = phone;
        this.text = text;
    }

    public String getPhone() {
        return phone;
    }

    public String getText() {
        return text;
    }
}
