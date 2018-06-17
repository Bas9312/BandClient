package com.bas.bandclient.models;

import com.bas.bandclient.models.db.OnePresetModel;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by bas on 3/13/18.
 */

public class DataToPlayForOnePreset {

    private final OnePreset onePreset;
    private final Stack<NoteToPlay> noteToPlays = new Stack<>();
    private Long timeOfStart = null;

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


    public String serialize(Long timeOfStart) {
        this.timeOfStart = timeOfStart;
        return new Gson().toJson(this);
    }

    public static DataToPlayForOnePreset deserialize(String data) {
        return new Gson().fromJson(data, DataToPlayForOnePreset.class);
    }

    public OnePreset getOnePreset() {
        return onePreset;
    }

    public Collection<NoteToPlay> getNotes() {
        return noteToPlays;
    }

    public Long getTimeOfStart() {
        return timeOfStart;
    }

    public void setTimeOfStart(Long timeOfStart) {
        this.timeOfStart = timeOfStart;
    }
}
