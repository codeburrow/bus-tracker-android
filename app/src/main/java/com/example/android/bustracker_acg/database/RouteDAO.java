package com.example.android.bustracker_acg.database;

/**
 * A route Data Access Object
 * (so we can manipulate the result of a select query)
 */


public class RouteDAO {

    // ID - routeID
    private int ID;
    // name in English
    private String nameENG;
    // name in Greek
    private String nameGR;
    // school
    private String school;


    /**
        Constructors
    */
    // Constructor: Empty
    public RouteDAO(){}

    // Constructor: ID, nameGR, nameENG, school
    public RouteDAO(int ID, String nameENG, String nameGR, String school){
        this.ID = ID;
        this.nameENG = nameENG;
        this.nameGR = nameGR;
        this.school = school;
    }

    /**
        Setters & Getters
    */
    public int getID(){
        return this.ID;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    public String getNameENG(){
        return this.nameENG;
    }

    public void setNameENG(String nameENG){
        this.nameENG = nameENG;
    }

    public String getNameGR(){
        return this.nameGR;
    }

    public void setNameGR(String nameGR){
        this.nameGR = nameGR;
    }

    public String getSchool(){
        return this.school;
    }

    public void setSchool(String school){
        this.school = school;
    }

}