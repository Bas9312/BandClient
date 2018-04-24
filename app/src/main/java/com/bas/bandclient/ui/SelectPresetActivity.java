package com.bas.bandclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bas.bandclient.R;
import com.bas.bandclient.models.db.OnePresetModel;
import com.bas.bandclient.ui.play.PlayingActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bas on 10.11.16.
 */

public class SelectPresetActivity extends AppCompatActivity {

    @BindView(R.id.lvInstrumentsList)
    ListView lvInstrumentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        setContentView(R.layout.activity_instruments_list);
        getSupportActionBar().show();
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        lvInstrumentsList.setAdapter(new PresetsListAdapter(OnePresetModel.getAll(), this, false));
        lvInstrumentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<OnePresetModel> allPresets = OnePresetModel.getAll();
                OnePresetModel preset = allPresets.get(position);
                startActivity(new Intent(SelectPresetActivity.this, PlayingActivity.class).putExtra("presetId", preset.getId()));
            }
        });
    }
}
