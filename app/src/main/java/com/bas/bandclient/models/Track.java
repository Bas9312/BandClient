package com.bas.bandclient.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bas on 27.11.17.
 */

public class Track implements Serializable {
    private final List<NoteToPlay> noteToPlays;
    private final String name;
    private InstrumentType type;

    public Track(List<NoteToPlay> noteToPlays, String name) {
        this.noteToPlays = noteToPlays;
        this.name = name;
    }

    public List<NoteToPlay> getNoteToPlays() {
        return noteToPlays;
    }

    public String getName() {
        return name;
    }

    public void setType(InstrumentType type) {
        this.type = type;
    }

    public InstrumentType getType() {
        return type;
    }
}
