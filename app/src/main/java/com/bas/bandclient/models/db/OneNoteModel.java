package com.bas.bandclient.models.db;

import com.bas.bandclient.models.Note;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by bas on 10.11.16.
 */

public class OneNoteModel extends RealmObject {
    private String note;
    private Float x;
    private Float y;
    private Float size;

    public void setX(Float x) {
        this.x = x;
    }

    public Float getX() {
        return x;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getY() {
        return y;
    }

    public void setNote(Note note) {
        this.note = note.toString();
    }

    public Note getNote() {
        return Note.parseFromString(note);
    }

    public static OneNoteModel createNewModel() {
        Realm realm = Realm.getDefaultInstance();
        return realm.createObject(OneNoteModel.class);
    }

    public void setSize(float size) {
        this.size = size;
    }

    public Float getSize() {
        return size;
    }

}
