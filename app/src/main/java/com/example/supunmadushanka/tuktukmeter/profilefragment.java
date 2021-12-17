package com.example.supunmadushanka.tuktukmeter;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Supun Madushanka on 2/15/2018.
 */
public class profilefragment extends Fragment{

    TextView drivername,driverlicean,totalhires,totalkm,totalmin,profit;

    String url="http://192.168.8.106:8080/TukTukServer/loaddriverprofile";
    String vehicledetails_url="http://192.168.8.106:8080/TukTukServer/getvehicledetailsformme";
    URL urlob;
    HttpURLConnection urlConnection;

    Context context;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view=inflater.inflate(R.layout.fragment_profile,container,false);

        context=this.getActivity();
        getActivity().setTitle("Home");

        drivername= (TextView) view.findViewById(R.id.textView21);
        driverlicean= (TextView) view.findViewById(R.id.textView18);
        totalhires= (TextView) view.findViewById(R.id.textView23);
        totalkm= (TextView) view.findViewById(R.id.textView25);
        totalmin= (TextView) view.findViewById(R.id.textView27);
        profit= (TextView) view.findViewById(R.id.textView29);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=sharedPreferences.edit();

        //callDriver();
        load();
        //callgetVehilesDetails();

        return view;
    }
    public String load(){
        String stat="";
        try {

            String drivernic= sharedPreferences.getString("driver","");

            if(!drivernic.equals("")){
                urlob=new URL(url);
                urlConnection= (HttpURLConnection) urlob.openConnection();
                urlConnection.setRequestMethod("POST");

                ObjectOutputStream obout=new ObjectOutputStream(urlConnection.getOutputStream());

                obout.writeObject(drivernic);
                obout.close();

                ObjectInputStream obin=new ObjectInputStream(urlConnection.getInputStream());
                HashMap res= (HashMap) obin.readObject();


                drivername.setText(res.get("name").toString());
                driverlicean.setText("L No: "+res.get("licean").toString());
                totalhires.setText(res.get("tothire").toString());
                totalmin.setText(res.get("tottime").toString()+" Min");
                profit.setText(res.get("profit").toString()+" LKR");
                NumberFormat formatter = new DecimalFormat("#0.00");
                totalkm.setText(formatter.format(res.get("totkm").toString())+" KM");

            }else{
                stat="Redirect To Login!";
            }
        }catch (Exception e){
            stat=e.toString();
        }finally{
            return stat;

        }
    }
//    public class getdriverAsyncTask extends AsyncTask<String,String,String> {
//        ProgressDialog progressDialog=new ProgressDialog(context);
//
//        @Override
//        protected String doInBackground(String... params) {
////            String stat="";
//            try {
//                load();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return "";
//        }
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog.setMessage("Login On Process..! Please Wait.");
//            progressDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            progressDialog.dismiss();
////            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        protected void onProgressUpdate(String... values) {
//
//        }
//    }
}
