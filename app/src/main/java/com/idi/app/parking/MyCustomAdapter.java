package com.idi.app.parking;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by pau on 01/01/16.
 */
public class MyCustomAdapter extends ArrayAdapter {
    private ArrayList<String> strings;
    private ArrayList<Ticket> tiquets;

    public MyCustomAdapter(Context context, ArrayList<String> objects) {
        super(context, R.layout.custom_list_item, objects);
        this.strings = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
