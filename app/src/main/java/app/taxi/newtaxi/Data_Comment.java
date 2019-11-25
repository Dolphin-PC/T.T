package app.taxi.newtaxi;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Data_Comment {
    public String userID;
    public String comment;
    public String cmt;
    public int index;

    public Data_Comment(){}

    public Data_Comment(String userID, String comment, int index) {
        this.userID=userID;
        this.comment = comment;
        this.index = index;
    }
    public Data_Comment(String cmt, int index){
        this.comment = cmt;
        this.index = index;
    }
    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("comment", comment);
        result.put("index", index);

        return result;
    }

    public String getCmt() {
        return cmt;
    }

    public void setCmt(String cmt) {
        this.cmt = cmt;
    }

    public void setIndex() {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getComment() {
        return comment;
    }
    public String getuserID() {
        return userID;
    }
    public void setuserID(String userID) {
        this.userID = userID;
    }

}
