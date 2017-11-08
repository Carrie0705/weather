package com.example.carrie0705.bean;

/**
 * Created by Carrie0705 on 2017/10/25.
 */

public class City {
    private String province;
    private String city;
    private String number;
    private String fisrtPY;
    private String allPY;
    private String allfirstPY;

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getNumber() {
        return number;
    }

    public String getFisrtPY() {
        return fisrtPY;
    }

    public String getAllPY() {
        return allPY;
    }

    public String getAllfirstPY() {
        return allfirstPY;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setFisrtPY(String fisrtPY) {
        this.fisrtPY = fisrtPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public void setAllfirstPY(String allfirstPY) {
        this.allfirstPY = allfirstPY;
    }

    public City(String province, String city, String number, String fisrtPY, String allPY, String allfirstPY) {
        this.province = province;
        this.city = city;
        this.number = number;
        this.fisrtPY = fisrtPY;
        this.allPY = allPY;
        this.allfirstPY = allfirstPY;
    }
}
