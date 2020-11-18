package ua.kiev.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatRoom {
    private String name;
    private User creator;
    private List<User> users = new ArrayList<>();
    private List<Message> messages = new LinkedList<>();

    public ChatRoom(String name, User creator) {
        this.name = name;
        this.creator = creator;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Chat name: ").append(name).append(", chat Admin: ").append(creator.getLogin()).append(", Users in chat: ").append(users.size()).toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean enterChat(User userEnter) {
        for (User user : users) {
            if (user.getLogin().equals(userEnter.getLogin())) {
                return false;
            }
        }
        users.add(userEnter);
        return true;
    }

    public boolean leaveChat(User userLeave) {
        for (User user : users) {
            if (user.getLogin().equals(userLeave.getLogin())) {
                users.remove(user);
                return true;
            }
        }
        return false;
    }

    public String toJSON() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public static ChatRoom fromJSON(String s) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(s, ChatRoom.class);
    }
}
