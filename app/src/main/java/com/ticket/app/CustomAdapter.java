package com.ticket.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int categories[];
    String[] categoryNames;
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, int[] categories, String[] categoryNames) {
        this.context = applicationContext;
        this.categories = categories;
        this.categoryNames = categoryNames;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        icon.setImageResource(categories[i]);
        names.setText(categoryNames[i]);
        return view;
    }
}
