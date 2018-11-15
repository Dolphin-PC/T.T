package com.example.pcy.newtaxi;

public class CommentData {
    public String userID;
    public String comment;
    public int index;

    public CommentData(){}

    public String getuserID() {
        return userID;
    }

    public void setuserID(String userID) {
        this.userID = userID;
    }

    public CommentData(String userID, String comment, int index) {
        this.userID=userID;
        this.comment = comment;
        this.index = index;

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
}
