package com.bas.bandclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.bas.bandclient.R;
import com.bas.bandclient.models.Composition;
import com.bas.bandclient.models.InstrumentType;
import com.bas.bandclient.ui.searchDevices.SearchDevicesActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TracksListActivity extends AppCompatActivity {

    public static final String COMPOSITION_EXTRA = "COMPOSITION_EXTRA";
    @BindView(R.id.lvTracksList)
    ListView lvTracksList;

    @BindView(R.id.next)
    Button next;

    private TracksListAdapter adapter;

    Composition composition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        setContentView(R.layout.activity_tracks_list);
        getSupportActionBar().show();
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        composition = (Composition) getIntent().getSerializableExtra(COMPOSITION_EXTRA);

        adapter = new TracksListAdapter(composition.getTrackList(), this);
        lvTracksList.setAdapter(adapter);
        lvTracksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popupMenu = new PopupMenu(TracksListActivity.this, view);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        adapter.setTrackSelected(position, menuItem.getTitle().toString());
                        return true;
                    }

                });

                for (InstrumentType type : InstrumentType.getListOfTypes()) {
                    popupMenu.getMenu().add(type.toString());
                }

                popupMenu.show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TracksListActivity.this, SearchDevicesActivity.class).putExtra(COMPOSITION_EXTRA, composition));
            }
        });
    }
}
