package app.taxi.newtaxi;

public class Data_Members {
    private String USER1;
    private String PROFILEURL;
    private String GENDER;
    private int INDEX;
    private String USERID;

    public Data_Members(){}

    public Data_Members(String USER1, int INDEX, String PROFILEURL, String GENDER,String USERID){
        this.USER1= USER1;
        this.INDEX = INDEX;
        this.PROFILEURL=PROFILEURL;
        this.GENDER=GENDER;
        this.USERID = USERID;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public int getINDEX() {
        return INDEX;
    }
    public void setINDEX(int INDEX) {
        this.INDEX = INDEX;
    }
    public void setUSER1(String USER1) {
        this.USER1 = USER1;
    }

    public String getUSER1() {
        return USER1;
    }

    public String getGENDER() {
        return GENDER;
    }

    public String getPROFILEURL() {
        return PROFILEURL;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public void setPROFILEURL(String PROFILEURL) {
        this.PROFILEURL = PROFILEURL;
    }
}
