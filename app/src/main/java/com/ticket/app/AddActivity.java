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
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Locale;


public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] countryNames={"Music", "Football", "Theater", "Cinema", "Flights", "Train", "Other events"};
    int flags[] = {R.drawable.ic_music, R.drawable.ic_football, R.drawable.ic_theater, R.drawable.ic_popcorn, R.drawable.ic_airplane, R.drawable.ic_train, R.drawable.ic_more};
    String[] provincearray = {"ciao"};
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
        Button gippiesse;
        gippiesse = findViewById(R.id.gippiesse);
        TextView testo = findViewById(R.id.textView2);

        final String[] testArray = getResources().getStringArray(R.array.regionArray);
        testo.setText(testArray[1]);






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
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.nullArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 1 : //Abruzzo
                        provincearray = getResources().getStringArray(R.array.abruzzoArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.abruzzoArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 2 : //Basilicata
                        provincearray = getResources().getStringArray(R.array.basilicataArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.basilicataArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 3 : //Calabria
                        provincearray = getResources().getStringArray(R.array.calabriaArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.calabriaArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 4 : //Campania
                        provincearray = getResources().getStringArray(R.array.campaniaArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.campaniaArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 5 : //Emilia
                        provincearray = getResources().getStringArray(R.array.emiliaArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.emiliaArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 6 : //Friuli
                        provincearray = getResources().getStringArray(R.array.friuliArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.friuliArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 7 : //Lazio
                        provincearray = getResources().getStringArray(R.array.lazioArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.lazioArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 8 : //Liguria
                        provincearray = getResources().getStringArray(R.array.liguriaArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.liguriaArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 9 : //Lombardia
                        provincearray = getResources().getStringArray(R.array.lombardiaArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.lombardiaArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 10 : //Marche
                        provincearray = getResources().getStringArray(R.array.marcheArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.marcheArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 11 : //Molise
                        provincearray = getResources().getStringArray(R.array.moliseArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.moliseArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 12 : //Piemonte
                        provincearray = getResources().getStringArray(R.array.piemonteArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.piemonteArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 13 : //Puglia
                        provincearray = getResources().getStringArray(R.array.pugliaArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.pugliaArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 14 : //Sardegna
                        provincearray = getResources().getStringArray(R.array.sardegnaArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.sardegnaArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 15 : //Sicilia
                        provincearray = getResources().getStringArray(R.array.siciliaArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.siciliaArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 16 : //Trentino
                        provincearray = getResources().getStringArray(R.array.trentinoArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.trentinoArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 17 : //Toscana
                        provincearray = getResources().getStringArray(R.array.toscanaArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.toscanaArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 18 : //Umbria
                        provincearray = getResources().getStringArray(R.array.umbriaArray);
                        provincearray = getResources().getStringArray(R.array.umbriaArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.umbriaArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 19 : //Valle d'Aosta
                        provincearray = getResources().getStringArray(R.array.valleArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.valleArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;

                    case 20 : //Veneto
                        provincearray = getResources().getStringArray(R.array.venetoArray);
                        adapter1 = ArrayAdapter.createFromResource( getApplicationContext(), R.array.venetoArray, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter1);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                (( ArrayAdapter<CharSequence>) spinner2.getAdapter()).clear();
            }
        });

      
       
       
       
       
       
       
       

       
       
       
       
       
       
 

        final Spinner spin = (Spinner) findViewById(R.id.cat_spinner);
        spin.setOnItemSelectedListener(this);

        CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(),flags,countryNames);
        spin.setAdapter(customAdapter);


//Performing action onItemSelected and onNothing selected

        gippiesse.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {


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
















                //regione = "Umbria";//insert code here
                int index = -1;
                for (int i=0;i<testArray.length;i++) {
                    if (testArray[i].equals(regione)) {
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
                }, 50);



            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        Toast.makeText(getApplicationContext(), countryNames[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
