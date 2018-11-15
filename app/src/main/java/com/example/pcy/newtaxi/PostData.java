package com.example.pcy.newtaxi;

public class PostData {
    private String UserID;
    private String Title;
    private String Start;
    private String Arrive;
    private int Person;
    private int index;
    private int Point;
    public PostData(){}

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getPoint() {
        return Point;
    }

    public void setPoint(int point) {
        Point = point;
    }

    public PostData(String UserID, String Title, String Start, String Arrive, int Person, int index, int point){
        this.UserID=UserID;
        this.Title=Title;
        this.Start=Start;
        this.Arrive=Arrive;
        this.Person=Person;
        this.index=index;
        this.Point=point;

    }
    public String getTitle(){ return Title; }
    public String getStart(){ return Start; }
    public String getArrive(){ return Arrive; }
    public int getPerson(){ return Person; }
    public int getIndex(){return index;}
    public void setTitle(String Title){this.Title=Title;}
    public void setStart(String Start){this.Start=Start;}
    public void setArrive(String Arrive){this.Arrive=Arrive;}
    public void setPerson(int Person){this.Person=Person;}
    public void setIndex(){this.index=index;}
}
