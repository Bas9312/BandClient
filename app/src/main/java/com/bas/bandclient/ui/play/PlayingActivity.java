package com.bas.bandclient.ui.play;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bas.bandclient.BandClientApplication;
import com.bas.bandclient.Consts;
import com.bas.bandclient.R;
import com.bas.bandclient.helpers.ConvertHelper;
import com.bas.bandclient.models.DataToPlayForOnePreset;
import com.bas.bandclient.models.Note;
import com.bas.bandclient.models.NoteToPlay;
import com.bas.bandclient.models.PresetManager;
import com.bas.bandclient.models.db.OneNoteModel;
import com.bas.bandclient.models.db.OnePresetModel;
import com.bas.bandclient.ui.widgets.NoteView;
import com.bas.bandclient.ui.widgets.OneVisualNote;
import com.bas.bandclient.ui.widgets.PlayingNoteView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bas on 23.11.16.
 */

public class PlayingActivity extends AppCompatActivity {
    @BindView(R.id.flFrame)
    FrameLayout flFrame;

    private ArrayList<OneVisualNote> noteViews = new ArrayList<>();

    private OnePresetModel preset;

    private ArrayDeque<NoteToPlay> playingQueue = new ArrayDeque<>();

    private Handler handler = new Handler();

    private Runnable runnable;

    private long timeOfStart;

    private HashMap<String, PlayingNoteView> oneVisualNotePlayingNoteViewHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_instruments_visual_editor);
        getSupportActionBar().show();
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ButterKnife.bind(this);
        Integer presetId = getIntent().getIntExtra("presetId", -1);
        if (presetId != -1) preset = OnePresetModel.getById(presetId);

        if (preset == null) {
            Toast.makeText(this, "Preset is null", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        getSupportActionBar().setTitle(preset.getPresetName());

        for (OneNoteModel note : preset.getNotes()) {
            Resources r = getResources();
            int px = (int) ConvertHelper.pxFromDp(this, note.getX());
            int py = (int) ConvertHelper.pxFromDp(this, note.getY());
            noteViews.add(new OneVisualNote(note.getNote(), note.getSize(), px, py));
        }
        restoreInstruments();

        runnable = new Runnable() {
            @Override
            public void run() {
                Iterator<NoteToPlay> it = playingQueue.iterator();

                List<PlayingNoteView> changedNotes = new ArrayList<>();

                while (it.hasNext()) {
                    NoteToPlay nextNote = it.next();
                    if (nextNote == null) return;

                    PlayingNoteView playingNoteView = getNoteViewByNote(nextNote);
                    if (playingNoteView == null) return;

                    long diffTime = nextNote.getTimeInMs() - (System.currentTimeMillis() - timeOfStart);


                    if (diffTime <= 0) {//TODO: Добавить момент удара
                        playingNoteView.setColor(Color.BLACK);
                        playingNoteView.setText(null);
                        playingQueue.remove();
                    } else {
                        if (diffTime < 500) {
                            if (!changedNotes.contains(playingNoteView)) {
                                playingNoteView.setColor(Color.argb((int) (Math.max(50, 255 - (diffTime / 500.0) * 255)), 255, 0, 0));
                                playingNoteView.setText(String.format("%.1fs", diffTime * 1.0 / 1000));
                                changedNotes.add(playingNoteView);
                            }
                        } else {
                            break;
                        }
                    }

                    playingNoteView.invalidate();
                }
                handler.postDelayed(runnable, 25);
            }
        };
    }

    protected void onResume() {
        super.onResume();

        BandClientApplication.getContext().getStorage().put(Consts.PREFS_CURRENT_NOTES, PresetManager.getNotesString(preset));
        BandClientApplication.getContext().setOnStartPlay(new BandClientApplication.OnStartPlay() {
            @Override
            public void onStartPlay(DataToPlayForOnePreset dataToPlayForOnePreset) {
                playingQueue = new ArrayDeque<>(dataToPlayForOnePreset.getNotes());
                timeOfStart = System.currentTimeMillis();
                handler.postDelayed(runnable, 1000);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        BandClientApplication.getContext().getStorage().put(Consts.PREFS_CURRENT_NOTES, "");
        BandClientApplication.getContext().setOnStartPlay(null);
    }

    private PlayingNoteView getNoteViewByNote(NoteToPlay nextNote) {
        return oneVisualNotePlayingNoteViewHashMap.get(nextNote.getNote().toString());
    }

    private void restoreInstruments() {
        clearCircles();
        for (OneVisualNote note : noteViews) {
            addCircle(note);
        }
    }

    private void clearCircles() {
        flFrame.removeAllViewsInLayout();
    }

    private void addCircle(OneVisualNote oneNote) {
        System.out.println("Add circle :" + oneNote.toString());
        PlayingNoteView noteView = new PlayingNoteView(this);
        noteView.setNote(oneNote);
        int size = oneNote.size != null ? oneNote.size.intValue() : (int) getResources().getDimension(R.dimen.circle_size_in_dp);
        noteView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        oneVisualNotePlayingNoteViewHashMap.put(oneNote.getNote().toString(), noteView);

        flFrame.addView(noteView);

        noteView.animate()
                .x(noteView.getLeft() + oneNote.x)
                .y(noteView.getRight() + oneNote.y)
                .setDuration(0)
                .start();
    }
}
