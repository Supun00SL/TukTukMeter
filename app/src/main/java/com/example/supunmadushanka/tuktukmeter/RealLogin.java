package com.example.supunmadushanka.tuktukmeter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class RealLogin extends AppCompatActivity {

    TextView reghere;
    EditText lognic,logpass;
    Button logbtn;
    Context context;

    String url="http://192.168.8.100:8080/TukTukServer/driverlogin";//http://192.168.8.100:8080/TukTukServer/registe
    URL urlob;
    HttpURLConnection urlConnection;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_login);

        context=this;

        reghere= (TextView) findViewById(R.id.Reallogin_reghere);
        lognic= (EditText) findViewById(R.id.log_nic);
        logpass= (EditText) findViewById(R.id.log_pass);
        logbtn= (Button) findViewById(R.id.log_btn);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

    }

    public void reghere(View view){
        if(view==reghere){
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

    }

    public void callreallogin(View view){
        if(view==logbtn){
            new RealLoginAsyncTask().execute();

        }

    }

    public String Login(){

            String nic=lognic.getText().toString();
            String pass=logpass.getText().toString();
            String stat="";

            if(!nic.equals("")|!pass.equals("")){
                try {
                    urlob=new URL(url);
                    urlConnection= (HttpURLConnection) urlob.openConnection();
                    urlConnection.setRequestMethod("POST");

                    ObjectOutputStream obout=new ObjectOutputStream(urlConnection.getOutputStream());

                    HashMap<String,Object> hashMap=new HashMap <>();

                    hashMap.put("nic",nic);
                    hashMap.put("pass",pass);


                    obout.writeObject(hashMap);
                    obout.close();

                    ObjectInputStream obin=new ObjectInputStream(urlConnection.getInputStream());
                    HashMap hm= (HashMap) obin.readObject();

                    if(hm.get("stat").toString().equals("true")){
                        editor.putString("driver",nic);
                        editor.apply();

                        deleteSQLiteDriver();
                        saveSQLiteDriver(hm);

                        Intent intent=new Intent(this,DriverProfileNav.class);
                        startActivity(intent);
                    }else{
                        stat="Your Entered Username or Password is Incorrect!";
                        //Toast.makeText(this,res, Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    //Toast.makeText(RealLogin.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                    stat=e.toString();
                }

            }else{
                //Toast.makeText(RealLogin.this, "Please Fill the Text Fields Correctly !", Toast.LENGTH_SHORT).show();
                stat="Please Fill the Text Fields Correctly !";
            }
        return stat;


    }

    public class RealLoginAsyncTask extends AsyncTask<String,String,String> {
        ProgressDialog progressDialog=new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            String stat="";
            try {
                stat=Login();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stat;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Login On Process..! Please Wait.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if(!s.equals("")){
             Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }

    public void saveSQLiteDriver(HashMap hashMap){
        try {
            Splash.db.execSQL("insert into Driver(NIC,NAME,LICEAN,BLOOD,CONTACT,EMERGENCY) values ('" + hashMap.get("nic").toString() + "','" + hashMap.get("name").toString() + "'" +
                    ",'"+hashMap.get("licean").toString()+"','"+hashMap.get("blood").toString()+"','"+hashMap.get("contact").toString()+"','"+hashMap.get("emergency").toString()+"')");
        }catch(Exception e){
            Toast.makeText(RealLogin.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSQLiteDriver(){
        try {
            Splash.db.execSQL("delete from Driver");
        }catch(Exception e){
            Toast.makeText(RealLogin.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

}
