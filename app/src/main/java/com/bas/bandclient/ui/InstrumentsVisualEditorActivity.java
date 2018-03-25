package com.bas.bandclient.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bas.bandclient.R;
import com.bas.bandclient.helpers.ConvertHelper;
import com.bas.bandclient.models.PresetManager;
import com.bas.bandclient.models.db.OneNoteModel;
import com.bas.bandclient.models.db.OnePresetModel;
import com.bas.bandclient.ui.widgets.NoteView;
import com.bas.bandclient.ui.widgets.OneVisualNote;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bas on 23.11.16.
 */

public class InstrumentsVisualEditorActivity extends AppCompatActivity {
    @BindView(R.id.flFrame)
    FrameLayout flFrame;

    private ArrayList<OneVisualNote> noteViews = new ArrayList<>();

    private OnePresetModel preset;

    private boolean isDataChanged = false;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_instrument, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_instrument:
                addCircle();
                break;
            case R.id.action_save_instruments:
                saveCircles();
                break;
            case R.id.action_clear_instruments:
                clearCircles();
                break;
            case R.id.action_restore_instruments:
                restoreInstruments();
                break;
            case R.id.action_plus_size:
                plusInstrumentsSize();
                break;
            case R.id.action_minus_size:
                minusInstrumentsSize();
                break;
        }
        return true;
    }

    private void minusInstrumentsSize() {
        isDataChanged = true;
        for (OneVisualNote note : noteViews) {
            note.minusSize();
        }

        for (int i = 0; i < flFrame.getChildCount(); i++) {
            NoteView noteView = (NoteView) flFrame.getChildAt(i);
            noteView.minusSize();
        }
    }

    private void plusInstrumentsSize() {
        isDataChanged = true;
        for (OneVisualNote note : noteViews) {
            note.plusSize();
        }

        for (int i = 0; i < flFrame.getChildCount(); i++) {
            NoteView noteView = (NoteView) flFrame.getChildAt(i);
            noteView.plusSize();
        }
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

    private void saveCircles() {
        System.out.println("Number of childes: " + flFrame.getChildCount());
        noteViews.clear();

        int[] parentLocation = new int[2];
        flFrame.getLocationInWindow(parentLocation);
        for (int i = 0; i < flFrame.getChildCount(); i++) {
            int[] location = new int[2];
            NoteView noteView = (NoteView) flFrame.getChildAt(i);
            noteView.getLocationInWindow(location);
            location[0] -= parentLocation[0];
            location[1] -= parentLocation[1];

            OneVisualNote forSave = new OneVisualNote(noteView.getNote(), noteView.getNoteSize(), location[0], location[1]);
            noteViews.add(forSave);
            System.out.println("Save child " + i + ": " + forSave.toString());
        }

        PresetManager.saveNotes(preset, noteViews);
    }


    private void addCircle() {
        isDataChanged = true;
        NoteView noteView = new NoteView(this);
        int size = (int) getResources().getDimension(R.dimen.circle_size_in_dp);
        noteView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        noteView.setNote(new OneVisualNote(0,0));
        flFrame.addView(noteView);
    }

    private void addCircle(OneVisualNote oneNote) {
        System.out.println("Add circle :" + oneNote.toString());
        NoteView noteView = new NoteView(this);
        noteView.setNote(oneNote);
        int size = oneNote.size != null ? oneNote.size.intValue() : (int) getResources().getDimension(R.dimen.circle_size_in_dp);
        noteView.setLayoutParams(new ViewGroup.LayoutParams(size, size));

        flFrame.addView(noteView);

        noteView.animate()
                .x(noteView.getLeft() + oneNote.x)
                .y(noteView.getRight() + oneNote.y)
                .setDuration(0)
                .start();
    }

    private boolean isDataChanged() {
        if (isDataChanged) return true;

        if (flFrame.getChildCount() != noteViews.size()) return true;

        int[] parentLocation = new int[2];
        flFrame.getLocationInWindow(parentLocation);
        for (int i = 0; i < flFrame.getChildCount(); i++) {
            int[] location = new int[2];
            NoteView noteView = (NoteView) flFrame.getChildAt(i);
            noteView.getLocationInWindow(location);
            location[0] -= parentLocation[0];
            location[1] -= parentLocation[1];

            if (location[0] != noteViews.get(i).x
                    || location[1] != noteViews.get(i).y
                    || noteView.getNote() != noteViews.get(i).getNote()) return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isDataChanged()) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.confirm_save)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveCircles();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        } else super.onBackPressed();
    }
}
