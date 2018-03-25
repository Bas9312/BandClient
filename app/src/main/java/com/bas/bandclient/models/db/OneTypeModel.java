package com.bas.bandclient.models.db;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by bas on 10.11.16.
 */

public class OneTypeModel extends RealmObject {
    private String name;

    public static void addNewModel(String name) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        OneTypeModel oneTypeModel = realm.createObject(OneTypeModel.class);
        oneTypeModel.name = name;
        realm.commitTransaction();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<OneTypeModel> getAllTypes() {
        return Realm.getDefaultInstance().where(OneTypeModel.class).findAll();
    }
}
