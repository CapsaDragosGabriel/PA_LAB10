package database;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import models.Message;
import models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class Network implements Serializable {
    public List<User> userList = new ArrayList<>();
    public List<Message> messages = new ArrayList<>();

    public void save() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Network network = new Network();
            network.setMessages(NetworkData.messages);
            network.setUserList(NetworkData.userList);
            mapper.writeValue(new File("E:\\AN2\\ProiectePA\\PA_LAB10\\database.json"), network);
        } catch (StreamWriteException e) {
            System.out.println("Problem saving the file.");
        } catch (DatabindException e) {

            System.out.println("Binding failed.");
        } catch (IOException e) {

            System.out.println("The save location is incorrect.");
        }

    }

    public void load() {
        Network network = new Network();
        System.out.println("LOADING DB");
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new FileInputStream("E:\\AN2\\ProiectePA\\PA_LAB10\\database.json");
            network = mapper.readValue(inputStream, Network.class);
            this.messages = network.getMessages();
            this.userList = network.getUserList();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("The location is incorrect.");
        }
        NetworkData.userList = network.getUserList();
        NetworkData.messages = network.getMessages();
    }

    @Override
    public String toString() {
        return "database.Network{" +
                "userList=" + userList +
                ", messages=" + messages +
                '}';
    }
}
