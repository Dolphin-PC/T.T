package com.example.pcy.newtaxi;

public class PostData {
    private String UserID;
    private String Title;
    private String Start;
    private String Arrive;
    private int Person;
    private int index;
    private int Point;
    private int Pay;
    private String driver;
    private String phonenumber;
    private String taxinumber;
    public PostData(){}


    public int getPay() {
        return Pay;
    }

    public void setPay(int pay) {
        Pay = pay;
    }

    public PostData(String UserID, String Title, String Start, String Arrive, int Person, int index, int point, int pay, String driver, String phonenumber, String taxinumber){
        this.UserID=UserID;
        this.Title=Title;
        this.Start=Start;
        this.Arrive=Arrive;
        this.Person=Person;
        this.index=index;
        this.Point=point;
        this.driver = driver;
        this.phonenumber=phonenumber;
        this.taxinumber=taxinumber;
        this.Pay = pay;
    }
    public String getTitle(){ return Title; }
    public String getStart(){ return Start; }
    public String getArrive(){ return Arrive; }
    public int getPerson(){ return Person; }
    public int getIndex(){return index;}
    public String getUserID() {
        return UserID;
    }
    public int getPoint() {
        return Point;
    }

    public String getTaxinumber() {
        return taxinumber;
    }

    public void setTaxinumber(String taxinumber) {
        this.taxinumber = taxinumber;
    }

    public String getPhonenumber() {

        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }


    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }


    public void setTitle(String Title){this.Title=Title;}
    public void setStart(String Start){this.Start=Start;}
    public void setArrive(String Arrive){this.Arrive=Arrive;}
    public void setPerson(int Person){this.Person=Person;}
    public void setIndex(){this.index=index;}
    public void setUserID(String userID) {
        UserID = userID;
    }
    public void setPoint(int point) { Point = point; }
}
