package app.taxi.newtaxi;

public class Data_Post {
    private String price,UserID,Title,Start,Arrive,Start_Latitude,Start_Longitude,Arrive_Latitude,Arrive_Longitude,driver,phonenumber,taxinumber,index,time,distance;
    private int Person,Pay,MaxPerson;
    public Data_Post(){}

    public Data_Post(String UserID, String price,String Title, String Start,String Start_Latitude,String Start_Longitude, String Arrive, String Arrive_Latitude,String Arrive_Longitude, int Person,int MaxPerson, String index,String distance, int pay, String time, String driver, String phonenumber, String taxinumber){
        this.UserID=UserID;
        this.Title=Title;
        this.Start=Start;
        this.Arrive=Arrive;
        this.Start_Latitude=Start_Latitude;
        this.Start_Longitude=Start_Longitude;
        this.Arrive_Latitude=Arrive_Latitude;
        this.Arrive_Longitude=Arrive_Longitude;
        this.Person=Person;
        this.MaxPerson=MaxPerson;
        this.index=index;
        this.price=price;
        this.driver = driver;
        this.phonenumber=phonenumber;
        this.taxinumber=taxinumber;
        this.Pay = pay;
        this.time = time;
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTitle(){ return Title; }
    public String getStart(){ return Start; }
    public String getArrive(){ return Arrive; }
    public int getPerson(){ return Person; }
    public String getIndex(){return index;}
    public String getUserID() {
        return UserID;
    }
    public String getPrice() {
        return price;
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
    public String getPhonenumber() { return phonenumber; }
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
    public String getArrive_Latitude() {
        return Arrive_Latitude;
    }

    public String getArrive_Longitude() {
        return Arrive_Longitude;
    }

    public String getStart_Latitude() {
        return Start_Latitude;
    }

    public String getStart_Longitude() {
        return Start_Longitude;
    }

    public void setArrive_Latitude(String arrive_Latitude) {
        Arrive_Latitude = arrive_Latitude;
    }

    public void setArrive_Longitude(String arrive_Longitude) {
        Arrive_Longitude = arrive_Longitude;
    }

    public void setStart_Latitude(String start_Latitude) {
        Start_Latitude = start_Latitude;
    }

    public void setStart_Longitude(String start_Longitude) {
        Start_Longitude = start_Longitude;
    }
}
