package com.idi.app.parking;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AddCarDialogFragment.NoticeDialogListener{

    private int NSPOTS = 15;
    private ArrayList<Ticket> tickets;
    private ParkingGridCustomAdapter adapter;
    private ParkingTicketOpenHelper db;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
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

        db = new ParkingTicketOpenHelper(getApplicationContext());
        tickets = db.getOpenTickets();

        adapter = new ParkingGridCustomAdapter(getApplicationContext(), tickets);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ticket ticket = tickets.get(position);
                if (ticket == null) {
                    DialogFragment fragment = AddCarDialogFragment.newInstance(position + 1);
                    fragment.show(getFragmentManager(), "check-in");
                } else {
                    showCheckOutDialog(position, ticket);
                }
            }
        });
    }

    private void showCheckOutDialog(final int position, final Ticket ticket) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sortida de vehicle")
                .setMessage("Vols generar el tiquet i deixar lliure la plaça " + (position + 1) + "?")
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
        else if (id == R.id.action_export) {
            File f = null;
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String timeStamp = sdf.format(cal.getTime());
            try {

                String extr = Environment.getExternalStorageDirectory().toString();
                File mFolder = new File(extr + "/csv");
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }

                String fileName = "DBtiquets_" + timeStamp + ".csv";

                f = new File(mFolder.getAbsolutePath(), fileName);
                int i = 1;
                while (!f.createNewFile()) {
                    fileName = "DBtiquets_" + timeStamp + "_" + i + ".csv";
                    ++i;
                    f = new File(mFolder.getAbsolutePath(), fileName);
                }
                FileOutputStream fOut = new FileOutputStream(f);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append("Número tiquet,Plaça,Matrícula,Entrada,Sortida,Preu");
                myOutWriter.append("\n");
                myOutWriter.append(db.getDBContentsForCSV());
                myOutWriter.close();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error exportant les dades",
                        Toast.LENGTH_SHORT).show();

            }
            if (f != null) Toast.makeText(getApplicationContext(),
                    "Activitat històrica del pàrquing exportada a " + f.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int spot, String inputLicensePlate) {
        Log.d("PosClick", inputLicensePlate);
        for (int i = 0; i < tickets.size(); ++i) {
            if (tickets.get(i) != null) {
                Ticket ticket = tickets.get(i);
                if (ticket.getLicensePlate().equals(inputLicensePlate)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Aquest vehicle ja és al pàrquing! Està aparcat a la plaça " + (i+1) + ". Tria una altra matrícula.",
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }
            }
        }
        Ticket ticket = new Ticket(spot, inputLicensePlate, getApplicationContext());
        tickets.set(spot-1, ticket);
        adapter.notifyDataSetChanged();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
