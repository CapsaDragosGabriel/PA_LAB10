package models;

import lombok.Data;
import models.User;

import java.io.Serializable;
import java.util.List;

@Data
public class Message implements Serializable {
    private User sender;
    private List<String> recievers;
    private String content;

    public Message(User sender, String content) {
        this.recievers = sender.getFriendList();
        this.sender = sender;
        this.content = content;
    }

    public Message() {
    }

    @Override
    public String toString() {
        return "Message from " + sender.getName() + ": " + content + '\n';
    }
}
