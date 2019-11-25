package app.taxi.newtaxi;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Data_TaxiDriver {
    private String ID,PW,NAME,NUMBER,PHONENUMBER;
    private int POINT;
    private boolean AUTH,CALL;
    public Data_TaxiDriver(){ }

    public Data_TaxiDriver(String ID,String PW,String NAME,String NUMBER,int POINT,String PHONENUMBER,boolean AUTH,boolean CALL){
        this.ID = ID;
        this.PW = PW;
        this.NAME = NAME;
        this.NUMBER = NAME;
        this.POINT = POINT;
        this.PHONENUMBER = PHONENUMBER;
        this.AUTH = AUTH;
        this.CALL = CALL;
    }
    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ID", ID);
        result.put("PW", PW);
        result.put("NAME", NAME);
        result.put("NAME", NAME);
        result.put("POINT", POINT);
        result.put("PHONENUMBER", PHONENUMBER);
        result.put("AUTH", AUTH);
        result.put("CALL", CALL);

        return result;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getID() {
        return ID;
    }

    public String getNUMBER() {
        return NUMBER;
    }

    public int getPOINT() {
        return POINT;
    }

    public String getPW() {
        return PW;
    }

    public String getPHONENUMBER() {
        return PHONENUMBER;
    }
    public boolean getCALL(){ return CALL;}
    public void setID(String ID) {
        this.ID = ID;
    }

    public void setNUMBER(String NUMBER) {
        this.NUMBER = NUMBER;
    }

    public void setPOINT(int POINT) {
        this.POINT = POINT;
    }

    public void setPW(String PW) {
        this.PW = PW;
    }
    public boolean getAUTH(){ return AUTH;}

    public void setAUTH(boolean AUTH) {
        this.AUTH = AUTH;
    }

    public void setPHONENUMBER(String PHONENUMBER) {
        this.PHONENUMBER = PHONENUMBER;
    }

    public void setCALL(boolean CALL) {
        this.CALL = CALL;
    }
}
