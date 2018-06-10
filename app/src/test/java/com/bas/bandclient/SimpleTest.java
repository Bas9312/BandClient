package com.bas.bandclient;

import com.bas.bandclient.helpers.MidiNoteHelper;
import com.bas.bandclient.models.Note;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SimpleTest {
    @Test
    public void parse_is_correct() throws Exception {
        Note note = Note.parseFromString("C#0");
        assertEquals(note, Note.C_SHARP);
        assertEquals(note.getOctave(), Note.Octave.SUBCONTRA);
    }

    @Test
    public void parse_from_pitch_is_correct() throws Exception {
        Note note = MidiNoteHelper.getNoteFromMidiPitch(93);
        assertEquals(note, Note.A);
        assertEquals(note.getOctave(), Note.Octave.THREE_LINED);
    }
}