package com.example.supunmadushanka.tuktukmeter;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    String url="http://192.168.8.100:8080/TukTukServer/register";//http://192.168.8.100:8080/TukTukServer/registe
    URL urlob;
    HttpURLConnection urlConnection;
    Context context;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText reg_nic,reg_fname,reg_lname,reg_licen,reg_contact,reg_emer,reg_blood;
    Button reg_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        context=this;

        reg_nic= (EditText) findViewById(R.id.reg_nic);
        reg_fname= (EditText) findViewById(R.id.reg_fname);
        reg_lname= (EditText) findViewById(R.id.reg_lname);
        reg_contact= (EditText) findViewById(R.id.reg_contactno);
        reg_emer= (EditText) findViewById(R.id.reg_emergency_no);
        reg_blood= (EditText) findViewById(R.id.reg_BloodGroup);
        reg_licen= (EditText) findViewById(R.id.reg_license);
        reg_save= (Button) findViewById(R.id.reg_register_btn);


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

    }

    public void calllogin(View view){

        if(view==reg_save){
            new SaveLoginAsyncTask().execute();
        }
    }

    public String login_save(){
        String stat="";
            try{

                String nic=reg_nic.getText().toString();
                String fname=reg_fname.getText().toString();
                String lname=reg_lname.getText().toString();
                String contact=reg_contact.getText().toString();
                String emer=reg_emer.getText().toString();
                String blood=reg_blood.getText().toString();
                String licen=reg_licen.getText().toString();

                if(!nic.equals("")|!fname.equals("")|!contact.equals("")|!licen.equals("")){
                    urlob=new URL(url);
                    urlConnection= (HttpURLConnection) urlob.openConnection();
                    urlConnection.setRequestMethod("POST");

                    ObjectOutputStream obout=new ObjectOutputStream(urlConnection.getOutputStream());

                    HashMap<String,Object> hashMap=new HashMap <>();

                    hashMap.put("nic",nic);
                    hashMap.put("fname",fname);
                    hashMap.put("lname",lname);
                    hashMap.put("contact",contact);
                    hashMap.put("emer",emer);
                    hashMap.put("blood",blood);
                    hashMap.put("licen",licen);

                    obout.writeObject(hashMap);
                    obout.close();

                    ObjectInputStream obin=new ObjectInputStream(urlConnection.getInputStream());
                    String res=obin.readObject().toString();

                    stat=res;

                    editor.putString("driver",nic);
                    editor.apply();

                    Intent intent=new Intent(context,RegisterVehicleDetails.class);
                    startActivity(intent);

                }else{
                    stat="Please Fill the Text Fields Correctly !";
                }

            }catch(Exception e){
                e.printStackTrace();
            }finally{
                return stat;
            }

    }

    public class SaveLoginAsyncTask extends AsyncTask<String,String,String> {
        ProgressDialog progressDialog=new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            String stat="";
            try {
                stat=login_save();
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
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }

}
