package app.taxi.newtaxi;

public class Data_Taxi {
    private String email;
    private String driver;
    private String taxiphonenumber;
    private String taxinumber;
    private int pay, pay_complete, service_each, person,yes,no;
    private boolean complete_driver, complete_client,complete_ride,call;

    public Data_Taxi() {
    }

    public Data_Taxi(String email, String driver, String taxiphonenumber, String taxinumber, int pay, int pay_complete, int service_each
            , boolean complete_driver, boolean complete_client,boolean complete_ride,boolean call, int person,int yes,int no) {
        this.email = email;
        this.driver = driver;
        this.taxiphonenumber = taxiphonenumber;
        this.taxinumber = taxinumber;
        this.pay = pay;
        this.pay_complete = pay_complete;
        this.service_each = service_each;
        this.complete_driver = complete_driver;
        this.complete_client = complete_client;
        this.complete_ride = complete_ride;
        this.call = call;
        this.person = person;
        this.yes = yes;
        this.no = no;
    }

    public int getYes() {
        return yes;
    }

    public int getNo() {
        return no;
    }

    public void setYes(int yes) {
        this.yes = yes;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public void setCall(boolean call) {
        this.call = call;
    }

    public boolean getCall(){ return call;}

    public void setComplete_ride(boolean complete_ride) {
        this.complete_ride = complete_ride;
    }
    public boolean getComplete_ride(){
        return complete_ride;
    }

    public String getEmail() {
        return email;
    }

    public String getDriver() {
        return driver;
    }

    public String getTaxiPhonenumber() {
        return taxiphonenumber;
    }

    public String getTaxinumber() {
        return taxinumber;
    }

    public int getPay() {
        return pay;
    }

    public Boolean getComplete_Driver() {
        return complete_driver;
    }

    public Boolean getComplete_Client() {
        return complete_client;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public void setTaxinumber(String taxinumber) {
        this.taxinumber = taxinumber;
    }

    public void setPhonenumber(String taxiphonenumber) {
        this.taxiphonenumber = taxiphonenumber;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getPay_complete() {
        return pay_complete;
    }

    public int getService_each() {
        return service_each;
    }

    public void setPay_complete(int pay_complete) {
        this.pay_complete = pay_complete;
    }

    public void setService_each(int service_each) {
        this.service_each = service_each;
    }


}
