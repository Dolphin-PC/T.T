package app.taxi.newtaxi;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Data_Members {
    private String USER1,PROFILEURL,GENDER,INDEX,USERID;
    private boolean JOIN,PAY;

    public Data_Members(){}

    public Data_Members(String USER1, String INDEX, String PROFILEURL, String GENDER,String USERID,boolean JOIN,boolean PAY){
        this.USER1= USER1;
        this.INDEX = INDEX;
        this.PROFILEURL=PROFILEURL;
        this.GENDER=GENDER;
        this.USERID = USERID;
        this.JOIN = JOIN;
        this.PAY = PAY;
    }
    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("USER1", USER1);
        result.put("INDEX", INDEX);
        result.put("PROFILEURL", PROFILEURL);
        result.put("GENDER", GENDER);
        result.put("USERID", USERID);
        result.put("JOIN", JOIN);
        result.put("PAY", PAY);

        return result;
    }

    public String getUSERID() {
        return USERID;
    }
    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String getINDEX() {
        return INDEX;
    }
    public void setINDEX(String INDEX) {
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

    public void setJOIN(boolean JOIN) {
        this.JOIN = JOIN;
    }
    public boolean getJOIN(){
        return JOIN;
    }

    public void setPAY(boolean PAY) {
        this.PAY = PAY;
    }
    public boolean getPAY(){return PAY;}
}
