package ua.kiev.prog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            User user = auth();
            if (user == null) {
                return;
            }
            mainMenu(user);


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            sc.close();
        }

    }

    public static User auth() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome!");
        System.out.println("Enter login: ");
        String login = sc.nextLine();
        System.out.println("Enter password: ");
        String password = sc.nextLine();
        User user = new User(login, password);
        String json = user.toJSON();
        if ((Helper.sendPOST("/auth", json)) == 200) {
            return user;
        }
        return null;
    }

    public static void mainMenu(User user) throws IOException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("-------------------------------");
            System.out.println("Choose your action");
            System.out.println("Main chat - 1");
            System.out.println("User list - 2");
            System.out.println("Set status - 3");
            System.out.println("Get user status - 4");
            System.out.println("Private message - 5");
            System.out.println("Create new chat room - 6");
            System.out.println("Enter chat room - 7");
            System.out.println("Leave chat room - 8");
            System.out.println("Delete chat room - 9");
            System.out.println("Chat room list - 10");
            System.out.println("Exit - 0");
            System.out.println("-------------------------------");
            int res = sc.nextInt();
            sc.nextLine();
            switch (res) {
                case 0:
                    System.out.println("Logout success");
                    return;
                case 1:
                    mainChat(user);
                    continue;
                case 2:
                    userList();
                    continue;
                case 3:
                    setUserStatus(user);
                    continue;
                case 4:
                    getUserStatus();
                    continue;
                case 5:
                    privateMessage(user);
                    continue;
                case 6:
                    createRoom(user);
                    continue;
                case 7:
                    chatRoom(user);
                    continue;
                case 8:
                    leaveRoom(user);
                    continue;
                case 9:
                    deleteRoom(user);
                    continue;
                case 10:
                    getRoomList();
            }
        }
    }

    public static void mainChat(User user) throws IOException {
        Scanner sc = new Scanner(System.in);
        Thread th = new Thread(new GetThread());
        th.setDaemon(true);
        th.start();

        System.out.println("Enter your message: ");
        while (true) {
            String text = sc.nextLine();
            if (text.isEmpty()) {
                th.interrupt();
                break;
            }

            Message m = new Message(user.getLogin(), text);
            int res = m.send(Utils.getURL() + "/add");

            if (res != 200) { // 200 OK
                System.out.println("HTTP error occured: " + res);
                return;
            }
        }
    }


    public static void userList() throws IOException {
        URL url = new URL(Utils.getURL() + "/users");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        InputStream is = http.getInputStream();
        try {
            byte[] buf = Helper.responseBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);

            UserList userList = UserList.fromJSON(strBuf);
            userList.getUser();
        } finally {
            is.close();
        }
    }


    public static void setUserStatus(User user) throws IOException {
        Scanner sc = new Scanner(System.in);
        URL obj = new URL(Utils.getURL() + "/userstatus");
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        System.out.println("Set your status available/notAvailable ");
        String status = sc.nextLine();
        String urlParameters = "user=" + user.toJSON() + "&status=" + status;

        OutputStream os = conn.getOutputStream();
        try {
            os.write(urlParameters.getBytes(StandardCharsets.UTF_8));
            if (conn.getResponseCode() == 200) {
                System.out.println("Status change");
            } else {
                System.out.println("Error");
            }
        } finally {
            os.close();
        }
    }

    public static void getUserStatus() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Input user login");
        String login = sc.nextLine();
        URL obj = new URL(Utils.getURL() + "/userstatus?user=" + login);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        InputStream is = conn.getInputStream();
        try {
            byte[] buf = Helper.responseBodyToArray(is);
            String response = new String(buf, StandardCharsets.UTF_8);
            System.out.println("User " + login + " status is: " + response);
        } finally {
            is.close();
        }

    }

    public static void privateMessage(User user) throws IOException {
        Scanner sc = new Scanner(System.in);
        Thread th = new Thread(new GetThreadPM(user));
        th.setDaemon(true);
        th.start();

        System.out.println("Enter your message: ");
        while (true) {
            String text = sc.nextLine();

            if (text.isEmpty()) {
                th.interrupt();
                break;
            }
            System.out.println("Enter recipient:");
            String recipient = sc.nextLine();

            Message m = new Message(user.getLogin(), recipient, text);
            int res = m.send(Utils.getURL() + "/add");
            if (res == 200) { // 200 OK
                System.out.println("Message sent to " + recipient);
                return;
            } else {
                System.out.println("HTTP error occured: " + res);
                return;
            }
        }
    }

    public static void createRoom(User user) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter chat room name: ");
        String roomName = sc.nextLine();
        ChatRoom room = new ChatRoom(roomName, user);

        URL obj = new URL(Utils.getURL() + "/createroom");
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        try {
            os.write(room.toJSON().getBytes(StandardCharsets.UTF_8));
            if (conn.getResponseCode() == 200) {
                System.out.println("Room create");
            } else {
                System.out.println("Error");
            }
        } finally {
            os.close();
        }

    }

    public static void chatRoom(User user) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter room name: ");
        String roomName = sc.nextLine();
        Thread th = new Thread(new GetThreadRoom(roomName));
        th.setDaemon(true);
        th.start();


        System.out.println("Enter your message: ");
        while (true) {
            String text = sc.nextLine();

            if (text.isEmpty()) {
                th.interrupt();
                break;
            }
            Message m = new Message(user.getLogin(), roomName, text);
            int res = m.send(Utils.getURL() + "/addroommessage");
            if (res != 200) { // 200 OK
                System.out.println("HTTP error occured: " + res);
                return;
            }
        }
    }

    public static void leaveRoom(User user) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter room name: ");
        String roomName = sc.nextLine();
        String userName = user.getLogin();
        URL url = new URL(Utils.getURL() + "/leaveroom?roomname=" + roomName + "&username=" + userName);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() == 200) {
            System.out.println("Leave success");
        } else {
            System.out.println(conn.getResponseCode());
        }
    }

    public static void deleteRoom(User user) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter room name: ");
        String roomName = sc.nextLine();
        String userName = user.getLogin();
        URL url = new URL(Utils.getURL() + "/deleteroom?roomname=" + roomName + "&username=" + userName);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() == 200) {
            System.out.println("delete success");
        } else {
            System.out.println(conn.getResponseCode());
        }

    }


    public static void getRoomList() throws IOException {

        URL url = new URL(Utils.getURL() + "/roomlist");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        InputStream is = http.getInputStream();
        try {
            byte[] buf = Helper.responseBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            ChatRoomList chatRoomList = ChatRoomList.fromJSON(strBuf);
            chatRoomList.getChatRoomList();
        } finally {
            is.close();
        }
    }
}
