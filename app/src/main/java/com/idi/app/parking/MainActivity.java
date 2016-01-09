package com.idi.app.parking;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.text.DateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddCarDialogFragment.NoticeDialogListener{

    private int NSPOTS = 15;
    private ArrayList<Ticket> tickets;
    private ParkingGridCustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), TicketListActivity.class);
                if (mIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mIntent);
                }
            }
        });

        final GridView gridView = (GridView) findViewById(R.id.parking_grid);

        tickets = new ArrayList<Ticket>();
        for (int i = 1; i <= NSPOTS; i++) tickets.add(null);

        ParkingTicketOpenHelper db = new ParkingTicketOpenHelper(getApplicationContext());
        tickets = db.getOpenTickets();

        adapter = new ParkingGridCustomAdapter(getApplicationContext(), tickets);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ticket ticket = tickets.get(position);
                if (ticket == null) {
                    DialogFragment fragment = AddCarDialogFragment.newInstance(position+1);
                    fragment.show(getFragmentManager(), "check-in");
                }
                else {
                    showCheckOutDialog(position, ticket);
                }
            }
        });
    }

    private void showCheckOutDialog(final int position, final Ticket ticket) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sortida de vehicle")
                .setMessage("Vols generar el tiquet i deixar lliure la plaça "+ (position+1) +"?")
                .setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Checkout cancelled
                        dialog.cancel();
                    }
                })
                .setPositiveButton("D'acord", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ticket.checkout(getApplicationContext());
                        showTicket(ticket);
                        tickets.set(position, null);
                        adapter.notifyDataSetChanged();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showTicket(Ticket ticket) {
        DateFormat df = DateFormat.getDateTimeInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Tiquet cotxe " + ticket.getLicensePlate())
                .setMessage("Entrada: " + df.format(ticket.getDateIn()) + "\n" +
                            "Sortida:  " + df.format(ticket.getDateOut()) + "\n" +
                            "\nPreu: " + String.format("%.2f", ticket.getPrice()) + "€ \n"
                ).setPositiveButton("Fet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent mIntent = new Intent(getApplicationContext(), AboutActivity.class);
            if (mIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mIntent);
            }
        }
        else if (id == R.id.action_help) {
            Intent mIntent = new Intent(getApplicationContext(), HelpActivity.class);
            if (mIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mIntent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int spot, String inputLicensePlate) {
        Log.d("PosClick", inputLicensePlate);
        Ticket ticket = new Ticket(spot, inputLicensePlate, getApplicationContext());
        tickets.set(spot-1, ticket);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
