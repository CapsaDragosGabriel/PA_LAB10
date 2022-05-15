package database;

import lombok.Data;
import models.Message;
import models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class NetworkData implements Serializable {
    public static List<User> userList = new ArrayList<>();
    public static List<Message> messages = new ArrayList<>();

    public static User findByName(String name) {
        for (User user : userList) {
            if (user.getName().equals(name))
                return user;
        }
        return null;
    }

}
