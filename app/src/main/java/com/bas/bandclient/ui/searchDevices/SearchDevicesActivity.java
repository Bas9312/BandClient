package com.bas.bandclient.ui.searchDevices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bas.bandclient.BandClientApplication;
import com.bas.bandclient.R;
import com.bas.bandclient.helpers.CompositionBinder;
import com.bas.bandclient.models.Composition;
import com.bas.bandclient.models.DataToPlay;
import com.bas.bandclient.models.DeviceModel;
import com.bas.bandclient.models.InstrumentType;
import com.bas.bandclient.models.Note;
import com.bas.bandclient.models.NoteToPlay;
import com.bas.bandclient.models.OnePreset;
import com.bas.bandclient.models.Track;
import com.google.gson.Gson;
import com.ndmsystems.api.KeeneticAPI;
import com.ndmsystems.api.gum.GumService;
import com.ndmsystems.api.helpers.InfoHelper;
import com.ndmsystems.api.localDeviceDiscovery.LocalDevicesDiscoverer;
import com.ndmsystems.api.session.P2PSession;
import com.ndmsystems.coala.message.CoAPMessageCode;
import com.ndmsystems.coala.resource_discovery.ResourceDiscoveryResult;
import com.ndmsystems.infrastructure.logging.LogHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bas.bandclient.ui.TracksListActivity.COMPOSITION_EXTRA;

public class SearchDevicesActivity extends Activity implements DeviceAdapter.Listener {
    @BindView(R.id.tvNeededNotes)
    protected TextView tvNeededNotes;
    @BindView(R.id.btnDecompose)
    protected Button btnDecompose;
    @BindView(R.id.devices)
    protected RecyclerView devices;

    private DeviceAdapter adapter;

    private Disposable timer;
    private Disposable discover;
    private GumService gumService = KeeneticAPI.getDependencyGraph().getGumService();
    private List<DeviceModel> devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_devices);
        ButterKnife.bind(this);
        Composition composition = (Composition) getIntent().getSerializableExtra(COMPOSITION_EXTRA);
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> notes = new HashMap<>();

        for (Track track : composition.getTrackList()) {
            for (NoteToPlay noteToPlay: track.getNoteToPlays()) {
                notes.put(track.getType() + " " + noteToPlay.getNote().toString(), track.getType() + " " + noteToPlay.getNote().toString() + ", ");
            }
        }

        for (String values : notes.values()) {
            stringBuilder.append(values);
        }

        tvNeededNotes.setText(getString(R.string.needed_notes, stringBuilder.toString()));


        adapter = new DeviceAdapter(this);
        devices.setAdapter(adapter);
        devices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnDecompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<OnePreset> presets = new ArrayList<>();


                for (DeviceModel deviceModel : devicesList) {
                    List<Note> notesList = new ArrayList<>();

                    String type = null;
                    for (String noteString : deviceModel.getNotes().split(",")) {
                        String[] parts = noteString.trim().split(" ");
                        type = parts[0];

                        notesList.add(Note.parseFromString(parts[1]));
                    }

                    presets.add(new OnePreset(deviceModel.getName(), InstrumentType.fromString(type), notesList));
                }
                DataToPlay dataToPlay = CompositionBinder.bind(composition, presets);

                if (dataToPlay == null) {
                    Toast.makeText(SearchDevicesActivity.this, "Can't decompose!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SearchDevicesActivity.this, "Success!!!", Toast.LENGTH_LONG).show();
                }
                
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startObservingLocalDevices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (discover != null && !discover.isDisposed())
            discover.dispose();
        stopObservingLocalDevices();
    }

    public void showDevices(List<DeviceModel> devices) {
        this.devices.setVisibility(View.VISIBLE);
        this.devicesList = devices;
        adapter.setDevices(devices);
    }


    private void startObservingLocalDevices() {
        if (timer == null)
            timer = Observable.interval(0, 1500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> observeLocalDevices());
    }

    private void stopObservingLocalDevices() {
        if (timer != null) {
            timer.dispose();
            timer = null;
        }
    }

    private void observeLocalDevices() {

            discover = discoverHosts()
                    .subscribe(
                            devices -> {
                                LogHelper.d("Hosts discovered: " + devices.size());
                                showDevices(devices);
                            },
                            throwable -> {
                                LogHelper.e("Error: " + throwable.getMessage());
                            }
                    );
    }


    public Observable<List<DeviceModel>> discoverHosts() {
        return gumService.runResourceDiscovery()
                .map(this::clearNoBandClients)
                .map(this::discoveryResultToListOfSessions)
                .flatMap(this::getDevicesInfo)
                .map(this::removeSelf)
                .map(this::sortDevices)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    private List<ResourceDiscoveryResult> clearNoBandClients(List<ResourceDiscoveryResult> resourceDiscoveryResults) {
        Iterator<ResourceDiscoveryResult> it = resourceDiscoveryResults.iterator();
        while (it.hasNext()) {
            ResourceDiscoveryResult resourceDiscoveryResult = it.next();
            Boolean isContains = false;

            for (String resource : resourceDiscoveryResult.getResources()) {
                if (resource.contains("notes")) isContains = true;
            }

            if (!isContains) it.remove();
        }

        return resourceDiscoveryResults;
    }

    private List<P2PSession> discoveryResultToListOfSessions(List<ResourceDiscoveryResult> discoveryResults) {
        LogHelper.d("Discovered " + discoveryResults.size() + " results, try to get info");
        return Observable.fromIterable(discoveryResults)
                .flatMap(discoveryResult -> new P2PSession(discoveryResult.getHost(), null, gumService, new LocalDevicesDiscoverer()).start())
                .toList()
                .blockingGet();
    }

    private List<DeviceModel> removeSelf(List<DeviceModel> deviceModels) {
        Iterator<DeviceModel> it = deviceModels.iterator();

        while (it.hasNext()) {
            DeviceModel deviceModel = it.next();
            if (BandClientApplication.getContext().getCid().equals(deviceModel.getCid())) {
                it.remove();
            }
        }
        return deviceModels;
    }

    private List<DeviceModel> sortDevices(List<DeviceModel> unsortedDevices) {
        List<DeviceModel> sortedDevices = new ArrayList<>(unsortedDevices);
        Collections.sort(sortedDevices, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        return sortedDevices;
    }

    private Observable<List<DeviceModel>> getDevicesInfo(List<P2PSession> sessions) {
        List<Observable<DeviceModel>> observables = new ArrayList<>();
        for (P2PSession session : sessions)
            observables.add(getDeviceInfo(session));

        return Observable
                .merge(observables)
                .toList()
                .toObservable();
    }

    private Observable<DeviceModel> getDeviceInfo(P2PSession session) {
        LogHelper.d("getDeviceInfo: " + session.getCid());
        DeviceModel deviceModel = new DeviceModel();
        return InfoHelper.getPeerInfo(gumService.getCoala(), new Gson(), session.getAddress())
                .flatMap(peerInfo -> {
                    deviceModel.setName(peerInfo.name);
                    deviceModel.setCid(peerInfo.cid);
                    return session.sendRequest(CoAPMessageCode.GET, "notes", null)
                            .map(s -> {
                                deviceModel.setNotes(s);
                                return deviceModel;
                            });
                });
    }

    @Override
    public void onDeviceClick(DeviceModel device) {

    }
}
