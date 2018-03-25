package com.bas.bandclient.models;

import com.bas.bandclient.models.db.OnePresetModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bas on 3/13/18.
 */

public class DataToPlayForOnePreset {

    private final OnePreset onePreset;
    private final List<NoteToPlay> noteToPlays = new ArrayList<>();

    public DataToPlayForOnePreset(OnePreset onePreset) {
        this.onePreset = onePreset;
    }

    public void addNoteToPlay(NoteToPlay noteToPlay) {
        noteToPlays.add(noteToPlay);
    }

    public NoteToPlay getLastNote() {
        if (noteToPlays.size() == 0) {
            return null;
        }

        return noteToPlays.get(noteToPlays.size()-1);
    }
}
