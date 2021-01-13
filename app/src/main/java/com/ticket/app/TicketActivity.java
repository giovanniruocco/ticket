package com.ticket.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketActivity extends AppCompatActivity {


    float x1, x2, y1, y2;
    private TextView tvname, tvdescription, tvcategory, tvprice, tvcity, tvregion, tvdate, tveventdate;
    private ImageView img, imag;
    private boolean isOpen = false;
    private ConstraintSet layout1, layout2, layout3;
    private ConstraintLayout constraintLayout;
    private Vibrator myVib;
    private FirebaseAuth auth;
    private FloatingActionButton floatingtool;
    private String loggeduser, utentecorrente;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private DatabaseReference myRef3;
    private String uid;
    private Map<String, String> tickets;
    private String[] preferiti;
    private List<String> chiavi;
    GoogleSignInClient mGoogleSignInClient;

    Toolbar toolbar;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        storage = FirebaseStorage.getInstance();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            utentecorrente = auth.getCurrentUser().getUid();
        }
        getSupportActionBar().hide();



        floatingtool = findViewById(R.id.floating_action_tool);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setSelected(true);

        //getSupportActionBar().hide();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(TicketActivity.this);
        if (acct != null) {
            utentecorrente = acct.getId();
        }


        myRef= FirebaseDatabase.getInstance().getReference();
        myRef2 = FirebaseDatabase.getInstance().getReference("Users");
        myRef3 = FirebaseDatabase.getInstance().getReference("Tickets");
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tickets=new HashMap<>();
        tvname = (TextView) findViewById(R.id.textName);
        tvname.setSelected(true);
        tvname.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvname.setSingleLine(true);
        tvname.setMarqueeRepeatLimit(-1);

        tvdescription = (TextView) findViewById(R.id.txtDesc);
        tvcategory = (TextView) findViewById(R.id.textCategory);
        tvcategory.setSelected(true);
        
        tvcity = (TextView) findViewById(R.id.textCity);
        tveventdate = (TextView) findViewById(R.id.textEventdate);
        tvdate = findViewById(R.id.txtDate);
        tvregion = (TextView) findViewById(R.id.txtRegion);
        tvprice = (TextView) findViewById(R.id.textPrice);
        img = (ImageView) findViewById(R.id.ticketthumbnail);
        imag = (ImageView) findViewById(R.id.categorythumbnail);

        myVib=(Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        // Ricezione dei dati
        Intent intent = getIntent();
        final String Name = intent.getExtras().getString("Name");
        final String Category =  intent.getExtras().getString("Category");
        final String Price = "Price: " + intent.getExtras().getString("Price");
        final String intentPrice = intent.getExtras().getString("Price");
        final String Description = "Description:\n" + intent.getExtras().getString("Description");
        final String intentDescription = intent.getExtras().getString("Description");
        final String intentEventdate = intent.getExtras().getString("Eventdate");
        final String City = "City: " + intent.getExtras().getString("City");
        final String intentCity = intent.getExtras().getString("City");
        final String Region = "Region: " + intent.getExtras().getString("Region");
        final String intentRegion = intent.getExtras().getString("Region");
        final String Date = "Date: " + intent.getExtras().getString("Date");
        final String Eventdate = "Event Date: " + intent.getExtras().getString("Eventdate");
        uid = intent.getExtras().getString("Uid");
        final String Tel = intent.getExtras().getString("Tel");
        final String Email = intent.getExtras().getString("Email");
        final String image = intent.getExtras().getString("Thumbnail");
        tvname.setText(Name);
        toolbar.setTitle(Name);
        tvcategory.setText(Category);
        tvdescription.setText(Description);
        tvprice.setText(Price + " €");
        tvcity.setText(City);
        tvregion.setText(Region);
        tvdate.setText(Date);
        tveventdate.setText(Eventdate);


        switch (Category) {
            case "Music" :
                imag.setImageResource(R.drawable.ic_music);
                break;

            case "Football" :
                imag.setImageResource(R.drawable.ic_football);
                break;

            case "Theater" :
                imag.setImageResource(R.drawable.ic_theater);
                break;

            case "Cinema" :
                imag.setImageResource(R.drawable.ic_popcorn);
                break;

            case "Flights" :
                imag.setImageResource(R.drawable.ic_airplane);
                break;

            case "Train" :
                imag.setImageResource(R.drawable.ic_train);
                break;

            case "Other events" :
                imag.setImageResource(R.drawable.ic_more);
                break;
        }


        Picasso.get()
                .load(image)
                .placeholder(R.drawable.roundloading)
                .fit()
                .centerCrop()
                .into(img);


        if (auth.getCurrentUser()!=null)
            loggeduser = auth.getCurrentUser().getEmail();
        else
            loggeduser = "";



        if (Email.equals(loggeduser)) {
            floatingtool.setImageResource(R.drawable.ic_mode_edit_black);

            floatingtool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    myVib.vibrate(25);

                    final CharSequence[] items={"Edit", "Delete", "Back"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(TicketActivity.this);
                    builder.setTitle("Edit Ticket");
                    builder.setItems(items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (items[i].equals("Edit")) {
                                myVib.vibrate(25);
                                Intent intento = new Intent(TicketActivity.this, EditActivity.class);
                                intento.putExtra("Name", Name);
                                intento.putExtra("Category", Category);
                                intento.putExtra("Eventdate", intentEventdate);
                                intento.putExtra("Description", intentDescription);
                                intento.putExtra("Price", intentPrice);
                                intento.putExtra("City", intentCity);
                                intento.putExtra("Region", intentRegion);
                                intento.putExtra("Image", image);
                                intento.putExtra("Uid", uid);

                                startActivity(intento);

                            } else if (items[i].equals("Delete")) {
                                myVib.vibrate(25);
                                new AlertDialog.Builder(TicketActivity.this)
                                        .setMessage("Are you sure you want delete " + Name +"?")
                                        .setCancelable(true)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                myRef3.child(uid).setValue(null);
                                                StorageReference canc = storage.getReferenceFromUrl(image);
                                                canc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                });
                                                startActivity(new Intent(TicketActivity.this, MyTicketsActivity.class) );
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();

                            } else if (items[i].equals("Back")) {
                                myVib.vibrate(25);
                                dialogInterface.dismiss();
                            }
                        }
                    });
                    builder.show();


                }


            });

        }
        else{
            floatingtool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    myVib.vibrate(25);

                    final CharSequence[] items={"Call", "Email", "Back"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(TicketActivity.this);
                    builder.setTitle("Contact");
                    builder.setItems(items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (items[i].equals("Call")) {
                                myVib.vibrate(25);
                                if (auth.getCurrentUser() != null) {

                                    if (Tel == null) {
                                        Toast.makeText(TicketActivity.this, "Telephone number is not available", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:" + Tel));
                                        startActivity(intent);
                                    }


                                } else
                                    Toast.makeText(TicketActivity.this, "You must be logged in.", Toast.LENGTH_SHORT).show();
                            } else if (items[i].equals("Email")) {
                                myVib.vibrate(25);
                                if (auth.getCurrentUser() != null) {
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setData(Uri.parse("mailto: "));
                                    String[] contatto = {Email};
                                    intent.putExtra(Intent.EXTRA_EMAIL, contatto);
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "Ticket");
                                    intent.putExtra(Intent.EXTRA_TEXT, "Cia fra.");
                                    intent.setType("message/rfc822");
                                    Intent chooser = Intent.createChooser(intent, "Send Email");
                                    startActivity(chooser);
                                } else
                                    Toast.makeText(TicketActivity.this, "You must be logged in.", Toast.LENGTH_SHORT).show();


                            } else if (items[i].equals("Back")) {
                                myVib.vibrate(25);
                                dialogInterface.dismiss();
                            }
                        }
                    });
                    builder.show();






                }


            });

        }





    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TicketActivity.this,MainActivity.class));
    }


}