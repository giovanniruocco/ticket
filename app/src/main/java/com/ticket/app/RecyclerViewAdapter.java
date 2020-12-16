package com.ticket.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext ;
    private List<Ticket> mData ;

    public RecyclerViewAdapter(Context mContext, List<Ticket> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_ticket,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Ticket currentticket= mData.get(position);
        holder.tv_ticket_name.setText(mData.get(position).getName());
        holder.tv_ticket_category.setText(mData.get(position).getCategory());

        switch (currentticket.getCategory()) {
            case "Music" :
                holder.cat_image.setImageResource(R.drawable.ic_music);
                break;

            case "Football" :
                holder.cat_image.setImageResource(R.drawable.ic_football);
                break;

            case "Theater" :
                holder.cat_image.setImageResource(R.drawable.ic_theater);
                break;

            case "Cinema" :
                holder.cat_image.setImageResource(R.drawable.ic_popcorn);
                break;

            case "Flights" :
                holder.cat_image.setImageResource(R.drawable.ic_airplane);
                break;

            case "Train" :
                holder.cat_image.setImageResource(R.drawable.ic_train);
                break;

            case "Other events" :
                holder.cat_image.setImageResource(R.drawable.ic_more);
                break;
        }

        holder.tv_ticket_price.setText(mData.get(position).getPrice() + " €");
        Picasso.get()
                .load(currentticket.getThumbnail())
                //.placeholder(R.drawable.caricacuore)
                .fit()
                .centerCrop()
                .into(holder.img_ticket_thumbnail);
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
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ticket_name;
        TextView tv_ticket_category;
        TextView tv_ticket_price;
        ImageView img_ticket_thumbnail, cat_image;
        CardView cardView ;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_ticket_name = (TextView) itemView.findViewById(R.id.ticket_name_id) ;
            tv_ticket_category=(TextView) itemView.findViewById(R.id.ticket_category_id);
            tv_ticket_price=(TextView) itemView.findViewById(R.id.ticket_price_id);
            img_ticket_thumbnail = (ImageView) itemView.findViewById(R.id.ticket_img_id);
            cat_image = (ImageView) itemView.findViewById(R.id.cat_img);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }
}