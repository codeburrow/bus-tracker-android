package com.example.android.bustracker_acg.database;

/**
 * Created by giorgos on 4/8/2016.
 */

/**
 *  A Faq Data Access Object
 */

public class FaqDAO {

    // ID - routeID
    private int ID;
    // Question in Greek
    private String questionGR;
    // Question in English
    private String questionENG;
    // Answer in Greek
    private String answerGR;
    // Answer in English
    private String answerENG;


    /**
     Constructors
     */
    // Constructor: Empty
    public FaqDAO(){}

    // Constructor: ID, nameGR, nameENG, school
    public FaqDAO(int ID, String questionENG, String questionGR, String answerENG, String answerGR){
        this.ID = ID;
        this.questionGR = questionGR;
        this.questionENG = questionENG;
        this.answerGR = answerGR;
        this.answerENG = answerENG;
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

    public String getQuestionGR(){
        return this.questionGR;
    }

    public void setQuestionGR(String questionGR){
        this.questionGR = questionGR;
    }

    public String getQuestionENG(){
        return this.questionENG;
    }

    public void setQuestionENG(String questionENG){
        this.questionENG = questionENG;
    }

    public String getAnswerGR(){
        return this.answerGR;
    }

    public void setAnswerGR(String answerGR){
        this.answerGR = answerGR;
    }

    public String getAnswerENG(){
        return this.answerENG;
    }

    public void setAnswerENG(String answerENG){
        this.answerENG = answerENG;
    }

}
