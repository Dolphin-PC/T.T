package app.taxi.newtaxi;

public class Data_report_charge {
    String time;
    String point;
    Data_report_charge(){ }

    Data_report_charge(String time,String point){
        this.time = time;
        this.point=point;
    }
    public String getPoint() {
        return point;
    }
    public String getTime() {
        return time;
    }
}
