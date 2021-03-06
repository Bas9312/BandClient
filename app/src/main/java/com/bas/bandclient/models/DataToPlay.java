package com.bas.bandclient.models;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bas on 3/13/18.
 */

public class DataToPlay {
    private HashMap<OnePreset, DataToPlayForOnePreset> innerData = new HashMap<>();

    public void addNoteToPreset(OnePreset onePreset, NoteToPlay note) {
        if (!innerData.containsKey(onePreset)) {
            innerData.put(onePreset, new DataToPlayForOnePreset(onePreset));
        }

        innerData.get(onePreset).addNoteToPlay(note);
    }

    public NoteToPlay getLastNoteForPreset(OnePreset onePreset) {
        if (!innerData.containsKey(onePreset)) {
            innerData.put(onePreset, new DataToPlayForOnePreset(onePreset));
        }

        return innerData.get(onePreset).getLastNote();
    }

    public void removeLastNoteFromPreset(OnePreset onePreset) {
        if (!innerData.containsKey(onePreset)) {
            innerData.put(onePreset, new DataToPlayForOnePreset(onePreset));
        }

        innerData.get(onePreset).removeLastNote();
    }

    public DataToPlayForOnePreset getDataToPlayByPreset(OnePreset onePreset) {
        return innerData.get(onePreset);
    }

    public Collection<DataToPlayForOnePreset> getDataToPlayByPresets() {
        return innerData.values();
    }

    @Override
    public String toString() {
        return "Number of presets: " + innerData.size();
    }
}
