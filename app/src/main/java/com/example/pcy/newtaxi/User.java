package com.example.pcy.newtaxi;

public class User {

    public String username;
    public String password;
    public String email;
    public String phonenumber;
    public int point;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String password, String email, String phonenumber, int point) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phonenumber = phonenumber;
        this.point = point;
    }
    public String getUsername(){return username;}
    public String getEmail(){return email;}
    public String getPassword(){return password;}
    public String getPhonenumber(){return phonenumber;}
    public int getPoint(){return point;}
    public void setUsername(){this.username=username;}
    public void setEmail(){this.email=email;}
    public void setPassword(){this.password=password;}
    public void setPhonenumber(){this.phonenumber=phonenumber;}
    public void setPoint(){this.point=point;}

}