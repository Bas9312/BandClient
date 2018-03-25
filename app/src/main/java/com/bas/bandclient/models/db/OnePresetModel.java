package com.bas.bandclient.models.db;

import com.bas.bandclient.BandClientApplication;
import com.bas.bandclient.models.InstrumentType;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by bas on 10.11.16.
 */

public class OnePresetModel extends RealmObject {
    private Integer id;
    private String presetName;
    private String type;

    private RealmList<OneNoteModel> notes;

    public static OnePresetModel createNewModel(String presetName) {
        Realm realm = Realm.getDefaultInstance();
        int key;
        try {
            key = realm.where(OnePresetModel.class).max("id").intValue() + 1;
        } catch(Exception ex) {
            key = 0;
        }
        realm.beginTransaction();
        OnePresetModel result = realm.createObject(OnePresetModel.class);
        result.presetName = presetName;
        result.id = key;
        realm.commitTransaction();
        return result;
    }

    public static List<OnePresetModel> getAll() {
        RealmResults<OnePresetModel> realmResults = Realm.getDefaultInstance().where(OnePresetModel.class).findAll();
        List<OnePresetModel> result = new ArrayList<>();
        for (OnePresetModel onePresetModel : realmResults) {
            result.add(onePresetModel);
        }
        return result;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.presetName = presetName;
        realm.commitTransaction();
        realm.close();
    }

    public RealmList<OneNoteModel> getNotes() {
        return notes;
    }

    public void setNotes(RealmList<OneNoteModel> notes) {
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

    public static OnePresetModel getById(Integer presetId) {
        return Realm.getDefaultInstance().where(OnePresetModel.class).equalTo("id", presetId).findFirst();
    }

    public InstrumentType getType() {
        if (type == null) return null;
        return InstrumentType.fromString(type);
    }

    public void setType(InstrumentType type) {
        if (type != null) this.type = type.toString();
    }

}
