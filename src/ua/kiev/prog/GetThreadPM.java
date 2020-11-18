package ua.kiev.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetThreadPM implements Runnable {
    private Gson gson;
    private int n;
    private User user;

    public GetThreadPM(User user) {
        gson = new GsonBuilder().create();
        this.user = user;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                URL url = new URL(Utils.getURL() + "/privatemessage?from=" + n + "&user=" + user.getLogin());
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream is = http.getInputStream();
                try {
                    byte[] buf = Helper.responseBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);

                    JsonPrivateMessages list = gson.fromJson(strBuf, JsonPrivateMessages.class);
                    if (list != null) {
                        for (Message m : list.getList()) {
                            System.out.println(m);
                            n++;
                        }
                    }
                } finally {
                    is.close();
                }

                Thread.sleep(500);
            }
        } catch (Exception ex) {
            System.out.println("Exit Private Message");
        }
    }
}
