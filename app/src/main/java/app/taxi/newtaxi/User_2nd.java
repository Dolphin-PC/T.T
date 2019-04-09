package app.taxi.newtaxi;

public class User_2nd {

    public String username;
    public String password;
    public String email;
    public String phonenumber;
    public String profile_url;
    public User_2nd() {

    }

    public User_2nd(String username, String password, String email, String phonenumber,String profile_url) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phonenumber = phonenumber;
        this.profile_url = profile_url;
    }
    public String getUsername(){return username;}
    public String getEmail(){return email;}
    public String getPassword(){return password;}
    public String getPhonenumber(){return phonenumber;}
    public String getProfile_url() { return profile_url; }

    public void setProfile_url(String profile_url) { this.profile_url = profile_url; }
    public void setUsername(){this.username=username;}
    public void setEmail(){this.email=email;}
    public void setPassword(){this.password=password;}
    public void setPhonenumber(){this.phonenumber=phonenumber;}

}
