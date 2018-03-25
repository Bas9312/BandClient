package com.bas.bandclient.models;

import com.bas.bandclient.models.db.OnePresetModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by bas on 3/13/18.
 */

public class DataToPlayForOnePreset {

    private final OnePreset onePreset;
    private final Stack<NoteToPlay> noteToPlays = new Stack<>();

    public DataToPlayForOnePreset(OnePreset onePreset) {
        this.onePreset = onePreset;
    }

    public void addNoteToPlay(NoteToPlay noteToPlay) {
        noteToPlays.push(noteToPlay);
    }

    public NoteToPlay getLastNote() {
        if (noteToPlays.size() == 0) {
            return null;
        }

        return noteToPlays.get(noteToPlays.size()-1);
    }

    public void removeLastNote() {
        noteToPlays.pop();
    }
}
