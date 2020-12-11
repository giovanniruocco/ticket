package com.ticket.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketActivity extends AppCompatActivity {


    float x1, x2, y1, y2;
    private TextView tvname, tvdescription, tvcategory, tvprice, tvcity, tvregion, tvdate;
    private ImageView img, imag;
    private boolean isOpen = false;
    private ConstraintSet layout1, layout2, layout3;
    private ConstraintLayout constraintLayout;
    private Vibrator myVib;
    private FirebaseAuth auth;
    private String utentecorrente;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private String uid;
    private Map<String, String> tickets;
    private String[] preferiti;
    private List<String> chiavi;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            utentecorrente = auth.getCurrentUser().getUid();
        }

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
        setContentView(R.layout.activity_ticket);
        tickets=new HashMap<>();
        tvname = (TextView) findViewById(R.id.textName);
        tvname.setSelected(true);
        tvdescription = (TextView) findViewById(R.id.txtDesc);
        tvcategory = (TextView) findViewById(R.id.textCategory);
        tvcategory.setSelected(true);
        
        tvcity = (TextView) findViewById(R.id.textCity);
        tvdate = findViewById(R.id.txtDate);
        tvregion = (TextView) findViewById(R.id.txtRegion);
        tvprice = (TextView) findViewById(R.id.textPrice);
        img = (ImageView) findViewById(R.id.ticketthumbnail);
        imag = (ImageView) findViewById(R.id.categorythumbnail);

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
        tvcategory.setText(Category);
        tvdescription.setText(Description);
        tvprice.setText(Price);
        tvcity.setText(City);
        tvregion.setText(Region);
        tvdate.setText(Date);
        Picasso.get()
                .load(image)
                //.placeholder(R.drawable.roundloading)
                .fit()
                .centerCrop()
                .into(img);




    }
}