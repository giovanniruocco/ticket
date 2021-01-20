package com.ticket.app;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    //Tutorial Class
    public int[] lst_images = {
           R.mipmap.ic_launcher_round,
            R.drawable.guide_1,
            R.drawable.guide_3,
            R.drawable.guide_2,
            R.drawable.ic_baseline_perm_phone_msg_24,
            R.drawable.ic_baseline_chat_24

    };
    public String[] lst_title = {
            "THE DREAM EVENT",
            "TICKETS FOR EVERYONE",
            "LOOK FOR TICKETS",
            "ADD YOUR PREFERENCES",
            "CONTACT THE OWNER",
            "REAL-TIME CHAT ROOM"
    };
    public String[] lst_description = {
            "The goal of our application is to help you finding/selling a ticket to a fantastic event.",
            "Choose from a list of tickets between many categories",
            "Waste no time with our search function!",
            "Use our filter function to find what you are looking for",
            "After you find your event you can contact the owner via email or telephone",
            "Use our real-time chat to talk to other users"
    };
    public int[]  lst_backgroundcolor = {
            Color.rgb(55,55,55),
            Color.rgb(239,85,85),
            Color.rgb(52,89,149),
            Color.rgb(75,150,75),
            Color.rgb(88, 164, 176),
            Color.rgb(157, 203, 186)
    };

    public SlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return lst_title.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    //Instantiate tutorial slider
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slider,container,false);
        LinearLayout layoutslide = (LinearLayout) view.findViewById(R.id.sliderlinearlayout);
        ImageView imgslide = (ImageView)  view.findViewById(R.id.sliderimg);
        TextView txttitle= (TextView) view.findViewById(R.id.txttitle);
        TextView description = (TextView) view.findViewById(R.id.txtdescription);
        layoutslide.setBackgroundColor(lst_backgroundcolor[position]);
        imgslide.setImageResource(lst_images[position]);
        txttitle.setText(lst_title[position]);
        description.setText(lst_description[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}