package com.example.supunmadushanka.tuktukmeter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

/**
 * Created by Supun Madushanka on 2/20/2018.
 */
public class choosevehiclefragment  extends Fragment implements AdapterView.OnItemClickListener{

    Context context;
    ListView listView;

    String vehicledetails_url="http://192.168.8.100:8080/TukTukServer/getvehicledetailsformme";
    URL urlob;
    HttpURLConnection urlConnection;

    ArrayAdapter listviewArrayAdapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choosevehiclefragment, container, false);

        context = this.getActivity();
        getActivity().setTitle("Choose Vehicle");

        listView= (ListView) view.findViewById(R.id.choosevehi_listView);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=sharedPreferences.edit();

        listviewArrayAdapter=new ArrayAdapter(getActivity(),R.layout.mylistview_item);
        listView.setAdapter(listviewArrayAdapter);

        listView.setOnItemClickListener(this);

        getVehilesDetails();

        return view;
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
               // listviewArrayAdapter.clear();

                for(Object o:dataset){
                   listviewArrayAdapter.add(o.toString());
                }
            }
        }catch(Exception e){
            e.printStackTrace();

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String fullval=parent.getItemAtPosition(position).toString();
        String vehi=fullval.split("\\(")[0].trim();

        String vehitype=fullval.substring(fullval.indexOf('(') + 1, fullval.indexOf(')'));;

        Intent i=new Intent(getActivity(),MainActivity.class);
        i.putExtra("vehi",vehi);
        i.putExtra("vehitype",vehitype);
        startActivity(i);

    }
}
