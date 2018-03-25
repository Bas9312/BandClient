package com.bas.bandclient;

import android.app.Application;

import com.bas.bandclient.models.db.OneTypeModel;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by bas on 10.11.16.
 */

public class BandClientApplication extends Application {

    private static BandClientApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder().build());
        if (OneTypeModel.getAllTypes().size() == 0) {
            OneTypeModel.addNewModel("blop");
            OneTypeModel.addNewModel("blow");
            OneTypeModel.addNewModel("chpok");
        }
    }
    public BandClientApplication() {
        super();
        instance = this;
    }

    public static BandClientApplication getContext() {
        return instance;
    }
}
