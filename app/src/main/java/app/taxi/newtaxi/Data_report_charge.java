package app.taxi.newtaxi;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Data_report_charge {
    String time;
    String point;
    Data_report_charge(){ }

    Data_report_charge(String time,String point){
        this.time = time;
        this.point=point;
    }
    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("time", time);
        result.put("point", point);

        return result;
    }
    public String getPoint() {
        return point;
    }
    public String getTime() {
        return time;
    }
}
