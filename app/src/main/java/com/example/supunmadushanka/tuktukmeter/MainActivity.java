package com.example.supunmadushanka.tuktukmeter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageButton imageButton;
    TextToSpeech t1;

    TextView vehino,vehit;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton= (ImageButton) findViewById(R.id.f1_start);
        vehino= (TextView) findViewById(R.id.textView2);
        vehit= (TextView) findViewById(R.id.nav_name);

        String vehi=getIntent().getStringExtra("vehi");
        String vehitype=getIntent().getStringExtra("vehitype");

        vehino.setText(vehi);
        vehit.setText(vehitype);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        Animation a1= AnimationUtils.loadAnimation(this,R.anim.startbtn1);
        imageButton.setAnimation(a1);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

    }

    public void startHire(View view){
        if(view==imageButton){
            String vehinos=vehino.getText().toString();
            String vehis=vehit.getText().toString();
            editor.putString("lastvehino",vehinos);
            editor.putString("lastvehi",vehis);
            editor.apply();

            t1.speak("Hire Has been Started", TextToSpeech.QUEUE_FLUSH, null);
            Intent intent=new Intent(this,Hire.class);
            this.startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.startbtn1);

        }

    }

}
