package com.bas.bandclient;

import com.bas.bandclient.helpers.CompositionBinder;
import com.bas.bandclient.helpers.FileReadHelper;
import com.bas.bandclient.helpers.MidiNoteHelper;
import com.bas.bandclient.models.Composition;
import com.bas.bandclient.models.DataToPlay;
import com.bas.bandclient.models.InstrumentType;
import com.bas.bandclient.models.Note;
import com.bas.bandclient.models.OnePreset;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class NoteTest {
    @Test
    public void parse_is_correct() throws Exception {
        Composition composition = FileReadHelper.readFile();
        List<OnePreset> presetList = new ArrayList<>();
        List<Note> noteList = Arrays.asList(new Note(Note.D_SHARP, Note.Octave.GREAT), new Note(Note.A, Note.Octave.GREAT), new Note(Note.D, Note.Octave.GREAT));
        OnePreset onePreset = new OnePreset("test", InstrumentType.fromString("blop"), noteList);
        presetList.add(onePreset);

        DataToPlay dataToPlay = CompositionBinder.bind(composition, presetList);
        assertNotNull(dataToPlay);
    }
}