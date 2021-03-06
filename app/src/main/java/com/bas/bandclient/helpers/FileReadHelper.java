package com.bas.bandclient.helpers;

import android.util.Log;

import com.bas.bandclient.BandClientApplication;
import com.bas.bandclient.R;
import com.bas.bandclient.models.Composition;
import com.bas.bandclient.models.InstrumentType;
import com.bas.bandclient.models.NoteToPlay;
import com.bas.bandclient.models.Track;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.ChannelEvent;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.MetaEvent;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TrackName;
import com.leff.midi.util.MidiUtil;
import com.ndmsystems.infrastructure.logging.LogHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by bas on 20.03.17.
 */

public class FileReadHelper {

    public static Composition readFile() {
        MidiFile midi = null;
        try {
            midi = new MidiFile(BandClientApplication.getContext().getResources().openRawResource(R.raw.kuznechic));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (midi != null) {
            List<Track> parsedTracks = new ArrayList<>();
            ArrayList<MidiTrack> tracks = midi.getTracks();
            System.out.println("Number of tracks: " + tracks.size());
            float bpm = 999;
            int mpqn = 99999;
            for (MidiTrack track : tracks) {
                ArrayList<ChannelEvent> trackNotes = new ArrayList<>();
                TreeSet<MidiEvent> events = track.getEvents();
                String trackName = null;
                System.out.println("Number of events in track: " + events.size());
                for (MidiEvent event : events) {
                    if (event instanceof Tempo) {
                        Log.d("Meta", "Bpm: " + ((Tempo) event).getBpm() + " mpwn: " + ((Tempo) event).getMpqn());
                        bpm = ((Tempo) event).getBpm();
                        mpqn = ((Tempo) event).getMpqn();
                    }
                    if (event instanceof TrackName) {
                        trackName = ((TrackName) event).getTrackName();
                    }
                    if (event instanceof NoteOn) {
                        NoteOn noteOnEvent = (NoteOn) event;
                        if (trackNotes.size() != 0) {
                            ChannelEvent previousNote = trackNotes.get(trackNotes.size() -1);
                            if (previousNote instanceof NoteOn &&
                                    (((NoteOn) previousNote).getVelocity() == 0
                                    && noteOnEvent.getVelocity() == 0)) {
                                continue;//Don't need add two note with 0 velocity
                            }
                        }
                        noteOnEvent.setTick(MidiUtil.ticksToMs(noteOnEvent.getTick(), mpqn, midi.getResolution()));
                        trackNotes.add(noteOnEvent);
                    }

                    if (event instanceof NoteOff) {
                        event.setTick(MidiUtil.ticksToMs(event.getTick(), mpqn, midi.getResolution()));
                        trackNotes.add((ChannelEvent) event);
                    }
                }
                List<NoteToPlay> clearedNotes = getNotesForPlay(trackNotes);
                if (clearedNotes.size() > 0) {
                    parsedTracks.add(new Track(clearedNotes, trackName, InstrumentType.fromString("blop")));//TODO: убрать!
                }
            }
            return new Composition(parsedTracks);
        } else System.out.println("midi is null");
        return null;
    }

    private static List<NoteToPlay> getNotesForPlay(ArrayList<ChannelEvent> allEvents) {
        List<NoteToPlay> noteToPlays = new ArrayList<>();
        for (ChannelEvent event : allEvents) {
            if (event instanceof NoteOn) {
                NoteOn noteOn = (NoteOn) event;
                if (noteOn.getVelocity() != 0) {
                    if (noteToPlays.size() > 0) {
                        NoteToPlay previous = noteToPlays.get(noteToPlays.size() - 1);
                        if (previous.getTimeInMs() == noteOn.getTick()
                                && previous.getNote().equals(MidiNoteHelper.getNoteFromMidiPitch(noteOn.getNoteValue()))) {
                            continue;
                        }
                    }
                    noteToPlays.add(new NoteToPlay(MidiNoteHelper.getNoteFromMidiPitch(noteOn.getNoteValue()), noteOn.getTick()));
                }
            } else {
                if (event instanceof NoteOff) {
                    if (noteToPlays.size() > 0) {
                        NoteToPlay previous = noteToPlays.get(noteToPlays.size() - 1);
                        previous.setLengthInMs(event.getTick() - previous.getTimeInMs());
                        noteToPlays.set(noteToPlays.size() - 1, previous);
                    } else LogHelper.e("NoteOff then 0 notes at list");
                }
            }
        }
        Collections.sort(noteToPlays);
        return noteToPlays;
    }

}
