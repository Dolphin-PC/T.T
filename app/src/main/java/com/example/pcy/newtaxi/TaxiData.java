package com.example.pcy.newtaxi;

public class TaxiData {
    private String email;
    private String driver;
    private String phonenumber;
    private String taxinumber;
    private int point;

    public TaxiData(){ }

    public TaxiData(String email,String driver,String phonenumber, String taxinumber,int point) {
        this.email = email;
        this.driver = driver;
        this.phonenumber = phonenumber;
        this.taxinumber = taxinumber;
        this.point = point;
    }

    public String getEmail() { return email; }
    public String getDriver() { return driver;}
    public String getPhonenumber() { return phonenumber;}
    public String getTaxinumber() { return taxinumber; }
    public int getPoint() { return point; }

    public void setEmail(String email) { this.email = email;}
    public void setPoint(int point) { this.point = point; }
    public void setTaxinumber(String taxinumber) { this.taxinumber = taxinumber; }
    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; }
    public void setDriver(String driver) { this.driver = driver; }
}
