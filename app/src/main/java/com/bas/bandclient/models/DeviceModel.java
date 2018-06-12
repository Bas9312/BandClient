package com.bas.bandclient.models;

import com.ndmsystems.api.session.P2PSession;

import java.net.InetSocketAddress;

/**
 * Created by bas on 4/19/18.
 */

public class DeviceModel {
    private String name;
    private String notes;
    private String cid;
    private P2PSession session;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCid() {
        return cid;
    }

    public void setSession(P2PSession session) {
        this.session = session;
    }

    public P2PSession getSession() {
        return session;
    }
}
