package com.bas.bandclient.ui.play;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bas.bandclient.BandClientApplication;
import com.bas.bandclient.Consts;
import com.bas.bandclient.R;
import com.bas.bandclient.helpers.ConvertHelper;
import com.bas.bandclient.models.DataToPlayForOnePreset;
import com.bas.bandclient.models.NoteToPlay;
import com.bas.bandclient.models.PresetManager;
import com.bas.bandclient.models.db.OneNoteModel;
import com.bas.bandclient.models.db.OnePresetModel;
import com.bas.bandclient.ui.widgets.OneVisualNote;
import com.bas.bandclient.ui.widgets.PlayingNoteView;
import com.ndmsystems.infrastructure.logging.LogHelper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
    private final Object flag = new Object();

    private Handler handler = new Handler();

    private Runnable runnable;

    private long timeOfStart;

    public enum Variant {First, Second, Third};

    private Variant variant = Variant.Third;

    private HashMap<String, PlayingNoteView> oneVisualNotePlayingNoteViewHashMap = new HashMap<>();

    private double timeOfStartVisual = 2000;

    private double timeOfEndVisual = 100;

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

        variant = BandClientApplication.getContext().getStorage().get(Consts.PREFS_PLAYING_VARIANT, Variant.class);

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
                Long currentTime = System.currentTimeMillis();
                if (currentTime > timeOfStart) {
                    flFrame.setBackgroundColor(Color.WHITE);
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
                            playingNoteView.setProcent(100, PlayingNoteView.TypeOfFilling.Circle);
                            playingNoteView.setText(null);
                            playingQueue.remove();
                        } else {
                            if (diffTime <= timeOfStartVisual) {
                                if (!changedNotes.contains(playingNoteView)) {
                                    if (diffTime < timeOfEndVisual) {
                                        playingNoteView.setProcent(100, PlayingNoteView.TypeOfFilling.Circle);
                                        playingNoteView.setColor(Color.GREEN);
                                    } else {
                                        if (variant == Variant.First) {
                                            playingNoteView.setColor(Color.argb((int) (Math.max(50, 255 - (diffTime / timeOfStartVisual) * 255)), 255, 0, 0));
                                        } else if (variant == Variant.Second) {
                                            playingNoteView.setProcent(100.0 - ((diffTime - timeOfEndVisual) * 100.0 / timeOfStartVisual), PlayingNoteView.TypeOfFilling.Circle);
                                            playingNoteView.setColor(Color.BLACK);
                                        } else if (variant == Variant.Third) {
                                            playingNoteView.setProcent(100.0 - ((diffTime - timeOfEndVisual) * 100.0 / timeOfStartVisual), PlayingNoteView.TypeOfFilling.BottomTop);
                                            playingNoteView.setColor(Color.BLACK);
                                        }
                                    }
                                    changedNotes.add(playingNoteView);
                                }
                            } else {
                                break;
                            }
                        }

                        playingNoteView.invalidate();
                    }

                } else {
                    if (timeOfStart - currentTime < 2000) {
                        int color = Color.argb((int) (((timeOfStart - currentTime) / 2000.0) * 255), 0, 0, 0);
                        flFrame.setBackgroundColor(color);
                    }
                }
                handler.postDelayed(runnable, 10);
            }
        };
    }

    protected void onResume() {
        super.onResume();

        BandClientApplication.getContext().getStorage().put(Consts.PREFS_CURRENT_NOTES, PresetManager.getNotesString(preset));
        BandClientApplication.getContext().setOnStartPlay(new BandClientApplication.OnStartPlay() {
            @Override
            public void onStartPlay(DataToPlayForOnePreset dataToPlayForOnePreset) {
                synchronized (flag) {
                    handler.removeCallbacks(runnable);
                    playingQueue = new ArrayDeque<>(dataToPlayForOnePreset.getNotes());
                    for (NoteToPlay noteToPlay : playingQueue) {
                        noteToPlay.setTimeInMs(noteToPlay.getTimeInMs() + 2000);
                    }
                    timeOfStart = dataToPlayForOnePreset.getTimeOfStart();
                    handler.post(runnable);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        BandClientApplication.getContext().getStorage().put(Consts.PREFS_CURRENT_NOTES, "");
        BandClientApplication.getContext().setOnStartPlay(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.change_type_of_playing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_simple:
                variant = Variant.First;
                break;
            case R.id.action_expanding:
                variant = Variant.Second;
                break;
            case R.id.action_bottom_top:
                variant = Variant.Third;
                break;
        }
        BandClientApplication.getContext().getStorage().put(Consts.PREFS_PLAYING_VARIANT, variant);
        return true;
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
