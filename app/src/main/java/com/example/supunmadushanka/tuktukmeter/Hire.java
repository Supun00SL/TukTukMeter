package com.example.supunmadushanka.tuktukmeter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Hire extends AppCompatActivity {

    boolean locclick=true;

    Context context;

    Thread thread;
    Handler handler;

    Thread threadwaiting;
    Handler handlerwaiting;

    TextView textViewkm;
    TextView textViewprice;
    TextView textViewtime;
    TextView textViewwaitingtime;
    TextView textviewspeed;
    Button stopbutton, pausebtn, resumebtn;
    ImageButton imageButton;

    TextToSpeech t1;

    LocationManager locationManager;
    LocationListener locationListener;

    int i = 0;
    int j = 0;
    double lat1;
    double lon1;
    float dist[] = {0.0f};
    float res;
    double travelkm = 0.00;
    double price = 0.0;
    double statrtprice = 0.0;
    double waitingprice = 0.0;
    double feeforkmd=0.0;
    double waittimepriced=0.0;
    double speed = 0.0;
    double hours = 0.0;

    Date starttime=null;
    Date endtime=null;
    Date hiredate=null;
    double avgspeed=0.0;
    Date hiretime=null;
    Date waitingtime=null;
    double tottimemin=0.0;
    double totkm=0.0;
    double totprice=0.0;


    long oldmillis = 0;
    long newmillis = 0;
    long millisrange = 0;

    double oldposi = 0.0;
    double newposi = 0.0;
    double posirange = 0.0;

    boolean milliflag = true;

    private ArrayList<LatLng> points; //added

    boolean mPaused;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //feetype
    String TYPE="";
    double BASICFEE=0.0;
    double FEEFORKM=0.0;
    String VEHICLE="";
    int DEFAULTWAITINGTIME_MIN=0;
    double FEEFORWAITINGTIME=0.0;
    double NIGHTBASICFEE=0.0;
    double NIGHTFEEFORKM=0.0;
    double NIGHTFEEFORWAITINGTIME=0.0;

    String url="http://192.168.8.100:8080/TukTukServer/SaveTripDetails";
    URL urlob;
    HttpURLConnection urlConnection;

    HashMap<String,Object> hashMap=new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire);

        context=this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mPaused = false;

        try {
            starttime=new SimpleDateFormat("HH:mm:ss").parse(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            hiredate=new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        SearchAll();

        points = new ArrayList<LatLng>(); //added

        textViewkm = (TextView) findViewById(R.id.f2_km);
        textViewprice = (TextView) findViewById(R.id.f2_price);
        textViewtime = (TextView) findViewById(R.id.f2_time);
        textViewwaitingtime = (TextView) findViewById(R.id.textView4);
        textviewspeed = (TextView) findViewById(R.id.textView6);

        imageButton = (ImageButton) findViewById(R.id.imageButton);

        stopbutton = (Button) findViewById(R.id.button);
        pausebtn = (Button) findViewById(R.id.button4);
        resumebtn = (Button) findViewById(R.id.button2);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        //assign start price check time 10clok nam wadi
        if(checkStarttime().equals("day")){
           statrtprice = BASICFEE;
           feeforkmd=FEEFORKM;
            waittimepriced=FEEFORWAITINGTIME;
        }else if(checkStarttime().equals("night")){
            statrtprice = NIGHTBASICFEE;
            feeforkmd=NIGHTFEEFORKM;
            waittimepriced=NIGHTFEEFORWAITINGTIME;
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (!mPaused) {

                    if (lat1 != 0 && lon1 != 0) {
                        Location.distanceBetween(lat1, lon1, location.getLatitude(), location.getLongitude(), dist);
                        res += dist[0];

                        travelkm = res / 1000;

                        //set price
                        int travelkmint = (int) Math.round(travelkm);
                        price = travelkmint * feeforkmd;
                        //set speed

                        if (milliflag) {
                            newmillis = System.currentTimeMillis();
                            newposi = travelkm;
                            milliflag = false;
                        } else {
                            oldmillis = newmillis;
                            newmillis = System.currentTimeMillis();

                            oldposi = newposi;
                            newposi = travelkm;

                        }

                        millisrange = newmillis - oldmillis;
                        posirange = newposi - oldposi;


                        double ieka = Double.parseDouble(millisrange + "");
                        hours = (ieka / (1000 * 60 * 60));
                        //Toast.makeText(Hire.this, hours+"", Toast.LENGTH_SHORT).show();
                        speed = posirange / hours;
                        //--------------------------------

                    }

                    lat1 = location.getLatitude();
                    lon1 = location.getLongitude();

                    LatLng latLng = new LatLng(lat1, lon1); //you already have this

                    points.add(latLng); //added

                    if (location.getAccuracy() == 0.0f) {
                        imageButton.setImageDrawable(getResources().getDrawable(R.drawable.noloc));
                    } else if (location.getAccuracy() > 0.0f && location.getAccuracy() <= 10.0f) {
                        imageButton.setImageDrawable(getResources().getDrawable(R.drawable.verygoodloc));
                    } else if (location.getAccuracy() > 10.0f && location.getAccuracy() <= 20.0f) {
                        imageButton.setImageDrawable(getResources().getDrawable(R.drawable.goodloc));
                    } else {
                        imageButton.setImageDrawable(getResources().getDrawable(R.drawable.badloc));
                    }

                } else {
                    imageButton.setImageDrawable(getResources().getDrawable(R.drawable.noloc));

                    //waiting price hadanawa
                    int waittimemin = (int) Math.round(j / (1000 * 60));
                    int waitlow=waittimemin/DEFAULTWAITINGTIME_MIN;
                    waitingprice = waitlow * waittimepriced;

                    //set speed to 0
                    speed = 0;

                }
                NumberFormat formatter = new DecimalFormat("#0.00");
                textViewkm.setText(formatter.format(travelkm) + "");
                textViewprice.setText(formatter.format(price + statrtprice + waitingprice) + "");
                if (speed > 1000) {
                    textviewspeed.setText(formatter.format(0) + "");
                } else {
                    textviewspeed.setText(formatter.format(speed) + "");
                }

            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                if(locclick){
                    locclick=false;
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);

                }

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
            return;
        } else {
            ConfigureButton();

        }


        handler = new Handler();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    if (!mPaused) {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                Date date = new Date();
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                cal.set(Calendar.HOUR_OF_DAY, 0);
                                cal.set(Calendar.MINUTE, 0);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, i);
                                date = cal.getTime();
                                textViewtime.setText(new SimpleDateFormat("HH:mm:ss").format(date));
                                i += 1000;
                                ConfigureButton();

                            }
                        });

                        try {
                            thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    } else {
                        try {
                            synchronized (thread) {

                                thread.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        });

        thread.start();

        //waiting thread

        handlerwaiting = new Handler();
        threadwaiting = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    if (mPaused) {
                        handlerwaiting.post(new Runnable() {

                            @Override
                            public void run() {
                                Date date = new Date();
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                cal.set(Calendar.HOUR_OF_DAY, 0);
                                cal.set(Calendar.MINUTE, 0);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, j);
                                date = cal.getTime();
                                textViewwaitingtime.setText(new SimpleDateFormat("HH:mm:ss").format(date));
                                j += 1000;
                                ConfigureButton();

                            }
                        });

                        try {
                            threadwaiting.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    } else {
                        try {
                            synchronized (threadwaiting) {

                                threadwaiting.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        });

        threadwaiting.start();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ConfigureButton();
                }
                return;
        }
    }

    public void ConfigureButton() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

    }

    public void stopall(View view){
        if(view==stopbutton){

            //Save Details in Server
            try {
                endtime=new SimpleDateFormat("HH:mm:ss").parse(new SimpleDateFormat("HH:mm:ss").format(new Date()));

                String hiretimestring=textViewtime.getText().toString();
                String waitingtimestring=textViewwaitingtime.getText().toString();
                hiretime=new SimpleDateFormat("HH:mm:ss").parse(hiretimestring);
                waitingtime=new SimpleDateFormat("HH:mm:ss").parse(waitingtimestring);

                tottimemin=(i+j)/(1000*60);
                totkm=Double.parseDouble(textViewkm.getText().toString());
                avgspeed=totkm/(i/1000*60*60);
                totprice=Double.parseDouble(textViewprice.getText().toString());
                String drivernic= sharedPreferences.getString("driver","");
                String vehinoeka=sharedPreferences.getString("lastvehino","");




                hashMap.put("starttime",starttime);
                hashMap.put("endtime",endtime);
                hashMap.put("date",hiredate);
                hashMap.put("avgspeed",avgspeed);
                hashMap.put("hiretime",hiretime);
                hashMap.put("waitingtime",waitingtime);
                hashMap.put("totkm",totkm);
                hashMap.put("totprice",totprice);
                hashMap.put("driver",drivernic);
                hashMap.put("vehicleno",vehinoeka);
                hashMap.put("totaltime",tottimemin);
                hashMap.put("waitingprice",waitingprice);
                hashMap.put("basicprice",statrtprice);

                //call
                CallSaveTripASYNC();

            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
    public void PauseBtn(View view){
        if(view==pausebtn) {
            t1.speak("Hire Has been Paused.", TextToSpeech.QUEUE_FLUSH, null);
                onPausethread();
        }
    }
    public void ResumeBtn(View view){
        if(view==resumebtn) {
            t1.speak("Hire Has been Resumed.", TextToSpeech.QUEUE_FLUSH, null);
            onResumethread();
        }
    }


    public void onPausethread() {
        synchronized (threadwaiting) {
            mPaused = true;
            threadwaiting.notifyAll();
        }
    }

    public void onResumethread() {
        synchronized (thread) {
            mPaused = false;
            thread.notifyAll();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void SearchAll(){

        try{
            String vehicle=sharedPreferences.getString("lastvehi","");
            Cursor cursor=Splash.db.rawQuery("select * from Feetype Where VEHICLE='"+vehicle+"'",null);
            while(cursor.moveToNext()){
                 TYPE=cursor.getString(0);
                 BASICFEE=cursor.getDouble(1);
                 FEEFORKM=cursor.getDouble(2);
                 VEHICLE=cursor.getString(3);
                 DEFAULTWAITINGTIME_MIN=cursor.getInt(4);
                 FEEFORWAITINGTIME=cursor.getDouble(5);
                 NIGHTBASICFEE=cursor.getDouble(6);
                 NIGHTFEEFORKM=cursor.getDouble(7);
                 NIGHTFEEFORWAITINGTIME=cursor.getDouble(8);
            }
        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();

        }
    }

    public String checkStarttime(){
        String stat="";
            int nowtime=Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));

            if(22<=nowtime){
                //night
                stat="night";
            }else if(0<=nowtime&&nowtime<05){
                //night
                stat="night";
            }else{
                //day
                stat="day";
            }
        return stat;
    }

    public String SaveTrip(){
        String stat="";
        try {
            urlob=new URL(url);
            urlConnection= (HttpURLConnection) urlob.openConnection();
            urlConnection.setRequestMethod("POST");

            ObjectOutputStream obout=new ObjectOutputStream(urlConnection.getOutputStream());
            obout.writeObject(hashMap);
            obout.close();

            ObjectInputStream obin=new ObjectInputStream(urlConnection.getInputStream());
            stat= obin.readObject().toString();
        }catch(Exception e){
            stat=e.toString();
        }
        return stat;
    }

    public class SaveTripAsyncTask extends AsyncTask<String,String,String> {
        ProgressDialog progressDialog=new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            String stat="";
            try {
                stat=SaveTrip();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stat;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Saving Details..! Please Wait.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if(!s.equals("")){
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
            t1.speak("Hire Has been Stoped. Your Traveled Kilometer count is "+textViewkm.getText().toString()+". Your Hire price is "+textViewprice.getText().toString()+" Rupees.", TextToSpeech.QUEUE_FLUSH, null);
            onPausethread();
            Intent intent=new Intent(context,MapsActivity.class);
            intent.putParcelableArrayListExtra( "lonlat", points );
            intent.putExtra("datamap", hashMap);
            context.startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }

    public void CallSaveTripASYNC(){
        new SaveTripAsyncTask().execute();

    }
}
