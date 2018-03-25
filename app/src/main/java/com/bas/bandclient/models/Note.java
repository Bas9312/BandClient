package com.bas.bandclient.models;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Note implements Serializable, Comparable {
    //C("C"), C_SHARP("C#"), D("D"), D_SHARP("D#"), E("E"), F("F"), F_SHARP("F#"), G("G"), G_SHARP("G#"), A("A"), A_SHARP("A#"), H("H");
    public static final Note C = new Note("C");
    public static final Note C_SHARP = new Note("C#");
    public static final Note D = new Note("D");
    public static final Note D_SHARP = new Note("D#");
    public static final Note E = new Note("E");
    public static final Note F = new Note("F");
    public static final Note F_SHARP = new Note("F#");
    public static final Note G = new Note("G");
    public static final Note G_SHARP = new Note("G#");
    public static final Note A = new Note("A");
    public static final Note A_SHARP = new Note("A#");
    public static final Note H = new Note("H");
    private final String stringRepresentation;

    private static final Pattern forParse = Pattern.compile("(\\D+)(\\d)");

    Octave octave;

    private Note(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public static Note parseFromString(String s) {
        Matcher m = forParse.matcher(s);
        if (m.matches()) {
            Note note = getFromString(m.group(1));
            note.octave = Octave.fromInt(Integer.valueOf(m.group(2)));
            return note;
        } else {
            Log.e("Note", "Can't parse string: " + s);
            return null;
        }
    }

    private static Note getFromString(String s) {
        return new Note(s);
    }

    @Override
    public String toString() {
        if (octave != null) return stringRepresentation + octave.toString();
        return stringRepresentation;
    }

    public Octave getOctave() {
        return octave;
    }

    public void setOctave(Octave octave) {
        this.octave = octave;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (this.getClass() != obj.getClass())
            return false;
        Note otherNote = (Note) obj;
        if (octave != null && otherNote.getOctave() != null) {
            return octave.equals(otherNote.getOctave()) && this.stringRepresentation.equals(otherNote.stringRepresentation);
        } else {
            return this.stringRepresentation.equals(otherNote.stringRepresentation);
        }
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (this == o) return 0;
        Note otherNote = (Note) o;
        int octaveCompare = octave.compareTo(otherNote.getOctave());
        if (octaveCompare != 0) return octaveCompare;
        return this.stringRepresentation.compareTo(otherNote.stringRepresentation);
    }

    public static List<Note> values() {
        return Arrays.asList(C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A_SHARP, H);
    }

    public enum Octave {
        SUBCONTRA(0), CONTRA(1), GREAT(2), SMALL(3), ONE_LINED(4), TWO_LINED(5), THREE_LINED(6), FOUR_LINED(7), FIVE_LINED(8);

        private final int numberInScientificNotaion;

        private Octave(int numberInScientificNotaion) {
            this.numberInScientificNotaion = numberInScientificNotaion;
        }

        public static Octave fromInt(int i) {
            return values()[i];
        }

        @Override
        public String toString() {
            return String.valueOf(numberInScientificNotaion);
        }
    }
}
