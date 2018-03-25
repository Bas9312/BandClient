package com.bas.bandclient.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bas on 27.11.17.
 */

public class Composition implements Serializable{
    private final List<Track> trackList;

    public Composition(List<Track> trackList) {
        this.trackList = trackList;
    }

    public List<Track> getTrackList() {
        return trackList;
    }
}
