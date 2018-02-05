/*
 * Copyright (c) 2018, Sharon Hains. CMPUT 301. University of Alberta - All Rights Reserved. You may use, distribute, or modify this code under the terms and conditions fo the Code of Student Behaviour at the University of Alberta.
 */

package com.example.sharonhains.hains_subbook;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Contains the main function, contains methods that control app activity
 * @author hains
 * @version 1
 * @see Activity
 */
public class MainActivity extends Activity {
    private static final String FILENAME = "subbook.sav";
    private EditText subcomment;
    private EditText subcharge;
    private EditText subname;
    private EditText subdate;
    private ListView prevSubscriptionList;
    private TextView displayprice;
    private double currenttotalprice;
    private String stringtotal;

    private ArrayList<Subscription> sublist;
    private ArrayAdapter<Subscription> adapter;

    private ViewFlipper vf;

    /**
     * Starts the app, creates the main interface
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * This block contains the variables for the buttons on both pages,
         * as well as the list of Subscriptions. The variable to flip the layout to the next page
         * is defined on the last line of this block.
         */
        Button backButton = (Button) findViewById(R.id.go_back);
        Button newSubButton = (Button) findViewById(R.id.addsub);
        final Button addButton = (Button) findViewById(R.id.add);
        final Button delButton = (Button) findViewById(R.id.delsub);
        final Button modifyButton = (Button) findViewById(R.id.modifysub);
        subcharge = (EditText) findViewById(R.id.subprice);
        subcomment = (EditText) findViewById(R.id.subcomment);
        subname = (EditText) findViewById(R.id.subname);
        subdate = (EditText) findViewById(R.id.subdate);
        prevSubscriptionList = (ListView) findViewById(R.id.prevSubscriptionList);
        vf = findViewById(R.id.viewFlipper);

