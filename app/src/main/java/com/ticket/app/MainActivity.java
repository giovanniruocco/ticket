package com.ticket.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private NavigationView nv;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private DrawerLayout activity_main;
    private DatabaseReference UsersRef;
    private FirebaseAuth auth;
    private TextView tvnamelogin, tvemaillogin;
    private ImageView immaginelogin;
    private String user;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();


        UsersRef= FirebaseDatabase.getInstance().getReference("Users");

        if(auth.getCurrentUser() != null) {
            user = auth.getCurrentUser().getEmail();
            Query query = UsersRef.orderByChild("email").equalTo(user);
            query.addListenerForSingleValueEvent(evento);
        }
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        NavigationView navigescionView = (NavigationView) findViewById(R.id.nv);
        View headerView = navigescionView.getHeaderView(0);

        immaginelogin= (ImageView) headerView.findViewById(R.id.immaginelogin);
        immaginelogin.setImageResource(R.drawable.ic_profile);
        tvnamelogin = (TextView) headerView.findViewById(R.id.navigation_name);
        tvemaillogin = (TextView) headerView.findViewById(R.id.navigation_email);

        activity_main = (DrawerLayout) findViewById(R.id.dl);


        if (auth.getCurrentUser()!=null){

            NavigationView navigationView = (NavigationView) findViewById(R.id.nv);

            Menu menu = navigationView.getMenu();
            MenuItem registrazione = menu.findItem(R.id.nv_login);
            registrazione.setTitle("Logout");
            registrazione.setIcon(R.drawable.ic_logout);
        }




        dl = findViewById(R.id.dl);
        t = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
        t.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {

                    case R.id.nv_login:
                        if(auth.getCurrentUser() != null){
                            auth.signOut();
                            mGoogleSignInClient.signOut();
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                        }
                        else {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            Toast.makeText(MainActivity.this, "Loginno ", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case R.id.nv_profile:
                        if(auth.getCurrentUser() != null) {
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                            Toast.makeText(MainActivity.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
                        }
                        else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("You must be logged in")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok, let's go", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                        }
                                    })
                                    .setNegativeButton("No, thanks", null)
                                    .show();
                        }
                        break;

                }
                return true;
            }
        });
        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    ValueEventListener evento = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                User user = snap.getValue(User.class);
                tvnamelogin.setText(user.getName() + " " + user.getSurname());
                tvemaillogin.setText(user.getEmail());
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void Clicckino(View v){
        if (auth.getCurrentUser()==null)
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        else
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
    }

}
