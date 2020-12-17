package com.ticket.app;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static java.util.Collections.reverse;


public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private DatabaseReference UsersRef;
    private String user;
    private String user_name, user_surname, user_tel, user_email, user_image;
    ImageView profileImage;


    TextView name, surname, mail, cell, tv_name;
    /*Button logout, gippiesse;*/
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        profileImage = (ImageView) findViewById(R.id.profileImage);
        profileImage.setImageResource(R.drawable.ic_profile);

        /*Button refresh = findViewById(R.id.refresh);*/

        /*refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity(new Intent(gps.this, gps.class));
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                Toast.makeText(ProfileActivity.this, "Refresh", Toast.LENGTH_SHORT).show();

            }
        });*/

        mDatabase = FirebaseDatabase.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");


        auth = FirebaseAuth.getInstance();
       /* logout = findViewById(R.id.logout);
        gippiesse = findViewById(R.id.gippiesse);*/
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        mail = findViewById(R.id.mail);
        cell = findViewById(R.id.cell);
        tv_name = findViewById(R.id.tv_name);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //auth = FirebaseAuth.getInstance();


        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (auth.getCurrentUser() != null) {
            //name.setText(auth.getCurrentUser().getDisplayName());
            mail.setText(auth.getCurrentUser().getEmail());
            cell.setText("Cellulare non inserito");
            Query query = UsersRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
            query.addListenerForSingleValueEvent(evento);
        }

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(ProfileActivity.this);
        if (acct != null && mDatabase.child("Users").child(acct.getId()).child("email").toString() != acct.getEmail()) { //mando nel database tutti i valore gel google account
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase.child("Users").child(personId).child("email").setValue(personEmail);
            mDatabase.child("Users").child(personId).child("name").setValue(personGivenName);
            mDatabase.child("Users").child(personId).child("surname").setValue(personFamilyName);
            //mDatabase.child("Users").child(personId).child("cell").setValue(null);
        }


 /*       logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                mGoogleSignInClient.signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                Toast.makeText(ProfileActivity.this, "Logout effettuato con successo", Toast.LENGTH_SHORT).show();

            }
        });

        gippiesse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestgps();

                if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(ProfileActivity.this, gps.class));

                    Toast.makeText(ProfileActivity.this, "GPS", Toast.LENGTH_SHORT).show();
                } *//*else
                    Toast.makeText(ProfileActivity.this, "GPS Permission denied", Toast.LENGTH_SHORT).show();
                    *//*

            }
        });*/


    }

    ValueEventListener evento = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                User user = snap.getValue(User.class);
                if (!(user.getName().equals(""))) {
                    user_name=user.getName();
                    user_surname= user.getSurname();
                    user_email = user.getEmail();
                    name.setText(user.getName());
                    surname.setText(user.getSurname());
                    tv_name.setText(user.getName() + " " + user.getSurname());
                }
                if (user.getCell() != null) {
                    user_tel = user.getCell();
                    cell.setText(user.getCell());
                }
                if (user.getImage() != null) {
                    user_image = user.getImage();
                    Picasso.get()
                            .load(user_image)
                            .placeholder(R.drawable.roundloading)
                            .fit()
                            .centerCrop()
                            .into(profileImage);
                }

            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void requestgps() {
        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        MenuInflater inflauto = getMenuInflater();
        inflauto.inflate(R.menu.profile_right_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.edit)
        {
            Intent intento = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intento.putExtra("Name", user_name);
            intento.putExtra("Surname", user_surname);
            intento.putExtra("Cell", user_tel);
            intento.putExtra("Email", user_email);
            intento.putExtra("Image", user_image);
            overridePendingTransition(0, 0);
            startActivity(intento);
            overridePendingTransition(0, 0);
        }

        return super.onOptionsItemSelected(item);
    }

}
