package com.bas.bandclient.ui.searchDevices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bas.bandclient.BandClientApplication;
import com.bas.bandclient.R;
import com.bas.bandclient.models.DeviceModel;
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

public class SearchDevicesActivity extends Activity implements DeviceAdapter.Listener {

    @BindView(R.id.devices)
    protected RecyclerView devices;

    private DeviceAdapter adapter;

    private Disposable timer;
    private Disposable discover;
    private GumService gumService = KeeneticAPI.getDependencyGraph().getGumService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_devices);
        ButterKnife.bind(this);
        adapter = new DeviceAdapter(this);
        devices.setAdapter(adapter);
        devices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
