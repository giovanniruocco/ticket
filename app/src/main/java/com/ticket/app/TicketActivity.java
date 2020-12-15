package com.ticket.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketActivity extends AppCompatActivity {


    float x1, x2, y1, y2;
    private TextView tvname, tvdescription, tvcategory, tvprice, tvcity, tvregion, tvdate, tooltxt;
    private ImageView img, imag;
    private boolean isOpen = false;
    private ConstraintSet layout1, layout2, layout3;
    private ConstraintLayout constraintLayout;
    private Vibrator myVib;
    private FirebaseAuth auth;
    private FloatingActionButton floatingtool;
    private String utentecorrente;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private String uid;
    private Map<String, String> tickets;
    private String[] preferiti;
    private List<String> chiavi;
    GoogleSignInClient mGoogleSignInClient;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

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
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tickets=new HashMap<>();
        tvname = (TextView) findViewById(R.id.textName);
        tvname.setSelected(true);
        tvdescription = (TextView) findViewById(R.id.txtDesc);
        tvcategory = (TextView) findViewById(R.id.textCategory);
        tvcategory.setSelected(true);
        
        tvcity = (TextView) findViewById(R.id.textCity);
        //tooltxt = (TextView) findViewById(R.id.toolbarTitle);
        tvdate = findViewById(R.id.txtDate);
        tvregion = (TextView) findViewById(R.id.txtRegion);
        tvprice = (TextView) findViewById(R.id.textPrice);
        img = (ImageView) findViewById(R.id.ticketthumbnail);
        /*imag = (ImageView) findViewById(R.id.categorythumbnail);*/

        myVib=(Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        // Ricezione dei dati
        Intent intent = getIntent();
        String Name = intent.getExtras().getString("Name");
        String Category =  intent.getExtras().getString("Category");
        String Price = "Price: " + intent.getExtras().getString("Price");
        String Description = Name + "'s description:\n" + intent.getExtras().getString("Description");
        String City = "City: " + intent.getExtras().getString("City");
        String Region = "Region: " + intent.getExtras().getString("Region");
        String Date = "Date: " + intent.getExtras().getString("Date");
        uid = intent.getExtras().getString("Uid");
        final String Tel = intent.getExtras().getString("Tel");
        final String Email = intent.getExtras().getString("Email");
        final String image = intent.getExtras().getString("Thumbnail");
        tvname.setText(Name);
        //tooltxt.setText(Name);
        toolbar.setTitle(Name);
        tvcategory.setText(Category);
        tvdescription.setText(Description);
        tvprice.setText(Price);
        tvcity.setText(City);
        tvregion.setText(Region);
        tvdate.setText(Date);
        Toast.makeText(TicketActivity.this, Email + "\n" + auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();

        Picasso.get()
                .load(image)
                //.placeholder(R.drawable.roundloading)
                .fit()
                .centerCrop()
                .into(img);

        if (Email.equals(auth.getCurrentUser().getEmail())) {
            floatingtool.setImageResource(R.drawable.ic_mode_edit_black);

            floatingtool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(TicketActivity.this, Email + "\n" + auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();

                }


            });

        }
        else{
            floatingtool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(TicketActivity.this, Email + "\n" + auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();


                    final CharSequence[] items={"Call", "Email", "Back"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(TicketActivity.this);
                    builder.setTitle("Contact");
                    builder.setItems(items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (items[i].equals("Call")) {
                                if (auth.getCurrentUser() != null) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + Tel));
                                    startActivity(intent);

                                } else
                                    Toast.makeText(TicketActivity.this, "You must be logged in.", Toast.LENGTH_SHORT).show();
                            } else if (items[i].equals("Email")) {
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