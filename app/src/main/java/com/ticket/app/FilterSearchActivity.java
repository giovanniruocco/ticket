package com.ticket.app;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilterSearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SeekBar ftage;
    private AutoCompleteTextView ftbreed,ftcity;
    private RadioGroup groupgender;
    private RadioButton genderbutton;
    private TextView fttextage;
    private Button ftbutton;
    private Vibrator myVib;
    String[] provinceArray;
    String[] regionArray;
    String addname, addprice, adddesc;
    String region, city, category, finaltext;
    String cell;
    String missingFields = "";
    Spinner spinner, spinner2;
    int id_regionArray;
    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    String userProvince, userAddress, regione, citta;



    String[] categoryNames={"Any event", "Music", "Football", "Theater", "Cinema", "Flights", "Train", "Other events"};
    int categories[] = {R.drawable.ic_more, R.drawable.ic_music, R.drawable.ic_football, R.drawable.ic_theater, R.drawable.ic_popcorn, R.drawable.ic_airplane, R.drawable.ic_train, R.drawable.ic_more};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);
        setTitle("Filter");
        myVib=(Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        Button gippiesse = findViewById(R.id.gippiesse);
        Button search_btn = findViewById(R.id.search_button);


        spinner = findViewById(R.id.reg_spinner);
        spinner2 = findViewById(R.id.city_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.regionArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<CharSequence> adapter1;

                switch (position) {
                    case 0 : //No selection
                        id_regionArray = R.array.nullArray;
                        break;

                    case 1 : //Abruzzo
                        id_regionArray = R.array.abruzzoArray;
                        break;

                    case 2 : //Basilicata
                        id_regionArray = R.array.basilicataArray;
                        break;

                    case 3 : //Calabria
                        id_regionArray = R.array.calabriaArray;
                        break;

                    case 4 : //Campania
                        id_regionArray = R.array.campaniaArray;
                        break;

                    case 5 : //Emilia
                        id_regionArray = R.array.emiliaArray;
                        break;

                    case 6 : //Friuli
                        id_regionArray = R.array.friuliArray;
                        break;

                    case 7 : //Lazio
                        id_regionArray = R.array.lazioArray;
                        break;

                    case 8 : //Liguria
                        id_regionArray = R.array.liguriaArray;
                        break;

                    case 9 : //Lombardia
                        id_regionArray = R.array.lombardiaArray;
                        break;

                    case 10 : //Marche
                        id_regionArray = R.array.marcheArray;
                        break;

                    case 11 : //Molise
                        id_regionArray = R.array.moliseArray;
                        break;

                    case 12 : //Piemonte
                        id_regionArray = R.array.piemonteArray;
                        break;

                    case 13 : //Puglia
                        id_regionArray = R.array.pugliaArray;
                        break;

                    case 14 : //Sardegna
                        id_regionArray = R.array.sardegnaArray;
                        break;

                    case 15 : //Sicilia
                        id_regionArray = R.array.siciliaArray;
                        break;

                    case 16 : //Trentino
                        id_regionArray = R.array.trentinoArray;
                        break;

                    case 17 : //Toscana
                        id_regionArray = R.array.toscanaArray;
                        break;

                    case 18 : //Umbria
                        id_regionArray = R.array.umbriaArray;
                        break;

                    case 19 : //Valle d'Aosta
                        id_regionArray = R.array.valleArray;
                        break;

                    case 20 : //Veneto
                        id_regionArray = R.array.venetoArray;
                        break;
                }

                adapter1 = ArrayAdapter.createFromResource(getApplicationContext(), id_regionArray, android.R.layout.simple_spinner_item);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(adapter1);
                regionArray = getResources().getStringArray(R.array.regionArray);
                provinceArray = getResources().getStringArray(id_regionArray);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                (( ArrayAdapter<CharSequence>) spinner2.getAdapter()).clear();
            }
        });

        final Spinner spin = (Spinner) findViewById(R.id.cat_spinner);
        spin.setOnItemSelectedListener(this);

        CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(), categories,categoryNames);
        spin.setAdapter(customAdapter);


        gippiesse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myVib.vibrate(25);
                useGPS();
            }
        });





        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spinner.getSelectedItem() !=null) {
                    if (spinner.getSelectedItem().toString().equals("")) {
                        region = "";
                    } else {
                        region = spinner.getSelectedItem().toString();
                    }
                }

                if (spinner2.getSelectedItem() !=null) {
                    if (spinner2.getSelectedItem().toString().equals("")) {
                        city = "";
                    } else {
                        city = spinner2.getSelectedItem().toString();
                    }
                }
                else
                    city= "";

                //if (!(categoryNames[spin.getSelectedItemPosition()] == "Any event"))
                    category = categoryNames[spin.getSelectedItemPosition()];
                //else
                    //category ="";
                String testo = category + region + city;

                Toast.makeText(FilterSearchActivity.this, testo, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(FilterSearchActivity.this, FilterActivity.class);
                intent.putExtra("Category", category);
                intent.putExtra("Region", region);
                intent.putExtra("City", city);
                intent.putExtra("Provenienza", false);
                startActivity(intent);

            }
            });
//Performing action onItemSelected and onNothing selected










    }




    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode==1)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                useGPS();
        }
    }

    private void useGPS() {


        if (ContextCompat.checkSelfPermission(FilterSearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(FilterSearchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(FilterSearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FilterSearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FilterSearchActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(FilterSearchActivity.this, "Permesso concesso", Toast.LENGTH_SHORT).show();
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
        } else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        } else {
            latitude = 0.0;
            longitude = 0.0;
        }


        //ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);

        try {

            Geocoder geocoder = new Geocoder(FilterSearchActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                userProvince = addresses.get(0).getSubAdminArea();
                userAddress = addresses.get(0).getAddressLine(0);
                if (userProvince.contains("Città Metropolitana di "))
                    userProvince = userProvince.substring(23);
                else if (userProvince.contains("Provincia di "))
                    userProvince = userProvince.substring(13);
                else if (userProvince.contains("Provincia dell'"))
                    userProvince = "L'" + userProvince.substring(15);
                regione = addresses.get(0).getAdminArea();
                citta = userProvince;

            } else {
                userProvince = "Unknown";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        int index = -1;
        for (int i = 0; i< regionArray.length; i++) {
            if (regionArray[i].equals(regione)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int index2 = -1;
                for (int i = 0; i< provinceArray.length; i++) {
                    if (provinceArray[i].equals(citta)) {
                        index2 = i;
                        break;
                    }
                }
                spinner2.setSelection(index2);
            }
        }, 150);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
