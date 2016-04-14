package com.example.android.bustracker_acg.database;

/**
 * An alarm Data Access Object
 * (so we can manipulate the result of a select query)
 */


public class AlarmDAO {

    // ID
    private int ID;
    // time
    private String time;
    // state
    private int state;



    /*
        Constructors
    */
    // Constructor: Empty
    public AlarmDAO(){}

    // Constructor: ID, nameGR, nameENG, school
    public AlarmDAO(int ID, String time, int state){
        this.ID = ID;
        this.time = time;
        this.state = state;
    }

    /*
        Setters & Getters
    */
    public int getID(){
        return this.ID;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    public String getTime(){
        return this.time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public int getState(){
        return this.state;
    }

    public void setState(int state){
        this.state = state;
    }


}