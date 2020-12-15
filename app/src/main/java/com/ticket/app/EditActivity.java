package com.ticket.app;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] categoryNames={"Music", "Football", "Theater", "Cinema", "Flights", "Train", "Other events"};
    int categories[] = {R.drawable.ic_music, R.drawable.ic_football, R.drawable.ic_theater, R.drawable.ic_popcorn, R.drawable.ic_airplane, R.drawable.ic_train, R.drawable.ic_more};
    ImageView imgview;
    String[] provinceArray;
    String[] regionArray;
    String[] cityArray;
    String addname, addprice, adddesc;
    String region, city, category, finaltext;
    String cell;
    String missingFields = "";

    private DatabaseReference mDatabase, TicketsRef;
    private FirebaseAuth auth;
    private DatabaseReference UsersRef;


    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri selectedImageUri;
    static final int REQUEST_CAMERA = 1;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    private Integer SELECT_FILE=0;
    private String urlimage, mCurrentPhotoPath;
    private Bitmap bmp, bitmap;
    File photoFile = null;
    private static final String IMAGE_DIRECTORY_NAME = "TICKET";
    private EditText et_name,et_price,et_description;
    int id_regionArray;
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
        TicketsRef = FirebaseDatabase.getInstance().getReference("Tickets");
        mDatabase= FirebaseDatabase.getInstance().getReference();

        Button gippiesse, add_btn, add_img;
        gippiesse = findViewById(R.id.gippiesse);
        add_btn = findViewById(R.id.add_button);
        add_img = findViewById(R.id.immaginepiuID);
        imgview = findViewById(R.id.immagineviewID);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        add_btn.setText("EDIT");

        UsersRef = FirebaseDatabase.getInstance().getReference("Users");

        auth = FirebaseAuth.getInstance();
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final String utente = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (auth.getCurrentUser() != null) {
            //name.setText(auth.getCurrentUser().getDisplayName());

            Query query = UsersRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
            query.addListenerForSingleValueEvent(event);
        }


        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
                //Toast.makeText(getApplicationContext(),"Ammo", Toast.LENGTH_LONG).show();
            }
        });

        et_name = (EditText) findViewById(R.id.add_name);
        et_description = (EditText) findViewById(R.id.add_description);
        et_price = (EditText) findViewById(R.id.add_price);


        final Spinner spinner = findViewById(R.id.spinner);
        final Spinner spinner2 = findViewById(R.id.spinner2);
        regionArray = getResources().getStringArray(R.array.regionArray);
        cityArray = getResources().getStringArray(R.array.cityArray);

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



        final Intent intent = getIntent();
        final String editName = intent.getExtras().getString("Name");
        final String editCategory =  intent.getExtras().getString("Category");
        final String editDescription = intent.getExtras().getString("Description");
        final String editPrice = intent.getExtras().getString("Price");
        final String editCity = intent.getExtras().getString("City");
        final String editRegion = intent.getExtras().getString("Region");
        final String editImage = intent.getExtras().getString("Image");

        et_name.setText(editName);
        et_description.setText(editDescription);
        et_price.setText(editPrice);

        Toast.makeText(EditActivity.this, editRegion, Toast.LENGTH_SHORT).show();

        int indexC = -1;
        for (int i = 0; i< categoryNames.length; i++) {
            if (categoryNames[i].equals(editCategory)) {
                indexC = i;
                break;
            }
        }

        int indexR = -1;
        for (int i = 0; i< regionArray.length; i++) {
            if (regionArray[i].equals(editRegion)) {
                indexR = i;
                break;
            }
        }

        spin.setSelection(indexC);
        spinner.setSelection(indexR);

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

                int indexP = -1;
                for (int i = 0; i< provinceArray.length; i++) {
                    if (provinceArray[i].equals(editCity)) {
                        indexP = i;
                        break;
                    }
                }

                spinner2.setSelection(indexP);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                (( ArrayAdapter<CharSequence>) spinner2.getAdapter()).clear();
            }
        });



        Picasso.get()
                .load(editImage)
                .fit()
                .centerCrop()
                .into(imgview);

        /*
        String descrizione= Name + "'s description:\n" + Description;

        String citta= "City: " + City;
        final String eig = intent.getExtras().getString("Age");
        if(Integer.parseInt(eig)==1)  annio=" year";
        final String Age = "Age: " + eig + annio;
        final String Tel = intent.getExtras().getString("Tel");
        final String Email = intent.getExtras().getString("Email");
        final String Uid = intent.getExtras().getString("Uid");
        final String utente=auth.getCurrentUser().getUid();*/

