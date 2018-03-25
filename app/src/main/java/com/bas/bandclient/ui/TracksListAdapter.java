package com.bas.bandclient.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bas.bandclient.R;
import com.bas.bandclient.models.InstrumentType;
import com.bas.bandclient.models.Track;
import com.bas.bandclient.models.db.OnePresetModel;

import java.util.List;


public class TracksListAdapter extends ArrayAdapter<Track> {

    private final List<Track> tracks;
    private final String[] presets;
    private final Activity context;
    private final LayoutInflater mLayoutInflater;

    public TracksListAdapter(List<Track> allTracks, Activity context) {
        super(context, R.layout.one_instrument_element, allTracks);
        this.tracks = allTracks;
        this.presets = new String[tracks.size()];
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup viewGroup) {
        final Track track = tracks.get(pos);

        View view = mLayoutInflater.inflate(R.layout.one_instrument_element, null);
        TextView textView = (TextView) view.findViewById(R.id.tvName);
        if (textView != null) textView.setText(track.getName());
        if (presets[pos] != null) view.setBackgroundColor(context.getResources().getColor(R.color.button_green));

        return view;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Track getItem(int pos) {
        return tracks.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setTrackSelected(int position, String presetName) {
        tracks.get(position).setType(InstrumentType.fromString(presetName));
        notifyDataSetChanged();
    }
}
