package app.taxi.newtaxi;


public class Data_message {

    private String URL;
    private String id;
    private String comment;
    private String time;
    private String INDEX;

    public Data_message(){}

    public Data_message(String INDEX, String URL, String id, String comment, String time) {
        this.INDEX = INDEX;
        this.URL = URL;
        this.id = id;
        this.comment = comment;
        this.time = time;
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
}