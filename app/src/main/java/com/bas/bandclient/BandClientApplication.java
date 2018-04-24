package com.bas.bandclient;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bas.bandclient.models.db.OneTypeModel;
import com.bas.bandclient.storage.IStorage;
import com.bas.bandclient.storage.SharedPreferencesStorage;
import com.google.gson.Gson;
import com.ndmsystems.api.KeeneticAPI;
import com.ndmsystems.api.gum.GumService;
import com.ndmsystems.api.helpers.IEventLogger;
import com.ndmsystems.coala.CoAPResource;
import com.ndmsystems.coala.CoAPResourceInput;
import com.ndmsystems.coala.CoAPResourceOutput;
import com.ndmsystems.coala.ICoalaStorage;
import com.ndmsystems.coala.message.CoAPMessage;
import com.ndmsystems.coala.message.CoAPMessageCode;
import com.ndmsystems.coala.message.CoAPMessagePayload;
import com.ndmsystems.coala.message.CoAPRequestMethod;
import com.ndmsystems.infrastructure.logging.LogHelper;
import com.ndmsystems.infrastructure.logging.collector.LogsCollector;

import java.util.HashMap;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by bas on 10.11.16.
 */

public class BandClientApplication extends Application {

    private static BandClientApplication instance;
    private static IStorage storage;
    private static ICoalaStorage coalaStorage;

    public IStorage getStorage() {
        return storage;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupLogging();

        storage = new SharedPreferencesStorage(new Gson(), getSharedPreferences("coreApplicationSharedPreferences", Context.MODE_PRIVATE));
        coalaStorage = new ICoalaStorage() {

            @Override
            public void put(String key, Object obj) {
                storage.put(getCoalaKey(key), obj);
            }

            @Override
            public <T> T get(String key, Class<T> clz) {
                return storage.get(getCoalaKey(key), clz);
            }

            @Override
            public void remove(String key) {
                storage.remove(getCoalaKey(key));
            }

            private String getCoalaKey(String key) {
                return "coala_" + key;
            }
        };

        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder().build());
        if (OneTypeModel.getAllTypes().size() == 0) {
            OneTypeModel.addNewModel("blop");
            OneTypeModel.addNewModel("blow");
            OneTypeModel.addNewModel("chpok");
        }

        setupCidAndDeviceName();
        KeeneticAPI.initialize(this, getCid(), getName(), coalaStorage, new IEventLogger() {
            @Override
            public void logEvent(String name, HashMap<String, String> params) {

            }

            @Override
            public void logEvent(String name, String key, String value) {

            }
        });
        KeeneticAPI.getDependencyGraph().getGumService().getCoala().addResource("notes", CoAPRequestMethod.GET, new CoAPResource.CoAPResourceHandler() {
            @Override
            public CoAPResourceOutput onReceive(CoAPResourceInput inputData) {
                String notes = storage.get(Consts.PREFS_CURRENT_NOTES, String.class);
                return new CoAPResourceOutput(new CoAPMessagePayload(notes), CoAPMessageCode.CoapCodeContent, CoAPMessage.MediaType.Json);
            }
        });

        KeeneticAPI.getDependencyGraph().getGumService().start();
    }

    private void setupLogging() {
        LogHelper.setLogLevel(BuildConfig.DEBUG ? LogHelper.LogLevel.VERBOSE : LogHelper.LogLevel.WARNING);
        LogHelper.addLogger(new AndroidStandardILogger());
        //elasticSearchLogger = new ElasticSearchLogger(new GsonBuilder().create());
        //LogHelper.addLogger(elasticSearchLogger);
        if (BuildConfig.DEBUG) {
            LogHelper.addLogger(LogsCollector.getInstance());
        }
    }

    public BandClientApplication() {
        super();
        instance = this;
    }


    private void setupCidAndDeviceName() {
        if (getCid() == null)
            setCid(UUID.randomUUID().toString());
        if (getName() == null)
            setName(generateName());
    }

    public String getCid() {
        return storage.get(Consts.PREFS_CID, String.class);
    }

    private void setCid( String cid) {
        storage.put(Consts.PREFS_CID, cid);
    }

    private String getName() {
        return storage.get(Consts.PREFS_NAME, String.class);
    }

    private void setName(String name) {
        storage.put(Consts.PREFS_NAME, name);
    }

    public String generateName() {
        String name;
        name = Build.MODEL;

        if (!name.contains(Build.MANUFACTURER))
            name = Build.MANUFACTURER + " " + name;

        name = name.length() > 32 ? name.substring(0, 32) : name;
        return name;
    }

    public static BandClientApplication getContext() {
        return instance;
    }
}
