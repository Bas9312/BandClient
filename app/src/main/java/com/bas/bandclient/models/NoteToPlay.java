package com.bas.bandclient.models;

import com.ndmsystems.infrastructure.logging.LogHelper;

import java.io.Serializable;

/**
 * Created by bas on 25.10.17.
 */

public class NoteToPlay implements Comparable, Serializable {
    private final Note note;
    private final Long timeInMs;
    private Long lengthInMs;

    public NoteToPlay(Note note, Long timeInMs) {
        this.note = note;
        this.timeInMs = timeInMs;
    }

    public NoteToPlay(Note note, Long timeInMs, Long lengthInMs) {
        this.note = note;
        this.timeInMs = timeInMs;
        this.lengthInMs = lengthInMs;
    }

    @Override
    public int compareTo(Object other) {
        if (this == other)
            return 0;

        NoteToPlay otherNote = (NoteToPlay) other;
        int timeCompare = timeInMs.compareTo(otherNote.getTimeInMs());
        if (timeCompare != 0) return timeCompare;
        else {
            return note.compareTo(note);
        }
    }

    public int compareByTime(Object other) {
        if (this == other)
            return 0;

        NoteToPlay otherNote = (NoteToPlay) other;
        int timeCompare = timeInMs.compareTo(otherNote.getTimeInMs());
        if (timeCompare != 0) return timeCompare;
        else {
            return lengthInMs.compareTo(otherNote.getLengthInMs());
        }
    }

    @Override
    public String toString() {
        return note.toString() + ", start in " + timeInMs + ", ends in " + (timeInMs + (lengthInMs == null ? 100L : lengthInMs));
    }

    public Note getNote() {
        return note;
    }

    public Long getTimeInMs() {
        return timeInMs;
    }

    public Long getLengthInMs() {
        if (lengthInMs != null) {
            return lengthInMs;
        } else {
            LogHelper.e("Length of note: " + this.toString() + "is null!");
            return 100L;
        }
    }

    public void setLengthInMs(Long lengthInMs) {
        this.lengthInMs = lengthInMs;
    }
}
