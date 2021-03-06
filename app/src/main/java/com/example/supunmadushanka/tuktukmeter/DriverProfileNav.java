package com.example.supunmadushanka.tuktukmeter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DriverProfileNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    TextView navnic,navname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        navnic= (TextView) headerView.findViewById(R.id.nav_nic);
        navname= (TextView) headerView.findViewById(R.id.nav_name);

        SearchAll();

        context=this;

        // Set the home as default
        FragmentManager fm=getFragmentManager();
        fm.beginTransaction().replace(R.id.drivercontent,new profilefragment()).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_profile_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment=null;
        if (id == R.id.nav_home) {

            fragment=new profilefragment();

        } else if (id == R.id.nav_choosevehi) {
            fragment=new choosevehiclefragment();
        } else if (id == R.id.nav_history) {
            fragment=new historyfragment();
        } else if (id == R.id.nav_income) {
            fragment=new incomefragment();
        } else if (id == R.id.nav_progress) {
            fragment=new progressfragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        FragmentManager fm=getFragmentManager();
        fm.beginTransaction().replace(R.id.drivercontent,fragment).commit();

        return true;
    }

    public void SearchAll(){

        try{
            Cursor cursor=Splash.db.rawQuery("select * from Driver",null);
            while(cursor.moveToNext()){
                String nic=cursor.getString(0);
                String name=cursor.getString(1);
                Toast.makeText(DriverProfileNav.this, ""+nic+" "+name, Toast.LENGTH_SHORT).show();
                navnic.setText(nic);
                navname.setText(name);

            }
        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_LONG).show();

        }



    }



}
