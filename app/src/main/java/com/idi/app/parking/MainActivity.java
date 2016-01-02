package com.idi.app.parking;

import android.app.DialogFragment;
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
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddCarDialogFragment.NoticeDialogListener{

    private int NSPOTS = 15;
    private ArrayList<Ticket> tickets;

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
            }
        });

        final GridView gridView = (GridView) findViewById(R.id.parking_grid);

        tickets = new ArrayList<Ticket>();
        for (int i = 1; i <= NSPOTS; i++) tickets.add(new Ticket(i, "ABC"+i));
        tickets.set(7, null);
        final MyCustomAdapter adapter = new MyCustomAdapter(getApplicationContext(), tickets);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ticket ticket = tickets.get(position);
                if (ticket == null) {
                    DialogFragment fragment = new AddCarDialogFragment();
                    fragment.show(getFragmentManager(), "check-in");
                }
                else {
                    ticket.checkout();
                    Toast.makeText(
                            getApplicationContext(),
                            "You clicked position " + position + ", ticket is " + ticket.getPrice() + "€",
                            Toast.LENGTH_SHORT)
                            .show();
                    //tickets.set(position, new Ticket(position + 1, "new plate number"));
                    //adapter.notifyDataSetChanged();
                }
            }
        });
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
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d("PosClick", "hey ho");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
