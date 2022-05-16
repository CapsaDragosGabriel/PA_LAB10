import database.NetworkData;
import models.Message;
import models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

class ClientThread extends Thread {
    private Socket socket = null;
    private User user;
    private Server server;

    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        if (Server.isRunning())
            while (this.socket != null) {
                PrintWriter out = null;
                try {
                    // System.out.println(this.server.connCount);
                    socket.setSoTimeout(10000);
                    // Get the request from the input stream: client → server
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    String request = in.readLine();
                    // Send the response to the oputput stream: server → client
                    out = new PrintWriter(socket.getOutputStream());
                    String raspuns = null;
                    if (request != null) {
                        if (request.equals("stop")) {
                            raspuns = "Server stopped";
                            out.println(raspuns);
                            out.flush();
                            this.server.closeServer();

                        } else if (request.length() == 4 && request.substring(0, 4).equals("exit")) {
                            this.server.connCount--;
                            if (this.user != null)
                                logoutUser();
                            try {
                                if (this.server.connCount == 0 && !Server.isRunning())
                                    this.server.getServerSocket().close();
                                socket.close(); // or use try-with-resources
                                this.socket = null;
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                            //Main.logoutUser();
                        } else raspuns = parseRequest(request);

                    }
                    if (raspuns != null)
                        out.println(raspuns);
                    out.flush();

                } catch (SocketTimeoutException e) {
                    System.out.println("Server timeout");

                    this.server.connCount--;
                    //System.out.println(server.connCount);
                    try {
                        this.server.closeServer();

                        if (this.user != null)
                            logoutUser();
                        if (server.connCount == 0) this.server.getServerSocket().close();
                        socket.close();
                        this.socket = null;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException e) {
                    System.err.println("Communication error... " + e);
                }

            }
    }

    private String parseRequest(String request) {

        if (request.length() > 9 && request.substring(0, 9).equals("register ")) {

            return registerUser(request.substring(9));
        } else if (request.length() > 6 && request.substring(0, 6).equals("login ")) {
            return loginUser(request.substring(6));
            //  return "Server recieved login request for the name: " + request.substring(6);
        } else if (request.length() > 7 && request.substring(0, 7).equals("friend ")) {
            return addFriends(request.substring(7));
            //return "Server recieved addFriends request from the user: " + request.substring(10);
        } else if (request.length() >= 5 && request.substring(0, 5).equals("send ")) {
            return send(request.substring(5));
            //return "Server recieved send request from the user: " + request.substring(5);
        } else if (request.length() == 4 && request.substring(0, 4).equals("read")) {
            return read();
            //return "Server recieved read request from the user: " + request.substring(5);
        } else
            return "This is not a valid command.";
    }

    private String registerUser(String substring) {
        if (this.user != null) {
            return "You are logged in with " + this.user.getName() + " and are not allowed to create other users";
        }
        if (NetworkData.userList != null)
            for (User user : NetworkData.userList) {
                if (user.getName().equals(substring))
                    return "User already registered";
            }

        NetworkData.userList.add(new User(substring));
        return "User " + substring + " will be registered";
    }

    private String loginUser(String substring) {
        if (NetworkData.userList == null) return "No users registered";
        User tempUser = NetworkData.findByName(substring);

        if (tempUser == null) {

            return "models.User not registered";
        }
        if (tempUser.isLogged()) {

            return "models.User already logged in";
        }
        if (this.user != null) {

            return "Logged with " + this.user;
        }
        this.user = tempUser;
        tempUser.setLogged(true);
        return "login user request";
    }

    private String addFriends(String substring) {
        if (this.user == null) {
            return "you re not logged in";
        }
        User tempUser = NetworkData.findByName(substring);
        if (tempUser == null) {
            return "that s not a registered user";
        }
        this.user.getFriendList().add(tempUser.getName());
        tempUser.getFriendList().add(this.user.getName());

        return "add friend request";

    }

    private String send(String substring) {
        if (this.user == null) {
            return "user not logged in";

        }
        if (this.user.getFriendList().isEmpty()) {
            return "user has no friends :(";
        }
        Message tempMessage = new Message(this.user, substring);
        NetworkData.messages.add(tempMessage);

        return "send request";
    }

    private String read() {
        if (this.user == null) {
            return "user not logged in";
        }
        //StringBuilder allMessages = new StringBuilder();
        String allMessages = "";
        if (NetworkData.messages == null)
            return "No messages on server";
        for (Message message : NetworkData.messages) {
            if (message.getRecievers().contains(this.user.getName()))
                allMessages = allMessages + "Message from " + message.getSender().getName() + ": " + message.getContent() + "\\      ";

        }
        if (allMessages != null) {
            return allMessages.toString();
        }
        return "No messages for you";
    }

    private void logoutUser() {
        this.user.setLogged(false);
    }
}