        /* The next  page will be shown when the "Add" button is clicked on the main page. */
        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                vf.showNext();
                setResult(RESULT_OK);
            }
        });

        /* The main  page will be shown when the "Back" button is clicked on the second page */
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vf.showNext();
                setResult(RESULT_OK);
            }
        });

        /* A new subscription will be added when the "Add New Subscription" is clicked */
        newSubButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                addNewSubscription();
            }
        });

        /* When an item in the subscription list is selected, next page is shown with details */
        prevSubscriptionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {

                final Object position = prevSubscriptionList.getItemAtPosition(i);
                viewDetails(i);

                vf.showNext();  /* Goes to the second page */

                /* Removes the item from sublist when the delete button is pressed */
                delButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        removeItemFromList(position,i);
                    }
                });
                /* Updates the item when modify existing subscription button is pressed*/
                modifyButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        modifySubDetails(i);
                    }
                });
            }
        });



    }

    /**
     * Loads the previous subscriptions from the saved file.
     */
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        loadFromFile();
        adapter = new ArrayAdapter<Subscription>(this,
                R.layout.list_item, sublist);
        prevSubscriptionList.setAdapter(adapter);
        listTotalCharge();
    }

    /**
     * Gets details from the selected subscription item, puts details into EditView on next page
     * @param i The position of the selected item in sublist
     */
    private void viewDetails(int i){
        Double selcharge = sublist.get(i).getCharge();
        String selname = sublist.get(i).getName();
        String selcomment = sublist.get(i).getComment();
        String seldate = sublist.get(i).getDate();

        String strcharge = Double.toString(selcharge);  /* Converts charge from double to string*/

        subcharge.setText(strcharge,TextView.BufferType.EDITABLE);
        subcomment.setText(selcomment,TextView.BufferType.EDITABLE);
        subname.setText(selname,TextView.BufferType.EDITABLE);
        subdate.setText(seldate,TextView.BufferType.EDITABLE);
    }

    /**
     * Gets strings from the EditView on the second page and passes them into the selected sub item
     * so that the item will be updated with new details
     * Returns exceptions if the details do not fit the parameters
     * Updates the total charge and saves the details in the file
     * @param i The position of the selected item in sublist
     */
    private void modifySubDetails(int i){
        double tosubtract = sublist.get(i).getCharge();
        String newCharge = subcharge.getText().toString();
        String newComment = subcomment.getText().toString();
        String newName = subname.getText().toString();
        String newDate = subdate.getText().toString();
        double selcharge = Double.parseDouble(newCharge);

        try {
            sublist.get(i).setCharge(selcharge);
            sublist.get(i).setComment(newComment);
            sublist.get(i).setName(newName);
            sublist.get(i).setDate(newDate);
            sublist.get(i).createSubString(newName,selcharge,newComment,newDate);
        } catch (NegativeValueException e){
            e.printStackTrace();
        } catch (CommentTooLongException e){
            e.printStackTrace();
        } catch (IncorrectDateException e){
            e.printStackTrace();
        } catch (NameTooLongException e){
            e.printStackTrace();
        }

        /* updated charge is the new charge minus the original charge */
        double updatedcharge = selcharge - tosubtract;
        updateTotalCharge(updatedcharge);
        adapter.notifyDataSetChanged();
        saveInFile();
    }

    /**
     * Gets details from the second page EditView and creates a new Subscription object with those
     * details.
     * Creates string to be displayed as a part of the list of subscriptions
     * Updates the total charge with the new subscription added. Saves new subscription in file.
     */
    private void addNewSubscription(){
        String newCharge = subcharge.getText().toString();
        String newComment = subcomment.getText().toString();
        String newName = subname.getText().toString();
        String newDate = subdate.getText().toString();

        double intCharge = Double.parseDouble(newCharge);
        Subscription newSub = new Subscription(newName,intCharge,newComment,newDate);
        newSub.createSubString(newName,intCharge,newComment,newDate);

        updateTotalCharge(intCharge);

        sublist.add(newSub);
        adapter.notifyDataSetChanged();
        saveInFile();
    }

    /**
     * Finds the charge of the selected subscription to be removed, subtracts charge from total
     * charge and is displayed with updated total. Subscription is removed from the sublist and
     * the file is saved.
     * @param position The object to be removed from the list
     * @param i The position of the selected item in sublist
     */
    private void removeItemFromList(Object position,int i){
        double removedcharge = sublist.get(i).getCharge();
        updateTotalCharge(-removedcharge);
        sublist.remove(position);
        adapter.notifyDataSetChanged();
        saveInFile();
    }

    /**
     * The charge to be added is passed and is added to the current total. The total is formatted
     * to have two decimals and is returned.
     * @param charge The charge to be added to the existing total
     * @return double; updated price, formatted with two decimals
     */
    private double updateTotalCharge(double charge){
        currenttotalprice = currenttotalprice + charge;

        formatTotalCharge(currenttotalprice);
        return currenttotalprice;
    }

    /**
     * Calculates total charge, retrieves count of items in list and gets the charge of each item
     * in the list. The charge is added for each item and the total charge is returned, formatted
     * to two decimals.
     * @return double; the total price of all of the subscriptions
     */
    private double listTotalCharge(){
        double addedcharge = 0;

        int length = prevSubscriptionList.getAdapter().getCount();

        /* Loops for the amount of items in the Subscription list */
        for (int i = 0; i < length; i++){
            addedcharge = sublist.get(i).getCharge();
            currenttotalprice = currenttotalprice + addedcharge;
        }
        formatTotalCharge(currenttotalprice);
        return currenttotalprice;
    }

    /**
     *  Formats the passed current total price to display as to two decimal places, sets the text
     *  to display the total monthly charge
     * @param currenttotalprice the price needing to have two decimal places
     */
    private void formatTotalCharge(double currenttotalprice){
        stringtotal = String.format("%.2f",currenttotalprice);
        displayprice = findViewById(R.id.TotalCharge);

        displayprice.setText("Total Monthly Charge: " + stringtotal);
    }

    /**
     *  Attempts to a load previous subscription list file.
     *  If one does not exist, a new one will be created.
     */
    private void loadFromFile() {
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();

            //Taken https://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt
            // 2018-01-24

            Type listType = new TypeToken<ArrayList<Subscription>>(){}.getType();

            sublist = gson.fromJson(in, listType);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            sublist = new ArrayList<Subscription>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     *  Saves the current subscription list in a file.
     */
    private void saveInFile() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME,
                    Context.MODE_PRIVATE);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            Gson gson = new Gson();

            gson.toJson(sublist, out);

            out.flush();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Handles when the app is exiting
     */
    protected void onDestroy(){
        super.onDestroy();
        Log.i("In Destroy method","The app is closing");
    }

}
