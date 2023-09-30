package com.example.gpsproject;


import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<LocInfo> {
    List<LocInfo> list;
    Context context;
    int xmlResource;
    int selectedIndex = -1;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<LocInfo> objects) {
        super(context, resource, objects);
        xmlResource = resource;
        list = objects;
        this.context = context;

    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource, null);

        TextView addressesVisited = adapterLayout.findViewById(R.id.addressesVisited);
        TextView timeSpent = adapterLayout.findViewById(R.id.timeSpent);

        timeSpent.setText(list.get(position).getTime() + "");
        addressesVisited.setText(list.get(position).getAddress() + "");


        return adapterLayout;

    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}