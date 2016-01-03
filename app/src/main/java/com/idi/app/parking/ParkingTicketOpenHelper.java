package com.idi.app.parking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by pau on 02/01/16.
 */
public class ParkingTicketOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tickets.db";
    private static final String TICKETS_TABLE_NAME = "tickets";
    private static final int PARKING_SIZE = 15;

    // Tickets table column names
    private static String KEY_SPOT = "spot";
    private static String KEY_ID = "id";
    private static String KEY_LICENSE_PLACE = "licenese_plate";
    private static String KEY_DATETIME_IN   = "datetime_in";
    private static String KEY_DATETIME_OUT  = "datetime_out";
    private static String KEY_PRICE = "price";

    private static final String TICKETS_TABLE_CREATE =
            "CREATE TABLE " + TICKETS_TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_SPOT + " INTEGER, " +
                    KEY_LICENSE_PLACE + " TEXT, " +
                    KEY_DATETIME_IN + " INTEGER, " +
                    KEY_DATETIME_OUT + " INTEGER, " +
                    KEY_PRICE + " TEXT" + // TODO: ojo
            ");";


    public ParkingTicketOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Post: returns row ID of the new inserted ticket or -1 if error
    public int addTicket(int spot, String licensePlate, long dateTimeIN) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SPOT, spot);
        values.put(KEY_LICENSE_PLACE, licensePlate);
        values.put(KEY_DATETIME_IN, dateTimeIN/1000);
        long res = db.insert(TICKETS_TABLE_NAME, null, values);
        db.close();
        return (int) res;
    }

    // Post: returns number of closed tickets (should be at most 1)
    public long closeOpenTicket(long id, long dateTimeOut, String price) {
        String filter = KEY_ID + "=" + id;
        ContentValues values = new ContentValues();
        values.put(KEY_DATETIME_OUT, dateTimeOut/1000);
        values.put(KEY_PRICE, price);

        SQLiteDatabase db = this.getWritableDatabase();
        long updatedRows = db.update(TICKETS_TABLE_NAME, values, filter, null);
        db.close();
        Log.d("closeOpenTicket", "updated " + updatedRows + " rows");
        return updatedRows;
    }

    public ArrayList<Ticket> getOpenTickets() {
        SQLiteDatabase db = this.getReadableDatabase();
        //db.query(distinct, TICKETS_TABLE_NAME, );

        ArrayList<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < PARKING_SIZE; i++) tickets.add(null);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TICKETS_TABLE_NAME);
        queryBuilder.appendWhere(KEY_DATETIME_OUT + " IS NULL");
        //queryBuilder.query(db, projectionIn, selection, selArgs, groupBy, having, sortOrder);

        Cursor c = queryBuilder.query(db, null, null, null, null, null, null);
        c.moveToFirst();
        if (c.getCount() > 15) Log.e("ParkingTicketOpenHelper", "Too much open tickets.");
        while (!c.isAfterLast()) {
            int id      = c.getInt(c.getColumnIndex(KEY_ID));
            int spot    = c.getInt(c.getColumnIndex(KEY_SPOT));
            String lp   = c.getString(c.getColumnIndex(KEY_LICENSE_PLACE));
            long dateIn = c.getInt(c.getColumnIndex(KEY_DATETIME_IN));
            Ticket ticket = new Ticket(id, spot, lp, dateIn*1000);
            Log.e("Helper", "dateIn guardat : "+ dateIn);
            tickets.set(spot-1, ticket);
            c.moveToNext();
        }
        return tickets;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TICKETS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete the old db if there was any
        db.execSQL("DROP TABLE IF EXISTS " + TICKETS_TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public ArrayList<Ticket> getTodayTickets() {
        long now = Calendar.getInstance().getTimeInMillis()/1000;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        long midnight = calendar.getTimeInMillis()/1000;
        return getTicketsBetweenDates(midnight, now);
    }

    private ArrayList<Ticket> getTicketsBetweenDates(long from, long to) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Ticket> tickets = new ArrayList<>();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TICKETS_TABLE_NAME);
        queryBuilder.appendWhere(KEY_DATETIME_OUT + " IS NOT NULL AND ");
        queryBuilder.appendWhere(KEY_DATETIME_IN + " >= " + from + " AND ");
        queryBuilder.appendWhere(KEY_DATETIME_OUT + " <= " + to);
        //queryBuilder.query(db, projectionIn, selection, selArgs, groupBy, having, sortOrder);

        Cursor c = queryBuilder.query(db, null, null, null, null, null, KEY_DATETIME_IN + " ASC");
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int id      = c.getInt(c.getColumnIndex(KEY_ID));
            int spot    = c.getInt(c.getColumnIndex(KEY_SPOT));
            String lp   = c.getString(c.getColumnIndex(KEY_LICENSE_PLACE));
            long dateIn = c.getInt(c.getColumnIndex(KEY_DATETIME_IN))*1000;
            long dateOut= c.getInt(c.getColumnIndex(KEY_DATETIME_OUT))*1000;
            String s    = c.getString(c.getColumnIndex(KEY_PRICE));
            double price = Double.parseDouble(s);
            Ticket ticket = new Ticket(id, spot, lp, dateIn*1000, dateOut*1000, price);
            tickets.add(ticket);
            c.moveToNext();
        }
        return tickets;
    }
}
