package com.example.supunmadushanka.tuktukmeter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button button;

    private ArrayList<LatLng> points; //added
    Polyline line; //added


    //For printer
    UUID uuid;
    BluetoothAdapter bluetoothAdapter;
    String deviceAddress;
    BluetoothDevice device;
    BluetoothSocket socket;
    OutputStream outputStream;
    HashMap hashMap = null;

    TextView vehino,datetime,totalkm,waitingtime,hiretime,waitingprice,basicprice,fullprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        button= (Button) findViewById(R.id.map_bill);
        vehino= (TextView) findViewById(R.id.textView40);
        datetime= (TextView) findViewById(R.id.textView41);
        totalkm= (TextView) findViewById(R.id.textView42);
        waitingtime= (TextView) findViewById(R.id.textView43);
        hiretime= (TextView) findViewById(R.id.textView44);
        waitingprice= (TextView) findViewById(R.id.textView45);
        basicprice= (TextView) findViewById(R.id.textView46);
        fullprice= (TextView) findViewById(R.id.textView47);



        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                points = extras.getParcelableArrayList("lonlat");
                hashMap = (HashMap) getIntent().getSerializableExtra("datamap");
                //Toast.makeText(MapsActivity.this, ""+hashMap.get("totprice"), Toast.LENGTH_SHORT).show();

                vehino.setText(hashMap.get("vehicleno").toString().toUpperCase());
                datetime.setText(new SimpleDateFormat("yyyy-MM-dd").format(hashMap.get("date"))+" "+new SimpleDateFormat("HH:mm:ss").format(hashMap.get("starttime")));
                totalkm.setText(hashMap.get("totkm").toString()+" KM");
                waitingtime.setText(new SimpleDateFormat("HH:mm:ss").format(hashMap.get("waitingtime")));
                hiretime.setText(new SimpleDateFormat("HH:mm:ss").format(hashMap.get("hiretime")));
                waitingprice.setText(hashMap.get("waitingprice").toString()+" LKR");
                basicprice.setText(hashMap.get("basicprice").toString()+" LKR");
                fullprice.setText(hashMap.get("totprice").toString()+" LKR");

            }
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //mona printer eka gahuwath meka wenas wenne naa
        uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        //deviceAddress = "98:D3:31:80:5C:05";
        deviceAddress = "02:2B:57:C8:55:B7";

        enableBT();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
               // Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            //Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        redrawLine();
    }


    private void redrawLine(){

        mMap.clear();  //clears all Markers and Polylines

        LatLng startpoint;
        LatLng stoppoint;
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);

            if(i==0){
                startpoint=points.get(i);
                mMap.addMarker(new MarkerOptions().position(startpoint).title("This is The Hire Started Position"));

            }
            if(i==(points.size()-1)){
                stoppoint=points.get(i);
                mMap.addMarker(new MarkerOptions().position(stoppoint).title("This is The Hire End Position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stoppoint,12.0f));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));

            }
        }

        line = mMap.addPolyline(options); //add Polyline
    }

    public void enableBT() {
        int REQUEST_ENABLE_BT = 2;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            //Cannot use Bluetooth Feature with this mobile Devile
            Toast.makeText(this, "Cannot use Bluetooth Feature with this mobile Devile", Toast.LENGTH_SHORT).show();
        } else {
            //We can use the Printer via Blutooth
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
                //to Create bluetooth Socket with Printer..
                CreateBTSocket();
            } else {
                //to create Bluetooth Socket with Printer
                CreateBTSocket();
            }
        }
    }
    private void CreateBTSocket() {
        try {
            device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            socket = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothAdapter.cancelDiscovery();
            socket.connect();
            outputStream = socket.getOutputStream();
            Toast.makeText(this, "Connected To BT..!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void PrintBill(View view){
        try{
            String price="1000.00LKR";
            String data="       TAXI METER"+price;
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(data.getBytes());
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.flush();

        }catch(Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}
