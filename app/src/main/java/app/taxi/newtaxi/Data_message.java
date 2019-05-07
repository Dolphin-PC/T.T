package app.taxi.newtaxi;


public class Data_message {

    private String URL;
    private String id;
    private String comment;
    private String time;
    private String INDEX,username;

    public Data_message(){}

    public Data_message(String INDEX, String URL, String id,String username, String comment, String time) {
        this.INDEX = INDEX;
        this.URL = URL;
        this.id = id;
        this.comment = comment;
        this.time = time;
        this.username = username;
    }

    public String getPROFILEURL() {
        return URL;
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