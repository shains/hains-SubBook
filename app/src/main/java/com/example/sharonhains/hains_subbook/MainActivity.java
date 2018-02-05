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

    private Button newSubButton;
    private Button delSubButton; //= (Button) findViewById(R.id.delsub);
    private ViewFlipper vf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //listTotalCharge();

        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                vf.showNext();
                setResult(RESULT_OK);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vf.showNext();
                setResult(RESULT_OK);
            }
        });

        newSubButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                addNewSubscription();
            }
        });

        prevSubscriptionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {

                final Object position = prevSubscriptionList.getItemAtPosition(i);
                viewDetails(i);

                vf.showNext();

                delButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        removeItemFromList(position,i);
                    }
                });
                modifyButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        modifySubDetails(i);
                    }
                });
            }
        });



    }

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

    private void viewDetails(int i){
        Double selcharge = sublist.get(i).getCharge();
        String selname = sublist.get(i).getName();
        String selcomment = sublist.get(i).getComment();
        String seldate = sublist.get(i).getDate();

        String strcharge = Double.toString(selcharge);

        subcharge.setText(strcharge,TextView.BufferType.EDITABLE);
        subcomment.setText(selcomment,TextView.BufferType.EDITABLE);
        subname.setText(selname,TextView.BufferType.EDITABLE);
        subdate.setText(seldate,TextView.BufferType.EDITABLE);
    }

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

        double updatedcharge = selcharge - tosubtract;
        updateTotalCharge(updatedcharge);
        adapter.notifyDataSetChanged();
        saveInFile();
    }

    private double updateTotalCharge(double charge){
        currenttotalprice = currenttotalprice + charge;
        formatTotalCharge(currenttotalprice);
        return currenttotalprice;
    }

    private double listTotalCharge(){
        double addedcharge = 0;
        int length = prevSubscriptionList.getAdapter().getCount();

        for (int i = 0; i < length; i++){
            addedcharge = sublist.get(i).getCharge();
            currenttotalprice = currenttotalprice + addedcharge;
        }
        formatTotalCharge(currenttotalprice);
        return currenttotalprice;
    }

    private void formatTotalCharge(double currenttotalprice){
        stringtotal = String.format("%.2f",currenttotalprice);
        displayprice = findViewById(R.id.TotalCharge);
        displayprice.setText("Total Monthly Charge: "+stringtotal);
    }

    private void removeItemFromList(Object position,int i){
        double removedcharge = sublist.get(i).getCharge();
        updateTotalCharge(-removedcharge);
        sublist.remove(position);
        adapter.notifyDataSetChanged();
        saveInFile();
    }

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

    protected void onDestroy(){
        super.onDestroy();
        Log.i("In Destroy method","The app is closing");
    }

}
