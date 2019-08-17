package app.taxi.newtaxi;

public class User {

    public String username;
    public String password;
    public String email;
    public String phonenumber;
    public int point,penalty_point,penalty;
    public String profile_url;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String password, String email, String phonenumber, int point,String profile_url, int penalty,int penalty_point) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phonenumber = phonenumber;
        this.point = point;
        this.profile_url = profile_url;
        this.penalty = penalty;
        this.penalty_point = penalty_point;
    }
    public String getUsername(){return username;}
    public String getEmail(){return email;}
    public String getPassword(){return password;}
    public String getPhonenumber(){return phonenumber;}
    public int getPoint(){return point;}
    public String getProfile_url() { return profile_url; }
    public int getPenalty() {
        return penalty;
    }

    public int getPenalty_point() {
        return penalty_point;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public void setPenalty_point(int penalty_point) {
        this.penalty_point = penalty_point;
    }

    public void setProfile_url(String profile_url) { this.profile_url = profile_url; }
    public void setUsername(){this.username=username;}
    public void setEmail(){this.email=email;}
    public void setPassword(){this.password=password;}
    public void setPhonenumber(){this.phonenumber=phonenumber;}
    public void setPoint(){this.point=point;}

}