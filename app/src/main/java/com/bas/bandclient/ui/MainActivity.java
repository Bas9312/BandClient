package com.bas.bandclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bas.bandclient.R;
import com.bas.bandclient.helpers.CompositionBinder;
import com.bas.bandclient.helpers.FileReadHelper;
import com.bas.bandclient.models.Composition;
import com.bas.bandclient.models.DataToPlay;
import com.bas.bandclient.models.InstrumentType;
import com.bas.bandclient.models.Note;
import com.bas.bandclient.models.OnePreset;
import com.bas.bandclient.models.Track;
import com.bas.bandclient.ui.play.PlayingActivity;
import com.bas.bandclient.ui.searchDevices.SearchDevicesActivity;
import com.ndmsystems.infrastructure.logging.LogHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    @BindView(R.id.btnInstrumentsList)
    Button btnInstrumentsList;

    @BindView(R.id.btnTypesList)
    Button btnTypesList;

    @BindView(R.id.btnReadFile)
    Button btnReadFile;

    @BindView(R.id.btnPlaying)
    Button btnPlaying;

    @BindView(R.id.btnConduct)
    Button btnConduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Composition composition = FileReadHelper.readFile();
        List<OnePreset> presetList = new ArrayList<>();
        List<Note> noteList = Arrays.asList(new Note(Note.D_SHARP, Note.Octave.GREAT), new Note(Note.A, Note.Octave.GREAT), new Note(Note.D, Note.Octave.GREAT));
        OnePreset onePreset = new OnePreset("test", InstrumentType.fromString("blop"), noteList);
        presetList.add(onePreset);

        DataToPlay dataToPlay = CompositionBinder.bind(composition, presetList);
        LogHelper.e("Data to play: " + (dataToPlay == null ? "null" : dataToPlay.toString()));

        btnInstrumentsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PresetsListActivity.class));
            }
        });

        btnTypesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TypesListActivity.class));
            }
        });


        btnReadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Composition composition = FileReadHelper.readFile();
                for (Track oneTrack : composition.getTrackList()) {
                    Log.d("Main", "Track: " + oneTrack.getName() + ", number of notes: " + oneTrack.getNoteToPlays().size());
                }
                startActivity(new Intent(MainActivity.this, TracksListActivity.class)
                        .putExtra(TracksListActivity.COMPOSITION_EXTRA, composition));

                /*HashMap<Note, Integer> usedNotes = new HashMap<>();
                if (noteEvents != null) {
                    for (NoteToPlay noteToPlay : noteEvents) {
                        System.out.println("Tick: " + noteToPlay.getTimeInMs() + ", length: " + noteToPlay.getLengthInMs() + ", note: " + noteToPlay.getNote().toString());
                        if (!usedNotes.containsKey(noteToPlay.getNote())) usedNotes.put(noteToPlay.getNote(), 1);
                        else usedNotes.put(noteToPlay.getNote(), usedNotes.get(noteToPlay.getNote()) + 1);
                    }

                    System.out.println("Number of different notes: " + usedNotes.size());
                    for (Note key : usedNotes.keySet()) {
                        System.out.println(key + " " + usedNotes.get(key));
                    }
                }*/
            }
        });

        btnPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SelectPresetActivity.class));
            }
        });

        btnConduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchDevicesActivity.class));
            }
        });
    }
}
