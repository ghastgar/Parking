package com.idi.app.parking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pau on 01/01/16.
 */
public class MyCustomAdapter extends ArrayAdapter {
    private ArrayList<String> strings;
    private ArrayList<Ticket> tickets;
    private Context context;

    public MyCustomAdapter(Context context, ArrayList<Ticket> tickets) {
        super(context, R.layout.custom_list_item, tickets);
        this.context = context;
        this.tickets = tickets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_list_item, parent, false);
        TextView idTV = (TextView) view.findViewById(R.id.spotId);
        TextView licensePlateTV = (TextView) view.findViewById(R.id.licensePlate);

        Ticket ticket = tickets.get(position);
        if (ticket != null) {
            idTV.setText(ticket.getParkingSpot()+"");
            licensePlateTV.setText(ticket.getLicensePlate());
            idTV.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            idTV.setText((position+1)+"");
            licensePlateTV.setText("lliure");
        }
        return view;
    }
}
