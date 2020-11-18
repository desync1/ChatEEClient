package ua.kiev.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class UserList {
    private static UserList userList = new UserList();
    private List<User> users = new ArrayList<>();

    public static UserList getInstance() {
        return userList;
    }

    public void getUser() {
        System.out.println("Users in chat");
        for (User user : users) {
            System.out.println("User login: " + user.getLogin() + ", User status: " + user.getAvailable());
        }
    }

    public static UserList fromJSON(String s) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(s, UserList.class);
    }

}
