package com.example.supunmadushanka.tuktukmeter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class Splash extends AppCompatActivity {

    ImageView splashImageView;
    TextView splashTextView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static SQLiteDatabase db;

    String url="http://192.168.8.100:8080/TukTukServer/FeetypeSync";
    URL urlob;
    HttpURLConnection urlConnection;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        splashImageView= (ImageView) findViewById(R.id.splash_iv);
        splashTextView= (TextView) findViewById(R.id.splash_tv);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

        context=this;

        Animation myani= AnimationUtils.loadAnimation(this,R.anim.mytrans);

        splashTextView.startAnimation(myani);
        splashImageView.startAnimation(myani);

        final Intent intentlogin=new Intent(this,RealLogin.class);
        final Intent intentprofile=new Intent(this,DriverProfileNav.class);
        final String drivernic= sharedPreferences.getString("driver","");

        //Create the DB
        db=openOrCreateDatabase("tuk.db",SQLiteDatabase.CREATE_IF_NECESSARY,null);
        db.setVersion(1);
        db.setLocale(Locale.getDefault());
        db.setLockingEnabled(true);
        //**************

        CreateDBTableDriver();
        CreateDBTablefeetype();
        callfeetype();

        Thread t=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                   if(!drivernic.equals("")){
                       startActivity(intentprofile);
                   }else{
                       startActivity(intentlogin);
                   }
                    finish();
                }
            }
        };
        t.start();



    }
    private void CreateDBTableDriver(){
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS Driver(NIC TEXT,NAME TEXT,LICEAN TEXT,BLOOD TEXT,CONTACT TEXT,EMERGENCY TEXT)");
            //Toast.makeText(this,"Table Created.",Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    private void CreateDBTablefeetype(){
        try {
            //idfeetype, type, basicfee, feefor1km, vehicle_idvehicle, defaultwaitingtime_min, feeforwaitingtime, nightbasicfee, nightfeeforkm, nightfeeforwaiting_min
            db.execSQL("CREATE TABLE IF NOT EXISTS Feetype(TYPE TEXT,BASICFEE REAL,FEEFORKM REAL,VEHICLE TEXT,DEFAULTWAITINGTIME_MIN INTEGER,FEEFORWAITINGTIME REAL,NIGHTBASICFEE REAL,NIGHTFEEFORKM REAL,NIGHTFEEFORWAITINGTIME REAL)");
            //Toast.makeText(this,"Table Created.",Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    public String saveSQLiteFeetype(HashMap hashMap){
        String stat="";
        try {
            Splash.db.execSQL("insert into Feetype(TYPE,BASICFEE,FEEFORKM,VEHICLE,DEFAULTWAITINGTIME_MIN,FEEFORWAITINGTIME,NIGHTBASICFEE,NIGHTFEEFORKM,NIGHTFEEFORWAITINGTIME) values ('" + hashMap.get("Type").toString() + "','" + hashMap.get("Basicfee").toString() + "'" +
                    ",'"+ hashMap.get("Feefor1km")+"','"+hashMap.get("Vehicle")+"','"+hashMap.get("DefaultwaitingtimeMin")+"','"+hashMap.get("Feeforwaitingtime")+"'" +
                    ",'"+hashMap.get("Nightbasicfee")+"','"+hashMap.get("Nightfeeforkm")+"','"+hashMap.get("NightfeeforwaitingMin")+"')");
        }catch(Exception e){
           stat=e.toString();
        }
        return stat;
    }

    public String deleteSQLiteFeetype(){
        String stat="";
        try {
            Splash.db.execSQL("delete from Feetype");
        }catch(Exception e){
            stat=e.toString();
        }

        return stat;

    }

    public String syncFeeType(){
        String stat="";
        try {
            urlob=new URL(url);
            urlConnection= (HttpURLConnection) urlob.openConnection();
            urlConnection.setRequestMethod("POST");
            ObjectOutputStream obout=new ObjectOutputStream(urlConnection.getOutputStream());

            obout.writeObject("in");
            obout.close();

            ObjectInputStream obin=new ObjectInputStream(urlConnection.getInputStream());
            Set dataset= (Set) obin.readObject();


            if(!dataset.isEmpty()){
                Iterator iterator=dataset.iterator();
                stat=deleteSQLiteFeetype();
                while (iterator.hasNext()){
                    HashMap hm = (HashMap) iterator.next();
                    stat=saveSQLiteFeetype(hm);
                }

            }
            stat=SearchAll();
        }catch(Exception e){
            stat="----here"+e.toString();
        }
        return stat;

    }
    public void callfeetype(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        String status = "";

        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = "WIFI Connected";
                Toast.makeText(Splash.this, status, Toast.LENGTH_SHORT).show();
                new FeeTypeSyncAsyncTask().execute();

            }else if(activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE){
                status="Mobile Data Connected";
                Toast.makeText(Splash.this, status, Toast.LENGTH_SHORT).show();
                new FeeTypeSyncAsyncTask().execute();

            }else{
                status="Not Connected";
                Toast.makeText(Splash.this, status, Toast.LENGTH_SHORT).show();
            }

          //  Log.i("info",status);


        }else {
            status="Not Connected";
            //Toast.makeText(Splash.this, status, Toast.LENGTH_SHORT).show();

        }

    }
    public class FeeTypeSyncAsyncTask extends AsyncTask<String,String,String> {
       // ProgressDialog progressDialog=new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            String stat="";
            try {
                stat=syncFeeType();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stat;
        }

        @Override
        protected void onPreExecute() {
          //  progressDialog.setMessage("Login On Process..! Please Wait.");
            //progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
          //  progressDialog.dismiss();
            if(!s.equals("")){
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }
    public String SearchAll(){
        String stat="";
        try{
            Cursor cursor=db.rawQuery("select * from Feetype",null);
            while(cursor.moveToNext()){
                String id=cursor.getString(0);
                String name=cursor.getString(1);
                String data=id+" - "+name;

                stat+=data;
                //Toast.makeText(Splash.this, ""+data, Toast.LENGTH_SHORT).show();

            }
        }catch (Exception e){
            //Toast.makeText(Splash.this, ""+e, Toast.LENGTH_SHORT).show();
            stat=e.toString();
        }
        return stat;


    }



}
