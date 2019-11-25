package app.taxi.newtaxi;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

class Data_exit {
    String day, menu, reporter, ID, name,index;
    int yes, no;

    Data_exit() {
    }

    Data_exit(String day, String menu, String reporter, String ID, String name, String index,
              int yes, int no) {
        this.day = day;
        this.menu = menu;
        this.index = index;
        this.reporter = reporter;
        this.ID = ID;
        this.name = name;
        this.yes = yes;
        this.no = no;
    }
    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("day", day);
        result.put("menu", menu);
        result.put("index", index);
        result.put("reporter", reporter);
        result.put("ID", ID);
        result.put("name", name);
        result.put("yes", yes);
        result.put("no", no);

        return result;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getNo() {
        return no;
    }

    public int get_yes() {
        return yes;
    }

    public String getDay() {
        return day;
    }

    public String getID() {
        return ID;
    }

    public String getMenu() {
        return menu;
    }

    public String getReporter() {
        return reporter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setID(String id) {
        this.ID = ID;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public void setYes(int yes) {
        this.yes = yes;
    }

}