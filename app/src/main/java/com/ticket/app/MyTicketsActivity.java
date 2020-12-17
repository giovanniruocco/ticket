package com.ticket.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;
import static java.util.Collections.reverse;

public class MyTicketsActivity extends AppCompatActivity {

    private FloatingActionButton addticket;
    private FirebaseAuth auth;
    private TextView welcome;
    private Vibrator myVib;
    private RecyclerView myrv;
    private List<Ticket> listatickets;
    private RecyclerViewMyTickets myAdapter;
    private DatabaseReference myRef,myRef2;
    private String utente,email;
    private ImageView immagine;
    private DatabaseReference mDatabase;
    GoogleSignInClient mGoogleSignInClient;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mDatabase=FirebaseDatabase.getInstance().getReference();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MyTicketsActivity.this);


        welcome=(TextView)findViewById(R.id.mytickets_welcome);
        immagine=(ImageView)findViewById(R.id.mytickets_image);
        immagine.setVisibility(View.INVISIBLE);
        setTitle("My Tickets");
        auth = FirebaseAuth.getInstance();
        final Intent loginact = new Intent(MyTicketsActivity.this,LoginActivity.class);
        if(auth.getCurrentUser() != null) {
            utente = auth.getCurrentUser().getUid();
            email=auth.getCurrentUser().getEmail();
        }
        else startActivity(loginact);
        addticket=(FloatingActionButton)findViewById(R.id.mytickets_add);
        myVib=(Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        listatickets = new ArrayList<>();
        myRef= FirebaseDatabase.getInstance().getReference("Tickets");
        myRef2=FirebaseDatabase.getInstance().getReference("Users");
        final Query query=myRef.orderByChild("utente").equalTo(utente);
        query.addListenerForSingleValueEvent(valueEventListener);
        Query profilo=myRef2.orderByChild("email").equalTo(email);
        profilo.addListenerForSingleValueEvent(evento);
        myrv = (RecyclerView) findViewById(R.id.mytickets_recyclerview);
        myrv.setLayoutManager(new GridLayoutManager(this, 1));
        final Intent intentmain = new Intent(this, MainActivity.class);
        addticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser()!=null)
                    startActivity(new Intent(MyTicketsActivity.this, AddActivity.class));
                else {
                    startActivity(new Intent(MyTicketsActivity.this, LoginActivity.class));
                    Toast.makeText(MyTicketsActivity.this, "You must be logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipemytickets);
        swipeRefreshLayout.setColorSchemeResources(R.color.viola,R.color.Rosso,R.color.verde);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                query.addListenerForSingleValueEvent(valueEventListener);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            listatickets.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ticket ticket = snapshot.getValue(Ticket.class);
                    listatickets.add(ticket);
                }
                reverse(listatickets);
                myAdapter=new RecyclerViewMyTickets( MyTicketsActivity.this,listatickets);
                myrv.setAdapter(myAdapter);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ValueEventListener evento = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snap: dataSnapshot.getChildren())
            {
                User user=snap.getValue(User.class);
                if (listatickets.isEmpty())
                {
                    welcome.setText("Welcome, " + user.getName() + " " + user.getSurname() +"\nAdd your tickets.");
                    immagine.setVisibility(View.VISIBLE);
                }
                else
                    welcome.setText("Welcome, " + user.getName() + " " + user.getSurname() +"\nThese are your tickets.");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };



    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MyTicketsActivity.this,MainActivity.class));
    }


}