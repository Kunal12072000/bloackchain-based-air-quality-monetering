package com.pollution.pollutionApp.pojo;

import java.io.Serializable;

public class ProfilePojo implements Serializable {
    public long contact;
    public String
            email;
    public String name;
    public String pass;
    public int age;
    public double ht;
    public double wt;
    public String gender;
    public String allergic;

    public ProfilePojo() {
    }

    public ProfilePojo(String pass, long contact, String name, String email) {
        this.contact = contact;
        this.email = email;
        this.name = name;
        this.pass = pass;
    }

    public ProfilePojo(String allergic, String gender, String pass, long contact, String name, double ht, double wt, int age, String email) {
        this.contact = contact;
        this.email = email;
        this.name = name;
        this.pass = pass;
        this.age = age;
        this.ht = ht;
        this.wt = wt;
        this.gender = gender;
        this.allergic = allergic;
    }
}