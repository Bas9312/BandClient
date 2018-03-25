package com.bas.bandclient;

import com.bas.bandclient.helpers.CompositionBinder;
import com.bas.bandclient.helpers.MidiNoteHelper;
import com.bas.bandclient.models.Composition;
import com.bas.bandclient.models.DataToPlay;
import com.bas.bandclient.models.InstrumentType;
import com.bas.bandclient.models.Note;
import com.bas.bandclient.models.NoteToPlay;
import com.bas.bandclient.models.OnePreset;
import com.bas.bandclient.models.Track;
import com.bas.bandclient.models.db.OnePresetModel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MergeTest {
    @Test
    public void merge_is_correct() throws Exception {
        InstrumentType instrumentType = new InstrumentType("blop");
        List<Track> trackList = new ArrayList<>();
        Track track1 = new Track(Arrays.asList(new NoteToPlay(Note.C, 0L, 100L), new NoteToPlay(Note.D, 100L, 100L),
                new NoteToPlay(Note.E, 2000L, 500L)), "1");
        track1.setType(instrumentType);
        Track track2 = new Track(Arrays.asList(new NoteToPlay(Note.C_SHARP, 0L, 150L), new NoteToPlay(Note.D_SHARP, 500L, 100L),
                new NoteToPlay(Note.F_SHARP, 2000L, 100L), new NoteToPlay(Note.F, 3000L, 100L)), "2");
        track2.setType(instrumentType);

        trackList.add(track1);
        trackList.add(track2);
        Composition composition = new Composition(trackList);

        List<OnePreset> presets = new ArrayList<>();
        OnePreset preset1 = new OnePreset("1p", instrumentType, Arrays.asList(Note.C, Note.F, Note.D_SHARP));
        OnePreset preset2 = new OnePreset("2p", instrumentType, Arrays.asList(Note.D, Note.E, Note.D_SHARP));
        OnePreset preset3 = new OnePreset("3p", instrumentType, Arrays.asList(Note.C_SHARP, Note.D_SHARP, Note.F_SHARP, Note.G));
        OnePreset preset4 = new OnePreset("4p", instrumentType, Arrays.asList(Note.D_SHARP));
        presets.add(preset1);
        presets.add(preset2);
        presets.add(preset3);
        presets.add(preset4);

        DataToPlay dataToPlay = CompositionBinder.bind(composition, presets);
        System.out.println("Success");
    }
}