//Performing action onItemSelected and onNothing selected

        gippiesse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(EditActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);


                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


                if (ActivityCompat.checkSelfPermission(EditActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(EditActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(EditActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(EditActivity.this, "Permesso concesso", Toast.LENGTH_SHORT).show();
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

                    Geocoder geocoder = new Geocoder(EditActivity.this, Locale.getDefault());
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
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addname = et_name.getText().toString().trim();
                addprice = et_price.getText().toString().trim();

                if(!(et_name.getText().toString().trim().equals("")))
                    addname = et_name.getText().toString().trim();
                else
                    missingFields = missingFields + " Name";

                if(!(et_price.getText().toString().trim().equals("")))
                    addprice = et_price.getText().toString().trim();
                else
                    missingFields = missingFields + " Price";

                if(!(et_description.getText().toString().trim().equals("")))
                    adddesc = et_description.getText().toString().trim();
                else
                    missingFields = missingFields + " Description";

                if (spinner.getSelectedItem() !=null) {
                    if (spinner.getSelectedItem().toString().equals("")) {
                        missingFields = missingFields + " Region";
                    } else {
                        region = spinner.getSelectedItem().toString();
                    }
                }

                if (spinner2.getSelectedItem() !=null) {
                    if (spinner2.getSelectedItem().toString().equals("")) {
                        missingFields = missingFields + " City";
                    } else {
                        city = spinner2.getSelectedItem().toString();
                    }
                } else {
                    missingFields = missingFields + " City";
                }

                category = categoryNames[spin.getSelectedItemPosition()];

                //finaltext = "Name: " + addname + "\nCategory: " + category + "\nDescription: " + adddesc + "\nRegion: " + region + "\nCity: " + city + "\nPrice: " + addprice + " €" + "\nEmail: " + email + "\nCell: " + cell;

                new AlertDialog.Builder(EditActivity.this)
                        .setMessage("Are you sure you entered the data correctly?")
                        .setCancelable(false)
                        .setPositiveButton("Yes, go ahead", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (missingFields == "") {
                                    getUrlimage();
                                    if (urlimage == null) {
                                        new AlertDialog.Builder(EditActivity.this)
                                                .setMessage("Image Missing")
                                                .setCancelable(false)
                                                .setNegativeButton("Ok, I'll check again", null)
                                                .show();
                                    } else {
                                        Ticket ticket = new Ticket(addname, category, adddesc, region, city, addprice, email, cell, getCurrentTimeStamp(), urlimage);
                                        ticket.setUtente(utente);
                                        String uid = mDatabase.child("Tickets").push().getKey();
                                        ticket.setUid(uid);
                                        mDatabase.child("Tickets").child(uid).setValue(ticket);
                                        Toast.makeText(EditActivity.this, "Congratulations, you've added your new post!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditActivity.this, ProfileActivity.class));
                                    }
                                } else {
                                    new AlertDialog.Builder(EditActivity.this)
                                            .setTitle("Fields missing\n")
                                            .setMessage(" " + missingFields)
                                            .setCancelable(false)
                                            .setNegativeButton("Ok, I'll check again", null)
                                            .show();
                                    missingFields = "";
                                }

                            }
                        })
                        .setNegativeButton("No, let me check", null)
                        .show();

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

    private void SelectImage(){
        final CharSequence[] items={"Camera","Album", "Back"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    captureimage();
                } else if (items[i].equals("Album")) {
                    if (ContextCompat.checkSelfPermission(EditActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                        selectimage();
                    else
                        requeststorage();
                } else if (items[i].equals("Back")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectimage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }
    private void requeststorage(){
        ActivityCompat.requestPermissions(EditActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
    }
    private void captureimage()
    {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        else
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Crea il file dove dovrebbe andare l'immagine
                try {

                    photoFile = createImageFile();
                    // Continua solamente se il file è stato creato con successo
                    if (photoFile != null) {
                        //Toast.makeText(this, "Creato", Toast.LENGTH_SHORT).show();
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.ticket.app.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                }
                catch (Exception ex) {
                    //Toast.makeText(this, "Eccezione", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==REQUEST_CAMERA){
                bmp = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                imgview.setImageBitmap(bmp);
                uploadCamera();

            }else if(requestCode==SELECT_FILE){
                selectedImageUri = data.getData();

                //CONVERT URI INTO BITMAP
                // Let's read picked image path using content resolver
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImageUri, filePath, null, null, null);
                ((Cursor) cursor).moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                bmp = BitmapFactory.decodeFile(imagePath, options);
                cursor.close(); // At the end remember to close the cursor or you will end with the RuntimeException!
                imgview.setImageBitmap(bmp);
                uploadCamera();

            }
        }
    }
    private File createImageFile() throws IOException {
        // Creazione di un file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefisso*/
                ".jpg",         /* suffisso */
                storageDir      /* cartella */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


   /* private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StorageReference ref = storageReference.child("Foto/" + UUID.randomUUID().toString());
        if (selectedImageUri != null) {

            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImageUri, filePath, null, null, null);
            ((Cursor) cursor).moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(imagePath, options);

            // Do something with the bitmap
            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();

            ByteArrayOutputStream baoo = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baoo);
            byte[] data = baoo.toByteArray();
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //Snackbar.make(v, "Upload completed", Toast.LENGTH_SHORT).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            setUrlimage(downloadUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            //Snackbar.make(v, "Upload failed, retry.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Loading: "+(int)progress+"%");
                        }
                    });
        }
    }
*/

    private void uploadCamera(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StorageReference ref = storageReference.child("Foto/" + UUID.randomUUID().toString());
        if (bmp!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] data = baos.toByteArray();
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //Snackbar.make(v, "Upload completed", Toast.LENGTH_SHORT).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            setUrlimage(downloadUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            //Snackbar.make(v, "Upload failed, retry.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Loading: "+(int)progress+"%");
                        }
                    });
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureimage();
            }
        }
        if (requestCode==1)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                selectimage();
        }
    }
    public void setUrlimage(Uri prova)
    {
        urlimage =new String(prova.toString());
    }
    public String getUrlimage()
    {
        return urlimage;
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    private void closeKeyboard(){
        View vista = this.getCurrentFocus();
        if (vista!=null){
            InputMethodManager input = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(vista.getWindowToken(),0);
        }
    }

    ValueEventListener event = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                User user = snap.getValue(User.class);
                if (!(user.getName().equals("")))
                    //name.setText("Name: " + user.getName() + " " + user.getSurname());
                    if (user.getCell() != null)
                        cell=user.getCell();

            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public boolean isEmpty(EditText et){
        return (et != null && (et.equals("") || et.equals(" ")));
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditActivity.this,MainActivity.class));
    }


}