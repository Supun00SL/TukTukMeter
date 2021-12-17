package com.example.supunmadushanka.tuktukmeter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

public class RegisterVehicleDetails extends AppCompatActivity {

    Spinner spinner;
    EditText editText;
    Button button;
    ListView listView;

    Context context;

    String vehicle_url="http://192.168.8.100:8080/TukTukServer/getvehicles";
    String vehicledetails_url="http://192.168.8.100:8080/TukTukServer/getvehicledetailsformme";
    String savevehicledetails_url="http://192.168.8.100:8080/TukTukServer/SaveVehicleDetails";
    URL urlob;
    HttpURLConnection urlConnection;

    ArrayAdapter spinneradapter;
    ArrayAdapter listviewArrayAdapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehicle_details);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        context=this;

        spinner= (Spinner) findViewById(R.id.rvd_spinner);
        editText= (EditText) findViewById(R.id.rvd_vehiclenumber);
        button= (Button) findViewById(R.id.rvd_add);
        listView= (ListView) findViewById(R.id.rvd_listview);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

        spinneradapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item);
        listviewArrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1);

        spinner.setAdapter(spinneradapter);
        listView.setAdapter(listviewArrayAdapter);

        callgetVehicles();
        callgetVehilesDetails();


    }

    public void getVehicles(){
        try {
            urlob=new URL(vehicle_url);
            urlConnection= (HttpURLConnection) urlob.openConnection();
            urlConnection.setRequestMethod("POST");

            ObjectInputStream obin=new ObjectInputStream(urlConnection.getInputStream());
            Set dataset= (Set) obin.readObject();

            spinneradapter.clear();
            for(Object o:dataset){
                spinneradapter.add(o.toString());

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callgetVehicles(){
        try {
            new getvehiclesAsyncTask().execute();
        }catch (Exception e){
            Toast.makeText(RegisterVehicleDetails.this, ""+e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

    public void getVehilesDetails(){
        try {
            urlob=new URL(vehicledetails_url);
            urlConnection= (HttpURLConnection) urlob.openConnection();
            urlConnection.setRequestMethod("POST");

           String drivernic= sharedPreferences.getString("driver","");

            if(!drivernic.equals("")){
                ObjectOutputStream obout=new ObjectOutputStream(urlConnection.getOutputStream());

                obout.writeObject(drivernic);
                obout.close();

                ObjectInputStream obin=new ObjectInputStream(urlConnection.getInputStream());
                Set dataset= (Set) obin.readObject();
                listviewArrayAdapter.clear();
                for(Object o:dataset){
                    listviewArrayAdapter.add(o.toString());
                }
            }
        }catch(Exception e){
            e.printStackTrace();

        }

    }
    public void callgetVehilesDetails(){
        try {
            new getvehiclesDetailsAsyncTask().execute();
        }catch (Exception e){
            Toast.makeText(RegisterVehicleDetails.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public String SaveVehicleDetails(){
        String stat="";
        try {
            urlob=new URL(savevehicledetails_url);
            urlConnection= (HttpURLConnection) urlob.openConnection();
            urlConnection.setRequestMethod("POST");

            ObjectOutputStream obout=new ObjectOutputStream(urlConnection.getOutputStream());

            String vehi=spinner.getSelectedItem().toString();
            String vehino=editText.getText().toString();
            String drivernic= sharedPreferences.getString("driver","");

            HashMap hashMap=new HashMap();
            hashMap.put("driver",drivernic);
            hashMap.put("vehi",vehi);
            hashMap.put("vehino",vehino);

            obout.writeObject(hashMap);
            obout.close();

            ObjectInputStream obin=new ObjectInputStream(urlConnection.getInputStream());
            String res=obin.readObject().toString();

            stat=res;

            getVehilesDetails();


        }catch (Exception e){
            e.printStackTrace();
            stat=e.toString();
        }finally {
            return stat;
        }

    }

    public void callSaveVehicleDetails(View view){
        if(view==button){
            new SaveVehicleDeatilsAsyncTask().execute();

        }
    }


    public class getvehiclesAsyncTask extends AsyncTask<String,String,String> {
        //ProgressDialog progressDialog=new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
//            String stat="";
            try {
               getVehicles();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPreExecute() {
//            progressDialog.setMessage("Login On Process..! Please Wait.");
//            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
//            progressDialog.dismiss();
//            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }
    public class getvehiclesDetailsAsyncTask extends AsyncTask<String,String,String> {
        //ProgressDialog progressDialog=new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
//            String stat="";
            try {
                getVehilesDetails();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPreExecute() {
//            progressDialog.setMessage("Login On Process..! Please Wait.");
//            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
//            progressDialog.dismiss();
//            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }
    public class SaveVehicleDeatilsAsyncTask extends AsyncTask<String,String,String> {
        ProgressDialog progressDialog=new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            String stat="";
            try {
                stat=SaveVehicleDetails();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stat;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Save On Process..! Please Wait.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if(!s.equals("")) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }

}
