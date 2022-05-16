package models;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class User implements Serializable {
    private String name;
    private List<String> friendList = new ArrayList<>();
    private boolean isLogged = false;

    public User(String name) {
        this.name = name;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", friendList=" + friendList +
                ", isLogged=" + isLogged +
                '}';
    }
}
