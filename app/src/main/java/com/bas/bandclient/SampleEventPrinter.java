package com.bas.bandclient;

import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.util.MidiEventListener;
import com.leff.midi.util.MidiProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bas on 20.03.17.
 */

public class SampleEventPrinter implements MidiEventListener
{
    private String mLabel;
    public ArrayList<MidiEvent> events = new ArrayList<>();

    public SampleEventPrinter(String label)
    {
        mLabel = label;
    }

    // 0. Implement the listener functions that will be called by the
    // MidiProcessor
    @Override
    public void onStart(boolean fromBeginning)
    {
        if(fromBeginning)
        {
            System.out.println(mLabel + " Started!");
        }
        else
        {
            System.out.println(mLabel + " resumed");
        }
    }

    @Override
    public void onEvent(MidiEvent event, long ms)
    {
        if (event.getDelta() != 0){
            String result = ms + ", " + mLabel + " received event: " + event;
            if (event instanceof NoteOn) result += " " + ((NoteOn) event).getNoteValue() + " " + ((NoteOn) event).getVelocity();
            System.out.println(result);

            event.setDelta(ms);
            events.add(event);
        }
    }

    @Override
    public void onStop(boolean finished)
    {
        if(finished)
        {
            System.out.println(mLabel + " Finished!");
        }
        else
        {
            System.out.println(mLabel + " paused");
        }
    }

}