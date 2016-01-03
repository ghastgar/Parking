package com.idi.app.parking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pau on 02/01/16.
 */
public class TicketListActivity extends AppCompatActivity {

    private TicketListActivity activity;
    private ParkingTicketOpenHelper db;
    private ListView listView;
    private ArrayList<Ticket> tickets;
    private TicketListCustomAdapter adapter;
    private TextView earnings;
    private long from;
    private long to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);

        Intent intent = getIntent();
        from = intent.getExtras().getLong("from");
        to = intent.getExtras().getLong("to");

        earnings = (TextView) findViewById(R.id.earnings);
        activity = this;
        db = new ParkingTicketOpenHelper(getApplicationContext());
        listView = (ListView) findViewById(R.id.ticketsList);

        if (from == 0 || to == 0) {
            tickets  = db.getTodayTickets();
        } else tickets = db.getTicketsBetweenDates(from, to);


        adapter = new TicketListCustomAdapter(getApplicationContext(), tickets);
        listView.setAdapter(adapter);

        double total = 0.0;
        for (Ticket ticket:tickets) total += ticket.getPrice();
        String totalString = String.format("%.2f", total);
        earnings.setText("Recaptació total: " + totalString + "€");
    }
}
