package app.taxi.newtaxi;

public class Data_Post {
    private String UserID;
    private String Title;
    private String Start;
    private String Arrive;
    private int Person;
    private int index;
    private int Point;
    private int Pay;
    private int MaxPerson;
    private String driver;
    private String phonenumber;
    private String taxinumber;
    public Data_Post(){}

    public Data_Post(String UserID, String Title, String Start, String Arrive, int Person,int MaxPerson, int index, int point, int pay, String driver, String phonenumber, String taxinumber){
        this.UserID=UserID;
        this.Title=Title;
        this.Start=Start;
        this.Arrive=Arrive;
        this.Person=Person;
        this.MaxPerson=MaxPerson;
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
    public int getPay() { return Pay; }

    public int getMaxPerson() {
        return MaxPerson;
    }

    public void setMaxPerson(int maxPerson) {
        MaxPerson = maxPerson;
    }

    public void setPay(int pay) {
        this.Pay = pay;
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
