package com.example.sharonhains.hains_subbook;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Sharon Hains on 2018-01-23.
 */

public class Subscription {

    private String name;
    private String date;
    private String comment;
    private double charge;
    private Format format;
    private String subListString;

    public Subscription (String name, double charge, String comment, String subdate){
        this.name = name;
        this.date = subdate;
        this.comment = comment;
        this.charge = charge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws NameTooLongException {

        if (name.length() > 20){
            throw new NameTooLongException();
        }
        this.name = name;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String subdate) throws IncorrectDateException{
        if (date.length() > 10) {
            throw new IncorrectDateException();
        }
        this.date = subdate;
    }

    /*public Date getDate() {
        return date;
    }

    public void setDate(Date date) throws IncorrectDateException {

        format = new SimpleDateFormat("yyyy-MM-dd");
        String stringdate = format.format(date);

        if (isValidDate(stringdate) == true) {
            this.date = date;
        }
        else {
            throw new IncorrectDateException();
        }
    }*/

    //Source http://www.java2s.com/Tutorial/Java/0120__Development/CheckifaStringisavaliddate.htm
    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) throws CommentTooLongException {

        if (comment.length() > 30){
            throw new CommentTooLongException();
        }
        this.comment = comment;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) throws NegativeValueException {

        if (charge < 0){
            throw new NegativeValueException();
        }
        this.charge = charge;
    }

    public void totalCharge(double charge){
        double initcharge = 0;
    }


    public String createSubString(String name, double charge, String comment, String date){
        String stringDouble = Double.toString(charge);
        subListString = "Subscription" + System.getProperty("line.separator")
                + "Name: " + name + System.getProperty("line.separator")
                + "Price: " + stringDouble + System.getProperty("line.separator")
                + "Date: " + date + System.getProperty("line.separator")
                + "Comment: " + comment;
        return subListString;
    }


    @Override
    public String toString(){
        //createSubString(name, charge, comment, subdate);
        return subListString;
    }

}
