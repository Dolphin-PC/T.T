package app.taxi.newtaxi;

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