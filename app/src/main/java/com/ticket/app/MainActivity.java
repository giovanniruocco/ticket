package com.ticket.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;



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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.reverse;

public class MainActivity extends AppCompatActivity {
    private NavigationView nv;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private DrawerLayout activity_main;
    private DatabaseReference UsersRef;
    private FirebaseAuth auth;
    private FloatingActionButton addbutton;
    private TextView tvnamelogin, tvemaillogin;
    private ImageView loginImage;
    private String user;
    private RecyclerView myrv;
    private List<Ticket> listatickets,tickets;
    private RecyclerViewAdapter myAdapter;
    private DatabaseReference myRef,myRef2;
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


        UsersRef = FirebaseDatabase.getInstance().getReference("Users");

        if (auth.getCurrentUser() != null) {
            user = auth.getCurrentUser().getEmail();
            Query query = UsersRef.orderByChild("email").equalTo(user);
            query.addListenerForSingleValueEvent(evento);
        }
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        NavigationView navView = (NavigationView) findViewById(R.id.nv);
        View headerView = navView.getHeaderView(0);

        loginImage = (ImageView) headerView.findViewById(R.id.loginImage);
        loginImage.setImageResource(R.drawable.ic_profile);
        tvnamelogin = (TextView) headerView.findViewById(R.id.navigation_name);
        tvemaillogin = (TextView) headerView.findViewById(R.id.navigation_email);

        activity_main = (DrawerLayout) findViewById(R.id.dl);


        if (auth.getCurrentUser() != null) {

            NavigationView navigationView = (NavigationView) findViewById(R.id.nv);

            Menu menu = navigationView.getMenu();
            MenuItem login = menu.findItem(R.id.nv_login);
            login.setTitle("Logout");
            login.setIcon(R.drawable.ic_logout);
        }


        listatickets = new ArrayList<>();


        addbutton=(FloatingActionButton)findViewById(R.id.main_add);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser()!=null)
                    startActivity(new Intent(MainActivity.this, AddActivity.class));
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
            }
        });

        myRef = FirebaseDatabase.getInstance().getReference("Tickets");
        myRef2 = FirebaseDatabase.getInstance().getReference("Users");
        myrv = (RecyclerView) findViewById(R.id.recyclerview_id);
        myrv.setLayoutManager(new GridLayoutManager(this, 1));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listatickets.clear();
                for (DataSnapshot posSnapshot : dataSnapshot.getChildren()) {
                    Ticket ticket = posSnapshot.getValue(Ticket.class);
                    listatickets.add(ticket);
                    //caricamento.setVisibility(View.INVISIBLE);
                }
                tickets = new ArrayList<>(listatickets);
                reverse(listatickets);
                myAdapter = new RecyclerViewAdapter(MainActivity.this, listatickets);
                myrv.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //caricamento.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "DATABASE ERROR", Toast.LENGTH_SHORT).show();
            }
        });
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipelayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.prova, R.color.Rosso, R.color.verde);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        swipeRefreshLayout.setRefreshing(true);

                                                        myRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                listatickets.clear();
                                                                for (DataSnapshot posSnapshot : dataSnapshot.getChildren()) {
                                                                    Ticket ticket = posSnapshot.getValue(Ticket.class);
                                                                    listatickets.add(ticket);
                                                                    //caricamento.setVisibility(View.INVISIBLE);
                                                                }
                                                                reverse(listatickets);
                                                                swipeRefreshLayout.setRefreshing(false);

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                //caricamento.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(MainActivity.this, "DATABASE ERROR", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });





        dl = findViewById(R.id.dl);
        t = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
        t.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(t);
        t.syncState();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                nv = (NavigationView) findViewById(R.id.nv);
                nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {

                            case R.id.nv_login:
                                if (auth.getCurrentUser() != null) {
                                    auth.signOut();
                                    mGoogleSignInClient.signOut();
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                } else {
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    Toast.makeText(MainActivity.this, "Loginno ", Toast.LENGTH_LONG).show();
                                }
                                break;

                            case R.id.nv_profile:
                                if (auth.getCurrentUser() != null) {
                                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                    Toast.makeText(MainActivity.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
                                } else {
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
                            case R.id.nv_mytickets:
                                if (auth.getCurrentUser() != null) {
                                    startActivity(new Intent(MainActivity.this, MyTicketsActivity.class));
                                } else {
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
