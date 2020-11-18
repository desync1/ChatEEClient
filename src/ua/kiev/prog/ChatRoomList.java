package ua.kiev.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomList {
    private static ChatRoomList chatRoomList = new ChatRoomList();
    private List<ChatRoom> list = new ArrayList<>();

    public static ChatRoomList getInstance() {
        return chatRoomList;
    }


    public void getChatRoomList() {
        for (ChatRoom chatRoom : list) {
            System.out.println(chatRoom);
        }
    }

    public static ChatRoomList fromJSON(String s) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(s, ChatRoomList.class);
    }
}
