package com.ticket.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class gps extends AppCompatActivity {
    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    String userProvince, userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        TextView tv = findViewById(R.id.text_view);
        TextView tv2 = findViewById(R.id.textView2);
        TextView tv3 = findViewById(R.id.textView3);
        TextView tv4 = findViewById(R.id.textView4);
        TextView tv5 = findViewById(R.id.textView5);
        TextView tv0 = findViewById(R.id.textView0);
        Button refresh = findViewById(R.id.button);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity(new Intent(gps.this, gps.class));
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                Toast.makeText(gps.this, "Aggiorno", Toast.LENGTH_SHORT).show();

            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(gps.this, "Permesso concesso", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else {
            latitude = 0.0;
            longitude = 0.0;
        }


        //ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);

        try {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                userProvince = addresses.get(0).getSubAdminArea();
                userAddress = addresses.get(0).getAddressLine(0);
                if (userProvince.contains("Città Metropolitana di "))
                    userProvince=userProvince.substring(23);
                else if (userProvince.contains("Provincia di "))
                    userProvince = userProvince.substring(13);
                else if (userProvince.contains("Provincia dell'"))
                    userProvince = "L'" + userProvince.substring(15);
                tv0.setText("User Region: " + (addresses.get(0).getAdminArea()));
                tv.setText("User Province: " + userProvince);
                tv2.setText("User Locality: " + (addresses.get(0).getLocality()));
                tv3.setText("User Address: " + userAddress);
                tv4.setText("Latitude: " + latitude);
                tv5.setText("Longitude: " + longitude);
            }
            else {
                userProvince = "Unknown";
                tv.setText(userProvince);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}