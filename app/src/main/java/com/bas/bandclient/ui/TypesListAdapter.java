package com.bas.bandclient.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bas.bandclient.R;
import com.bas.bandclient.models.InstrumentType;
import com.bas.bandclient.models.db.OnePresetModel;

import java.util.List;


public class TypesListAdapter extends ArrayAdapter<InstrumentType> {

    private final List<InstrumentType> instruments;
    private final Activity context;
    private final LayoutInflater mLayoutInflater;

    public TypesListAdapter(List<InstrumentType> allInstruments, Activity context) {
        super(context, R.layout.one_instrument_element, allInstruments);
        this.instruments = allInstruments;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.instruments.add(null);
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup viewGroup) {
        final InstrumentType device = instruments.get(pos);

        if (device != null) {
            View view = mLayoutInflater.inflate(R.layout.one_instrument_element, null);
            TextView textView = (TextView) view.findViewById(R.id.tvName);
            if (textView != null) textView.setText(device.toString());
            return view;
        } else {
            View view = mLayoutInflater.inflate(R.layout.add_instrument_element, null);
            return view;
        }
    }

    @Override
    public int getCount() {
        return instruments.size();
    }

    @Override
    public InstrumentType getItem(int pos) {
        return instruments.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
