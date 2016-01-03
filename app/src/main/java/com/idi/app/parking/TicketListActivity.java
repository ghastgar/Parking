package com.idi.app.parking;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private TextView fromDate;
    private TextView fromTime;
    private TextView toDate;
    private TextView toTime;
    private Calendar fromC;
    private Calendar toC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);

        earnings = (TextView) findViewById(R.id.earnings);
        activity = this;
        db = new ParkingTicketOpenHelper(getApplicationContext());
        listView = (ListView) findViewById(R.id.ticketsList);
        fromDate = (TextView) findViewById(R.id.fromDate);
        fromTime = (TextView) findViewById(R.id.fromTime);
        toDate = (TextView) findViewById(R.id.toDate);
        toTime = (TextView) findViewById(R.id.toTime);

        if (savedInstanceState != null) {
            long fromLong = savedInstanceState.getLong("fromLong");
            long toLong   = savedInstanceState.getLong("toLong");
            fromC = Calendar.getInstance();
            toC   = Calendar.getInstance();
            fromC.setTime(new Date(fromLong));
            toC.setTime(new Date(toLong));
        } else {
            toC = Calendar.getInstance();
            toC.set(Calendar.SECOND, 59);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            fromC = calendar;
        }
        updateInterval();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong("fromLong", fromC.getTimeInMillis());
        outState.putLong("toLong",   toC.getTimeInMillis());
        super.onSaveInstanceState(outState);
    }

    private void updateList() {
        long from = fromC.getTimeInMillis();
        long to   = toC.getTimeInMillis();
        tickets = db.getTicketsBetweenDates(from, to);
        adapter = new TicketListCustomAdapter(getApplicationContext(), tickets);
        listView.setAdapter(adapter);
    }

    private void updateInterval() {
        updateList();
        updateTextViews();
    }

    private void updateTextViews() {
        DateFormat df = DateFormat.getDateInstance();
        DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);

        fromDate.setText("De: " + df.format(fromC.getTime()));
        fromTime.setText(tf.format(fromC.getTime()));
        toDate.setText("A:   " + df.format(toC.getTime()));
        toTime.setText(tf.format(toC.getTime()));

        double total = 0.0;
        for (Ticket ticket:tickets) {
            total += ticket.getPrice();
        }
        String totalString = String.format("%.2f", total);
        earnings.setText("Recaptació total: " + totalString + "€");
    }

    public void showFromDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fromC.set(year, monthOfYear, dayOfMonth);
                updateInterval();
            }
        };
        newFragment.show(getFragmentManager(), "fromDatePicker");
    }

    public void showFromTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                fromC.set(Calendar.HOUR_OF_DAY, hourOfDay);
                fromC.set(Calendar.MINUTE, minute);
                updateInterval();
            }
        };
        newFragment.show(getFragmentManager(), "fromTimePicker");
    }

    public void showToDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                toC.set(year, monthOfYear, dayOfMonth);
                updateInterval();
            }
        };
        newFragment.show(getFragmentManager(), "toDatePicker");
    }

    public void showToTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                toC.set(Calendar.HOUR_OF_DAY, hourOfDay);
                toC.set(Calendar.MINUTE, minute);
                updateInterval();
            }
        };
        newFragment.show(getFragmentManager(), "toTimePicker");
    }
}
