package com.idi.app.parking;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddCarDialogFragment.NoticeDialogListener{

    private int NSPOTS = 15;
    private ArrayList<Ticket> tickets;
    private MyCustomAdapter adapter;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //Intent mIntent = new Intent(getApplicationContext(), Recaptacio.class);
                //startActivity(mIntent);
            }
        });

        final GridView gridView = (GridView) findViewById(R.id.parking_grid);

        tickets = new ArrayList<Ticket>();
        //for (int i = 1; i <= NSPOTS; i++) tickets.add(new Ticket(i, "ABC"+i));
        for (int i = 1; i <= NSPOTS; i++) tickets.add(null);

        ParkingTicketOpenHelper db = new ParkingTicketOpenHelper(getApplicationContext());
        tickets = db.getOpenTickets();

        adapter = new MyCustomAdapter(getApplicationContext(), tickets);
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
                    /*ticket.checkout(getApplicationContext());
                    Toast.makeText(
                            getApplicationContext(),
                            "You clicked position " + position + ", ticket is " + ticket.getPrice() + "€",
                            Toast.LENGTH_SHORT)
                            .show();
                    tickets.set(position, null);
                    adapter.notifyDataSetChanged();*/
                    //tickets.set(position, new Ticket(position + 1, "new plate number"));
                    //adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void showCheckOutDialog(final int position, final Ticket ticket) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Retirar cotxe")
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
                        tickets.set(position, null);
                        adapter.notifyDataSetChanged();
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
        if (id == R.id.action_settings) {
            return true;
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

    }
}
