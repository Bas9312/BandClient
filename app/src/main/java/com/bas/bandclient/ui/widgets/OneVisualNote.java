package com.bas.bandclient.ui.widgets;

import com.bas.bandclient.models.Note;

/**
 * Created by bas on 27.02.17.
 */

public class OneVisualNote {
    private Note note = null;
    public Float size = null;
    public Float x;
    public Float y;

    public OneVisualNote(Note note, float size, float x, float y) {
        this.note = note;
        this.size = size;
        this.x = x;
        this.y = y;
    }

    public OneVisualNote(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void plusSize() {
        x = (float) (x * 1.2);
        y = (float) (y * 1.2);
    }

    public void minusSize() {
        x = (float) (x * 0.8);
        y = (float) (y * 0.8);
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "name: " + (note != null ? note.toString() : "null") + ", size: " + size + ", x: " + x + ", y: " + y;
    }
}
