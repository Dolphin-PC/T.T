package app.taxi.newtaxi;

public class Data_TaxiDriver {
    private String ID,PW,NAME,NUMBER,PHONENUMBER;
    private int POINT;
    private boolean AUTH,CALL;
    public Data_TaxiDriver(){ }

    public Data_TaxiDriver(String ID,String PW,String NAME,String NUMBER,int POINT,String PHONENUMBER,boolean AUTH,boolean CALL){
        this.ID = ID;
        this.PW = PW;
        this.NAME = NAME;
        this.NUMBER = NUMBER;
        this.POINT = POINT;
        this.PHONENUMBER = PHONENUMBER;
        this.AUTH = AUTH;
        this.CALL = CALL;
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
