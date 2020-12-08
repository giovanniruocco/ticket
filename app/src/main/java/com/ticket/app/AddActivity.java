package com.ticket.app;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Locale;


public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] categoryNames={"Music", "Football", "Theater", "Cinema", "Flights", "Train", "Other events"};
    int flags[] = {R.drawable.ic_music, R.drawable.ic_football, R.drawable.ic_theater, R.drawable.ic_popcorn, R.drawable.ic_airplane, R.drawable.ic_train, R.drawable.ic_more};
    String[] provincearray;
    String[] regionearray;
    String addname ;
    String addprice;
    String region, city;
    private EditText et_name,et_price,et_description;
    int id_regionearray;
    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    String userProvince, userAddress, regione, citta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Button gippiesse, add_btn;
        gippiesse = findViewById(R.id.gippiesse);
        add_btn = findViewById(R.id.add_button);

        et_name = (EditText) findViewById(R.id.add_name);
        et_description = (EditText) findViewById(R.id.add_description);
        et_price = (EditText) findViewById(R.id.add_price);


        final Spinner spinner = findViewById(R.id.spinner);
        final Spinner spinner2 = findViewById(R.id.spinner2);
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
                        id_regionearray = R.array.nullArray;
                        break;

                    case 1 : //Abruzzo
                        id_regionearray = R.array.abruzzoArray;
                        break;

                    case 2 : //Basilicata
                        id_regionearray = R.array.basilicataArray;
                        break;

                    case 3 : //Calabria
                        id_regionearray = R.array.calabriaArray;
                        break;

                    case 4 : //Campania
                        id_regionearray = R.array.campaniaArray;
                        break;

                    case 5 : //Emilia
                        id_regionearray = R.array.emiliaArray;
                        break;

                    case 6 : //Friuli
                        id_regionearray = R.array.friuliArray;
                        break;

                    case 7 : //Lazio
                        id_regionearray = R.array.lazioArray;
                        break;

                    case 8 : //Liguria
                        id_regionearray = R.array.liguriaArray;
                        break;

                    case 9 : //Lombardia
                        id_regionearray = R.array.lombardiaArray;
                        break;

                    case 10 : //Marche
                        id_regionearray = R.array.marcheArray;
                        break;

                    case 11 : //Molise
                        id_regionearray = R.array.moliseArray;
                        break;

                    case 12 : //Piemonte
                        id_regionearray = R.array.piemonteArray;
                        break;

                    case 13 : //Puglia
                        id_regionearray = R.array.pugliaArray;
                        break;

                    case 14 : //Sardegna
                        id_regionearray = R.array.sardegnaArray;
                        break;

                    case 15 : //Sicilia
                        id_regionearray = R.array.siciliaArray;
                        break;

                    case 16 : //Trentino
                        id_regionearray = R.array.trentinoArray;
                        break;

                    case 17 : //Toscana
                        id_regionearray = R.array.toscanaArray;
                        break;

                    case 18 : //Umbria
                        id_regionearray = R.array.umbriaArray;
                        break;

                    case 19 : //Valle d'Aosta
                        id_regionearray = R.array.valleArray;
                        break;

                    case 20 : //Veneto
                        id_regionearray = R.array.venetoArray;
                        break;
                }

                adapter1 = ArrayAdapter.createFromResource(getApplicationContext(), id_regionearray, android.R.layout.simple_spinner_item);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(adapter1);
                regionearray = getResources().getStringArray(R.array.regionArray);
                provincearray = getResources().getStringArray(id_regionearray);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                (( ArrayAdapter<CharSequence>) spinner2.getAdapter()).clear();
            }
        });

        final Spinner spin = (Spinner) findViewById(R.id.cat_spinner);
        spin.setOnItemSelectedListener(this);

        CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(),flags,categoryNames);
        spin.setAdapter(customAdapter);


//Performing action onItemSelected and onNothing selected

        gippiesse.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {

                                             if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                                 ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);


                                             LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


                                             if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                                     && ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                                     && ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                                                 Toast.makeText(AddActivity.this, "Permesso concesso", Toast.LENGTH_SHORT).show();
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

                                                 Geocoder geocoder = new Geocoder(AddActivity.this, Locale.getDefault());
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
                for (int i=0;i<regionearray.length;i++) {
                    if (regionearray[i].equals(regione)) {
                        index = i;
                        break;
                    }
                }
                spinner.setSelection(index);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //citta = "Terni";//insert code here
                        int indexo = -1;
                        for (int i=0;i<provincearray.length;i++) {
                            if (provincearray[i].equals(citta)) {
                                indexo = i;
                                break;
                            }
                        }
                        spinner2.setSelection(indexo);
                    }
                }, 150);

            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addname = et_name.getText().toString().trim();
                addprice = et_price.getText().toString().trim();
                if (spinner.getSelectedItem()!=null && spinner2.getSelectedItem() !=null) {
                    region = spinner.getSelectedItem().toString();
                    city = spinner2.getSelectedItem().toString();
                }
                else {
                    region = "";
                    city = "";
                }
                String category = categoryNames[spin.getSelectedItemPosition()];
                String finaltext = "Name: " + addname + "\nCategory: " + category + "\nRegion: " + region + "\nCity: " + city + "\nPrice: " + addprice;
                Toast.makeText(getApplicationContext(),finaltext, Toast.LENGTH_LONG).show();
            }

        });



    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        //Toast.makeText(getApplicationContext(), categoryNames[position], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
