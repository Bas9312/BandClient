package com.bas.bandclient.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bas.bandclient.R;
import com.bas.bandclient.models.db.OneTypeModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bas on 10.11.16.
 */

public class EnterTypeNameActivity extends AppCompatActivity {

    @BindView(R.id.etInstrumentName)
    EditText etInstrumentName;

    @BindView(R.id.btnNext)
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        setContentView(R.layout.activity_one_type);
        getSupportActionBar().show();
        ButterKnife.bind(this);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneTypeModel.addNewModel(etInstrumentName.getText().toString());
                finish();
            }
        });
    }
}
