package com.ticket.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.animation.MotionSpec;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.reverse;

public class MainActivity extends AppCompatActivity {
    private NavigationView nv;
    private DrawerLayout dl;
    private int numero,conta=0,conto=0;
    private SearchView searchView;
    private ActionBarDrawerToggle t;
    private DrawerLayout activity_main;
    private DatabaseReference UsersRef;
    private FirebaseAuth auth;
    private FloatingActionButton addbutton;
    private TextView tvnamelogin, tvemaillogin;
    private ImageView loginImage;
    private String user, user_image;
    private RecyclerView myrv;
    private List<Ticket> listatickets,tickets;
    private RecyclerViewAdapter myAdapter;
    private DatabaseReference myRef,myRef2;
    private boolean ordine=false;
    GoogleSignInClient mGoogleSignInClient;
    private Vibrator myVib;
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            startActivity(new Intent(MainActivity.this, SliderActivity.class));
            // first time task

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }


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
        myVib=(Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        addbutton=(FloatingActionButton)findViewById(R.id.main_add);
        MotionSpec hide_spec = MotionSpec.createFromResource(MainActivity.this , R.animator.hide_spec);
        addbutton.setHideMotionSpec(hide_spec);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(25);
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

        myrv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    /*ObjectAnimator animation = ObjectAnimator.ofFloat(addbutton, "translationY", 260f);
                    animation.setDuration(60);
                    animation.start();*/
                    addbutton.hide();
                } else if (dy < 0) {
                    /*ObjectAnimator animation = ObjectAnimator.ofFloat(addbutton, "translationY", -9.5f);
                    animation.setDuration(60);
                    animation.start();*/
                    addbutton.show();
                }
            }
        });

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
        swipeRefreshLayout.setColorSchemeResources(R.color.viola, R.color.Rosso, R.color.verde);
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
                                myVib.vibrate(25);
                                if (auth.getCurrentUser() != null) {
                                    auth.signOut();
                                    mGoogleSignInClient.signOut();
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                } else {
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                }
                                break;

                            case R.id.nv_profile:
                                if (auth.getCurrentUser() != null) {
                                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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
                            case R.id.about:
                                startActivity(new Intent(MainActivity.this, SliderActivity.class));
                                break;
                            case R.id.info:
                                //startActivity(new Intent(MainActivity.this,DevelopersActivity.class));
                                break;
                            case R.id.feedback:
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setData(Uri.parse("mailto: "));
                                String[] contatto = {"ticket@engineer.com"};
                                intent.putExtra(Intent.EXTRA_EMAIL, contatto);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Ticket:Feedback");
                                intent.putExtra(Intent.EXTRA_TEXT, "I would like to report the following bug: ");
                                intent.setType("message/rfc822");
                                Intent chooser = Intent.createChooser(intent, "Send Email");
                                startActivity(chooser);
                                break;
                            case R.id.cheers:
                                String urlString = "https://www.youtube.com/watch?v=6xUnSVTh8fI";
                                Intent intento = new Intent(Intent.ACTION_VIEW,Uri.parse(urlString));
                                intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                intento.setPackage(null);
                                startActivity(intento);

                                break;


                        }
                        return true;
                    }
                });
            }

    ValueEventListener evento = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                User user = snap.getValue(User.class);
                tvnamelogin.setText(user.getName() + " " + user.getSurname());
                tvemaillogin.setText(user.getEmail());

                if (user.getImage() != null) {
                    user_image = user.getImage();
                    Picasso.get()
                            .load(user_image)
                            .placeholder(R.drawable.roundloading)
                            .fit()
                            .centerCrop()
                            .into(loginImage);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {

        MenuInflater inflauto = getMenuInflater();
        inflauto.inflate(R.menu.right_menu,menu);
        MenuItem cerca=menu.findItem(R.id.app_bar_search);
        cerca.setIcon(R.drawable.ic_search);
        searchView = (SearchView)cerca.getActionView();
        searchView.setQueryHint("Search for name...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intento = new Intent(MainActivity.this,FilterActivity.class);
                intento.putExtra("Name",query);
                intento.putExtra("Provenienza",true);
                startActivity(intento);
                searchView.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) searchViewMenuItem.getActionView();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(t.onOptionsItemSelected(item))
            return true;
        if (item.getItemId()==R.id.filtra) {
            startActivity(new Intent(MainActivity.this,FilterSearchActivity.class));
            return true;
        }


        if (item.getItemId()==R.id.ordina)
            changeOrder();

        return super.onOptionsItemSelected(item);
    }

    public void Click(View v){
        myVib.vibrate(25);
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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
    }


    public void onBackPressed() {
        addbutton.hide();
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to close Ticket?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void changeOrder(){
        conta++;
        if (ordine)
            conta=1;
        ordine=false;
        reverse(listatickets);
        myAdapter=new RecyclerViewAdapter( MainActivity.this,listatickets);
        myrv.setAdapter(myAdapter);
        if(conta%2==0) {
            //item.setIcon(R.drawable.ic_discendant_sort);
            Snackbar snackBar = Snackbar.make(activity_main, "From the most recent post", Snackbar.LENGTH_SHORT);
            snackBar.show();
        }
        else {
            //item.setIcon(R.drawable.ic_ascendant_sort);
            Snackbar snackBar = Snackbar.make(activity_main, "From the oldest post ", Snackbar.LENGTH_SHORT);
            snackBar.show();
        }
    }

}
