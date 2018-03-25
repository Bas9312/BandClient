package com.bas.bandclient.models;

import com.bas.bandclient.models.db.OneTypeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bas on 20.12.17.
 */

public class InstrumentType {
    private String name;

    public InstrumentType(String name) {
        this.name = name;
    }

    public static InstrumentType fromString(String type) {
        for (InstrumentType oneInstrument : getListOfTypes()) {
            if (oneInstrument.name.equals(type)) return oneInstrument;
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<InstrumentType> getListOfTypes() {
        List<OneTypeModel> models = OneTypeModel.getAllTypes();

        List<InstrumentType> result = new ArrayList<>();
        for (OneTypeModel model : models) {
            result.add(new InstrumentType(model.getName()));
        }
        return result;
    }
}
