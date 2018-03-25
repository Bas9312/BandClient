package com.bas.bandclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bas.bandclient.R;
import com.bas.bandclient.helpers.FileReadHelper;
import com.bas.bandclient.models.Composition;
import com.bas.bandclient.models.Note;
import com.bas.bandclient.models.NoteToPlay;
import com.bas.bandclient.models.Track;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    @Bind(R.id.btnInstrumentsList)
    Button btnInstrumentsList;

    @Bind(R.id.btnTypesList)
    Button btnTypesList;

    @Bind(R.id.btnReadFile)
    Button btnReadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btnInstrumentsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InstrumentsListActivity.class));
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
    }
}