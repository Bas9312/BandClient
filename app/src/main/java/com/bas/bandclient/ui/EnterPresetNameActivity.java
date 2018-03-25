package com.bas.bandclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bas.bandclient.R;
import com.bas.bandclient.models.InstrumentType;
import com.bas.bandclient.models.PresetManager;
import com.bas.bandclient.models.db.OnePresetModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bas on 10.11.16.
 */

public class EnterPresetNameActivity extends AppCompatActivity {

    @Bind(R.id.etInstrumentName)
    EditText etInstrumentName;

    @Bind(R.id.spInstrumentType)
    Spinner spInstrumentType;

    @Bind(R.id.btnNext)
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        setContentView(R.layout.activity_one_instrument);
        getSupportActionBar().show();
        ButterKnife.bind(this);

        spInstrumentType.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, InstrumentType.getListOfTypes()));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnePresetModel preset = OnePresetModel.createNewModel(etInstrumentName.getText().toString());
                PresetManager.saveType(preset, (InstrumentType) spInstrumentType.getSelectedItem());
                startActivity(new Intent(EnterPresetNameActivity.this, InstrumentsVisualEditorActivity.class).putExtra("presetId", preset.getId()));
                finish();
            }
        });
    }
}
