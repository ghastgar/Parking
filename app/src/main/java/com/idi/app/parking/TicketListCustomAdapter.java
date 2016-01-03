package com.idi.app.parking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pau on 03/01/16.
 */
public class TicketListCustomAdapter extends ArrayAdapter {

    private Context context;
    private List<Ticket> tickets;

    public TicketListCustomAdapter(Context context, ArrayList<Ticket> tickets) {
        super(context, R.layout.ticket_list_item, tickets);
        this.context = context;
        this.tickets = tickets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.ticket_list_item, parent, false);
        TextView licensePlateTV = (TextView) view.findViewById(R.id.licensePlateTV);
        TextView spotTV     = (TextView) view.findViewById(R.id.spotTV);
        TextView dateInTV   = (TextView) view.findViewById(R.id.dateInTV);
        TextView dateOutTV  = (TextView) view.findViewById(R.id.dateOutTV);
        TextView price      = (TextView) view.findViewById(R.id.priceTV);

        Ticket ticket = tickets.get(position);
        if (ticket != null) {
            licensePlateTV.setText(ticket.getLicensePlate());
            String text = "Plaça " + ticket.getParkingSpot();
            spotTV.setText(text);
            DateFormat df = DateFormat.getDateTimeInstance();
            String text1 = "Entrada: " + df.format(ticket.getDateIn());
            dateInTV.setText(text1);
            String text2 = "Sortida:  " + df.format(ticket.getDateOut());
            dateOutTV.setText(text2);
            String priceString = String.format("%.2f", ticket.getPrice()) + "€";
            price.setText(priceString);
        }
        return view;
    }

}
