/*
 * Copyright (c) 2018, Sharon Hains. CMPUT 301. University of Alberta - All Rights Reserved. You may use, distribute, or modify this code under the terms and conditions fo the Code of Student Behaviour at the University of Alberta.
 */

package com.example.sharonhains.hains_subbook;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Sharon Hains on 2018-01-23.
 */


/**
 * Represents a Subscription Object
 * @author hains
 * @version 1
 */
public class Subscription {

    private String name;
    private String date;
    private String comment;
    private double charge;
    private String subListString;

    /**
     * Creates a Subscription object
     * @param name Subscription name
     * @param charge Monthly subscription price
     * @param comment Any user defined comment
     * @param subdate Date of the subscription
     */
    public Subscription (String name, double charge, String comment, String subdate){
        this.name = name;
        this.date = subdate;
        this.comment = comment;
        this.charge = charge;
    }

    /**
     * Returns the name from the Subscription object
     * @return name
     */
    public String getName()     { return name; }

    /**
     * Sets the name of the Subscription object, throws exception if length is >20 characters
     * @param name Subscription name
     * @throws NameTooLongException Name length too long
     */
    public void setName(String name) throws NameTooLongException {

        if (name.length() > 20){
            throw new NameTooLongException();
        }
        this.name = name;
    }

    /**
     * Returns the date from the Subscription object
     * @return date
     */
    public String getDate()     { return date; }

    /**
     * Sets the name of the Subscription object, throws exception if length >10
     * @param subdate Subscription date
     * @throws IncorrectDateException Date length too long
     */
    public void setDate(String subdate) throws IncorrectDateException{
        if (date.length() > 10) {
            throw new IncorrectDateException();
        }
        this.date = subdate;
    }

    /**
     * Returns the comment from the Subscription object
     * @return comment
     */
    public String getComment()  { return comment; }

    /**
     * Sets the comment of the Subscription object, throws exception if length >30
     * @param comment Subscription comment
     * @throws CommentTooLongException Comment length too long
     */
    public void setComment(String comment) throws CommentTooLongException {

        if (comment.length() > 30){
            throw new CommentTooLongException();
        }
        this.comment = comment;
    }

    /**
     * Returns the monthly charge from the Subscription object
     * @return charge
     */
    public double getCharge()   { return charge; }

    /**
     * Sets the charge of the Subscription object, throws exception if charge < 0
     * @param charge Subscription charge
     * @throws NegativeValueException Negative value entered
     */
    public void setCharge(double charge) throws NegativeValueException {

        if (charge < 0){
            throw new NegativeValueException();
        }
        this.charge = charge;
    }

    /**
     * Creates concatenated string to be displayed as a ListIem, converts charge to String
     * @param name Subscription name
     * @param charge Subscription charge
     * @param comment Subscription comment
     * @param date Subscription date
     * @return Returns concatenated string
     */
    public String createSubString(String name, double charge, String comment, String date){
        String stringDouble = Double.toString(charge);
        subListString = "Subscription" + System.getProperty("line.separator")
                + "Name: " + name + System.getProperty("line.separator")
                + "Price: " + stringDouble + System.getProperty("line.separator")
                + "Date: " + date + System.getProperty("line.separator")
                + "Comment: " + comment;
        return subListString;
    }

    /**
     * Properly displays the ListItem using the concatenated SubString, converts object address
     * @return subListString from createSubString
     */
    @Override
    public String toString()    { return subListString; }

}
