package com.bas.bandclient.models;

import android.util.Log;

import com.bas.bandclient.BandClientApplication;
import com.bas.bandclient.helpers.ConvertHelper;
import com.bas.bandclient.models.db.OneNoteModel;
import com.bas.bandclient.models.db.OnePresetModel;
import com.bas.bandclient.ui.widgets.OneVisualNote;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by bas on 21.02.17.
 */

public class PresetManager {
    public static void saveNotes(final OnePresetModel preset, final ArrayList<OneVisualNote> notesList) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                preset.getNotes().deleteAllFromRealm();
                RealmList<OneNoteModel> newNotes = new RealmList<OneNoteModel>();
                for (OneVisualNote oneNote : notesList) {
                    OneNoteModel noteModel = OneNoteModel.createNewModel();
                    noteModel.setX(ConvertHelper.dpFromPx(BandClientApplication.getContext(), oneNote.x));
                    noteModel.setY(ConvertHelper.dpFromPx(BandClientApplication.getContext(), oneNote.y));
                    noteModel.setNote(oneNote.getNote());
                    noteModel.setSize(oneNote.size);
                    newNotes.add(noteModel);
                }
                preset.setNotes(newNotes);
            }
        });
    }

    public static void saveType(final OnePresetModel preset, final InstrumentType type) {
        Log.d("PresetManager", "InstrumentType: " + type);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                preset.setType(type);
            }
        });
    }

    public static List<OneNoteModel> getNotes(OnePresetModel preset) {
        return preset.getNotes().subList(0, preset.getNotes().size());
    }
    public static String getNotesString(OnePresetModel preset) {
        StringBuilder notesString = new StringBuilder();

        for (OneNoteModel oneNoteModel : PresetManager.getNotes(preset)) {
            notesString.append(preset.getType()).append(" ").append(oneNoteModel.getNote().toString()).append(", ");
        }

        if (notesString.length() > 0) {
            notesString.delete(notesString.length() - 2, notesString.length() - 1);
        }
        return notesString.toString();
    }
}
