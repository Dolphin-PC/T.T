package app.taxi.newtaxi;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Data_message {

    private String profileurl;
    private String id;
    private String comment;
    private String time;
    private String INDEX, username;

    public Data_message() {
    }

    public Data_message(String INDEX, String profileurl, String id, String username, String comment, String time) {
        this.INDEX = INDEX;
        this.profileurl = profileurl;
        this.id = id;
        this.comment = comment;
        this.time = time;
        this.username = username;
    }
    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("INDEX", INDEX);
        result.put("profileurl", profileurl);
        result.put("id", id);
        result.put("comment", comment);
        result.put("time", time);
        result.put("username", username);

        return result;
    }

    public String getPROFILEURL() {
        return profileurl;
    }

    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getTime() {
        return time;
    }

    public String getINDEX() {
        return INDEX;
    }

    public String getUsername() {
        return username;
    }

}

