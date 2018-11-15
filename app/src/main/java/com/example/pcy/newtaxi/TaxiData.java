package com.example.pcy.newtaxi;

public class TaxiData {
    private String email;
    private String drivername;
    private String phonenumber;
    private String taxinumber;
    private int point;

    public TaxiData(){ }

    public TaxiData(String email,String drivername,String phonenumber, String taxinumber,int point) {
        this.email = email;
        this.drivername = drivername;
        this.phonenumber = phonenumber;
        this.taxinumber = taxinumber;
        this.point = point;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
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

    public String getDrivername() {

        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
