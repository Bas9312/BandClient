package com.bas.bandclient.helpers;

import com.bas.bandclient.models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bas on 27.02.17.
 */

public class MidiNoteHelper {

    public static Note getNoteFromMidiPitch(int pitch) {
        Note note = Note.values().get(pitch % 12);
        note.setOctave(Note.Octave.fromInt(pitch / 12 - 1));
        return note;
    }
}
