package com.example.guftagoochatapp.Model;

public class Chatlist {
    public String id;
    public String otherid;

    public Chatlist(String id, String otherid) {
        this.id = id;
        this.otherid = otherid;
    }
    public Chatlist() {
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getOtherid() {
        return otherid;
    }
    public void setOtherid(String otherid) {
        this.otherid = otherid;
    }
}
