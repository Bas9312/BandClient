package com.bas.bandclient.models;

import com.bas.bandclient.models.db.OneNoteModel;
import com.bas.bandclient.models.db.OnePresetModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bas on 3/13/18.
 */

public class OnePreset {
    private String presetName;
    private InstrumentType type;

    private List<Note> notes;

    public OnePreset(OnePresetModel onePresetModel) {
        this.presetName = onePresetModel.getPresetName();
        this.type = onePresetModel.getType();
        this.notes = new ArrayList<>();
        for (OneNoteModel oneNoteModel : PresetManager.getNotes(onePresetModel)) {
            this.notes.add(oneNoteModel.getNote());
        }
    }

    public OnePreset(String presetName, InstrumentType type, List<Note> notes) {
        this.presetName = presetName;
        this.type = type;
        this.notes = notes;
    }

    public InstrumentType getType() {
        if (type != null) {
            return type;
        } else return InstrumentType.fromString("blop");
    }

    public String getPresetName() {
        return presetName;
    }

    public List<Note> getNotes() {
        return notes;
    }
}
