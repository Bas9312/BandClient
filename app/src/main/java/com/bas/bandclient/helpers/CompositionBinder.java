package com.bas.bandclient.helpers;

import com.bas.bandclient.models.Composition;
import com.bas.bandclient.models.DataToPlay;
import com.bas.bandclient.models.DataToPlayForOnePreset;
import com.bas.bandclient.models.InstrumentType;
import com.bas.bandclient.models.NoteToPlay;
import com.bas.bandclient.models.OnePreset;
import com.bas.bandclient.models.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by bas on 3/13/18.
 */

public class CompositionBinder {

    public static Long TIME_BETWEEN_NOTES = 1000L;

    public static DataToPlay bind(Composition composition, List<OnePreset> presetModelList) {
        HashMap<InstrumentType, List<NoteToPlay>> toPlayHashMap = getNotesByInstrumentType(composition);

        HashMap<InstrumentType, List<OnePreset>> presetsByInstrumentType = getPresetsByInstrumentType(presetModelList);

        if (toPlayHashMap.size() > presetsByInstrumentType.size()) {
            System.out.println("Number of types for play more then player types");
            return null;
        }

        return bindData(toPlayHashMap, presetsByInstrumentType);
    }

    private static DataToPlay bindData(HashMap<InstrumentType, List<NoteToPlay>> toPlayHashMap, HashMap<InstrumentType, List<OnePreset>> presetsByInstrumentType) {
        DataToPlay dataToPlay = new DataToPlay();


        for (Map.Entry<InstrumentType, List<NoteToPlay>> entry : toPlayHashMap.entrySet()) {
            InstrumentType instrumentType = entry.getKey();
            Stack<NoteToPlay> notesToBind = new Stack<>();
            List<NoteToPlay> values = entry.getValue();
            Collections.reverse(values);
            notesToBind.addAll(values);
            List<OnePreset> presetsToBind = presetsByInstrumentType.get(instrumentType);
            if (presetsToBind == null || presetsToBind.size() == 0) {
                System.out.println("No one can't play " + instrumentType.toString());
                return null;
            }
            dataToPlay = addNoteToPlay(dataToPlay, presetsToBind, notesToBind);
            if (dataToPlay == null) return null;
        }

        return dataToPlay;
    }

    private static DataToPlay addNoteToPlay(DataToPlay dataToPlay, List<OnePreset> presetsToBind, Stack<NoteToPlay> notesToBind) {
        if (notesToBind.empty()) {
            return dataToPlay;
        }
        NoteToPlay noteToPlay = notesToBind.pop();
        List<OnePreset> presetsThatCanPlayNote = getPresetsThatCanPlayNote(noteToPlay, presetsToBind);
        for (OnePreset preset : presetsThatCanPlayNote) {
            NoteToPlay lastNote = dataToPlay.getLastNoteForPreset(preset);

            if (lastNote == null
                    || (lastNote.getTimeInMs() + lastNote.getLengthInMs() + TIME_BETWEEN_NOTES) <= noteToPlay.getTimeInMs()) {
                dataToPlay.addNoteToPreset(preset, noteToPlay);
                DataToPlay dataForReturn = addNoteToPlay(dataToPlay, presetsToBind, notesToBind);
                if (dataForReturn != null) {
                    return dataForReturn;
                } else {
                    dataToPlay.removeLastNoteFromPreset(preset);
                }
            }
        }
        notesToBind.push(noteToPlay);
        return null;
    }

    private static List<OnePreset> getPresetsThatCanPlayNote(NoteToPlay oneNote, List<OnePreset> presetsToBind) {
        List<OnePreset> forReturn = new ArrayList<>();
        for (OnePreset presetModel : presetsToBind) {
            if (presetModel.getNotes().contains(oneNote.getNote())) {
                forReturn.add(presetModel);
            }
        }
        return forReturn;
    }

    private static HashMap<InstrumentType, List<NoteToPlay>> getNotesByInstrumentType(Composition composition) {
        HashMap<InstrumentType, List<NoteToPlay>> toPlayHashMap = new HashMap<>();
        for (Track track : composition.getTrackList()) {
            if (!toPlayHashMap.containsKey(track.getType())) {
                toPlayHashMap.put(track.getType(), new ArrayList<NoteToPlay>());
            }

            List<NoteToPlay> noteToPlays = toPlayHashMap.get(track.getType());

            toPlayHashMap.put(track.getType(), mergeNotes(noteToPlays, track.getNoteToPlays()));
        }
        return toPlayHashMap;
    }

    private static HashMap<InstrumentType, List<OnePreset>> getPresetsByInstrumentType(List<OnePreset> presetModelList) {
        HashMap<InstrumentType, List<OnePreset>> presetsByInstrumentType = new HashMap<>();
        for (OnePreset onePresetModel : presetModelList) {
            if (!presetsByInstrumentType.containsKey(onePresetModel.getType())) {
                presetsByInstrumentType.put(onePresetModel.getType(), new ArrayList<OnePreset>());
            }
            List<OnePreset> tempPresetList = presetsByInstrumentType.get(onePresetModel.getType());
            tempPresetList.add(onePresetModel);
            presetsByInstrumentType.put(onePresetModel.getType(), tempPresetList);
        }
        return presetsByInstrumentType;
    }

    private static List<NoteToPlay> mergeNotes(List<NoteToPlay> firstNotes, List<NoteToPlay> secondNotes) {
        List<NoteToPlay> resultList = new ArrayList<>();
        int positionFirst = 0, positionSecond = 0;
        while (positionSecond < secondNotes.size()) {
            while (positionFirst < firstNotes.size()) {
                if (positionSecond == firstNotes.size() || firstNotes.get(positionFirst).compareByTime(secondNotes.get(positionSecond)) <= 0) {
                    resultList.add(firstNotes.get(positionFirst));
                    positionFirst++;
                } else {
                    resultList.add(secondNotes.get(positionSecond));
                    positionSecond++;
                }
            }
            if (positionSecond < secondNotes.size()) {
                resultList.add(secondNotes.get(positionSecond));
                positionSecond++;
            }
        }

        return resultList;
    }
}
