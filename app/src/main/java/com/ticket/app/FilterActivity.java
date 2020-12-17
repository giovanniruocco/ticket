package com.ticket.app;

import android.content.Intent;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.reverse;

public class FilterActivity extends AppCompatActivity {

    private RecyclerView myrv;
    private List<Ticket> listatickets;
    private RecyclerViewAdapter myAdapter;
    private DatabaseReference myRef;
    private String ftcategory,ftregion,ftcity,name;
    private boolean provenienza;
    private int numero,conta=0,conto=0;
    private TextView filtro,errore;
    private ImageView immagine;
    private boolean ordine=false;
    private View v;
    String resultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        v = findViewById(android.R.id.content);

        filtro = (TextView)findViewById(R.id.filter_result);
        errore = (TextView)findViewById(R.id.filter_error);
        immagine=(ImageView)findViewById(R.id.filter_image);
        immagine.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        ftcategory=intent.getExtras().getString("Category");
        ftregion=intent.getExtras().getString("Region");
        ftcity=intent.getExtras().getString("City");
        provenienza = intent.getExtras().getBoolean("Provenienza");
        listatickets = new ArrayList<>();
        myRef= FirebaseDatabase.getInstance().getReference("Tickets");
        myrv = (RecyclerView) findViewById(R.id.recyclerview_filter);
        myrv.setLayoutManager(new GridLayoutManager(this, 1));
        if(provenienza){
            setTitle("Find the ticket");
            name =intent.getExtras().getString("Name");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listatickets.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Ticket ticket = snapshot.getValue(Ticket.class);
                            if (ticket.getName().toLowerCase().contains(name.toLowerCase()))
                                listatickets.add(ticket);
                        }
                        if (listatickets.isEmpty()) {
                            filtro.setVisibility(View.INVISIBLE);
                            errore.setText("The search did not return any results.");
                            immagine.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            filtro.setText("Results for name:\n" +name);
                            reverse(listatickets);
                            errore.setVisibility(View.INVISIBLE);
                            myAdapter=new RecyclerViewAdapter( FilterActivity.this,listatickets);
                            myrv.setAdapter(myAdapter);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {

            setTitle(ftcategory);
            Query query;
            if (ftcategory.equals("Any event")){
                query = myRef.orderByChild("category");}
            else{
                query = myRef.orderByChild("category").equalTo(ftcategory);}
            resultText= "Results of the search for:\nCategory: " + ftcategory;
            query.addListenerForSingleValueEvent(valueEventListener);
        }
    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            listatickets.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ticket ticket = snapshot.getValue(Ticket.class);
                    listatickets.add(ticket);

                    if (!ftregion.isEmpty())
                    {
                        for (Ticket a : listatickets) {
                            if (!(a.getRegion().equalsIgnoreCase(ftregion))) {
                                listatickets.remove(a);
                            }
                        }
                    }

                    if (!ftcity.isEmpty())
                    {
                        for (Ticket a : listatickets) {
                            if (!(a.getCity().equalsIgnoreCase(ftcity))) {
                                listatickets.remove(a);
                            }
                        }
                    }
                }
                if (ftregion.isEmpty())
                    resultText += "\nRegion: Any region";
                else
                    resultText += "\nRegion: " + ftregion;
                if (ftcity.isEmpty())
                    resultText += "\nCity: Any city";
                else
                    resultText += "\nCity: " + ftcity;

                if (listatickets.isEmpty()){
                    filtro.setText(resultText);
                    errore.setText("The search did not return any results.");
                    immagine.setVisibility(View.VISIBLE);
                }
                else {
                    filtro.setText(resultText);
                    errore.setVisibility(View.INVISIBLE);
                    reverse(listatickets);
                    myAdapter=new RecyclerViewAdapter( FilterActivity.this,listatickets);
                    myrv.setAdapter(myAdapter);
                }
            }
        }



        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        MenuInflater inflauto = getMenuInflater();
        inflauto.inflate(R.menu.search_right_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.ordina)
        {
                            conta++;
                            if (ordine)
                                conta=1;
                            ordine=false;
                            reverse(listatickets);
                            myAdapter=new RecyclerViewAdapter( FilterActivity.this,listatickets);
                            myrv.setAdapter(myAdapter);
                            if(conta%2==0) {
                                //item.setIcon(R.drawable.ic_discendant_sort);
                                Snackbar.make(v,"From the most recent post",Snackbar.LENGTH_SHORT).show();
                            }
                            else {
                                //item.setIcon(R.drawable.ic_ascendant_sort);
                                Snackbar.make(v,"From the oldest post",Snackbar.LENGTH_SHORT).show();
                            }
                        }

        return super.onOptionsItemSelected(item);
    }

}
