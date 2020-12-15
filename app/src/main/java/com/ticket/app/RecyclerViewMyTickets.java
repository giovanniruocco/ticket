package com.ticket.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

public class RecyclerViewMyTickets extends RecyclerView.Adapter<RecyclerViewMyTickets.MyViewHolder> {
    private Context mContext ;
    private List<Ticket> mData ;
    private String uid;
    private Vibrator myVib;
    private DatabaseReference myRef;
    private String Name,Category,Description,Price,Date,Tel,Email,Region,City,Thumbnail,Utente;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public RecyclerViewMyTickets(Context mContext, List<Ticket> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_my_tickets,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        myRef= FirebaseDatabase.getInstance().getReference("Tickets");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Ticket currentticket= mData.get(position);
        holder.tv_ticket_name.setText(mData.get(position).getName());
        holder.tv_ticket_category.setText(mData.get(position).getCategory());

        uid=mData.get(position).getUid();
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            Utente = auth.getCurrentUser().getUid();
        }

        holder.tv_ticket_price.setText(mData.get(position).getPrice() + " €");
        Picasso.get()
                .load(currentticket.getThumbnail())
                //.placeholder(R.drawable.caricacuore)
                .fit()
                .centerCrop()
                .into(holder.img_ticket_thumbnail);
        Name=mData.get(position).getName();
        Category=mData.get(position).getCategory();
        Description=mData.get(position).getDescription();
        Region=mData.get(position).getRegion();
        City=mData.get(position).getCity();
        Price=mData.get(position).getPrice();
        Date=mData.get(position).getDate();
        Tel=mData.get(position).getTel();
        Email=mData.get(position).getEmail();
        Thumbnail=mData.get(position).getThumbnail();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TicketActivity.class);
                intent.putExtra("Name",mData.get(position).getName());
                intent.putExtra("Category",mData.get(position).getCategory());
                intent.putExtra("Description",mData.get(position).getDescription());
                intent.putExtra("Region",mData.get(position).getRegion());
                intent.putExtra("City",mData.get(position).getCity());
                intent.putExtra("Price",mData.get(position).getPrice());
                intent.putExtra("Email",mData.get(position).getEmail());
                intent.putExtra("Tel",mData.get(position).getTel());
                intent.putExtra("Date",mData.get(position).getDate());
                intent.putExtra("Thumbnail",mData.get(position).getThumbnail());
                intent.putExtra("Uid",mData.get(position).getUid());
                mContext.startActivity(intent);
            }
        });


       /*
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                final CharSequence[] items={"Edit dog", "Delete dog","Back"};
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("What do you want to do?");
                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("Edit dog")) {
                            Intent intento = new Intent(mContext,EditActivity.class);
                            intento.putExtra("Name",mData.get(position).getName());
                            intento.putExtra("Breed",mData.get(position).getBreed());
                            intento.putExtra("Description",mData.get(position).getDescription());
                            intento.putExtra("Gender",mData.get(position).getGender());
                            intento.putExtra("City",mData.get(position).getCity());
                            intento.putExtra("Age",mData.get(position).getAge());
                            intento.putExtra("Tel",mData.get(position).getTel());
                            intento.putExtra("Email",mData.get(position).getEmail());
                            intento.putExtra("Image",mData.get(position).getThumbnail());
                            intento.putExtra("Uid",mData.get(position).getUid());
                            mContext.startActivity(intento);
                            dialogInterface.dismiss();
                        }else if (items[i].equals("Back")) {
                            dialogInterface.dismiss();
                        }
                        else if (items[i].equals("Delete dog"))
                        {
                            dialogInterface.dismiss();

                            new AlertDialog.Builder(mContext)
                                    .setMessage("Are you sure you want to delete " + mData.get(position).getName() +"?")
                                    .setCancelable(true)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            myRef.child(mData.get(position).getUid()).setValue(null);
                                            StorageReference cancella = storage.getReferenceFromUrl(mData.get(position).getThumbnail());
                                            cancella.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            });
                                            mContext.startActivity(new Intent(mContext,ProfileActivity.class));
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        }
                    }
                });
                builder.show();
                myVib=(Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
                return true;
            }
        });

        */

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ticket_name;
        TextView tv_ticket_category;
        TextView tv_ticket_price;
        ImageView img_ticket_thumbnail;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_ticket_name = (TextView) itemView.findViewById(R.id.ticket_name_id_profile) ;
            tv_ticket_category=(TextView) itemView.findViewById(R.id.ticket_category_id_profile);
            tv_ticket_price=(TextView)itemView.findViewById(R.id.ticket_price_profile);
            img_ticket_thumbnail = (ImageView) itemView.findViewById(R.id.ticket_img_id_profile);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id_profile);
        }
    }
}