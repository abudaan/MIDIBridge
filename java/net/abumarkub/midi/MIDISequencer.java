/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 *  
 *  Wrapper around the javax.sound.midi.Sequencer class 
 *  
 */
package net.abumarkub.midi;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import javax.sound.midi.*;
import netscape.javascript.JSObject;
import org.apache.commons.codec.binary.Base64;

public class MIDISequencer implements Receiver, MetaEventListener {

    private Sequence _sequence;
    private Sequencer _sequencer;
    private Transmitter _transmitter;
    private Transmitter _transmitter2;
    private Receiver _receiver;
    private JSObject _midiEventListener;
    private JSObject _metaEventListener;
    private boolean _hasMetaEventListener;

    public MIDISequencer() {
                
        try {
            _sequencer = javax.sound.midi.MidiSystem.getSequencer(false);

        } catch (MidiUnavailableException e) {
            System.out.println(e);
        }
        _receiver = null;
        _transmitter = null;
        _transmitter2 = null;
        _metaEventListener = null;
    }

    public boolean addEventListener(String id, JSObject eventListener) {

        //System.out.println("addEventListener");
        if (id.equals("midimessage")) {
            _midiEventListener = eventListener;
            return true;
        } else if (id.equals("metamessage")) {
            if (!_hasMetaEventListener) {
                _hasMetaEventListener = _sequencer.addMetaEventListener(this);
            }
            _metaEventListener = eventListener;
            return true;
        }
        return false;
    }

    public Sequence loadBase64String(String data) {
        System.out.println("load:" + data);
        _sequence = null;
        byte[] decoded = Base64.decodeBase64(data);
        ByteArrayInputStream input = new ByteArrayInputStream(decoded);
        try {
            _sequence = javax.sound.midi.MidiSystem.getSequence(input);
            loadSequence(_sequence);

        } catch (Exception e) {
            System.out.println("loading: " + e);
        }
        return _sequence;
    }

    public Sequence playBase64String(String data) {
        loadBase64String(data);
        play();
        return _sequence;
    }

    public Sequence loadMidiFile(String url) {
        FileInputStream is = null;
        _sequence = null;

        try {
            //System.out.println("loadMidiFile: " + url);
            is = new FileInputStream(url);
            //} catch (FileNotFoundException e) {
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            _sequence = javax.sound.midi.MidiSystem.getSequence(is);
            loadSequence(_sequence);

        } catch (Exception e) {
            System.out.println(e);
        }

        return _sequence;
    }

    public Sequence playMidiFile(String url) {
        loadMidiFile(url);
        play();
        return _sequence;
    }

    public void close() {
        if (_sequencer.isOpen()) {
            _sequencer.stop();
            _sequencer.close();
        }
        if (_transmitter != null) {
            _transmitter.close();
        }
    }

    private void loadSequence(Sequence seq) {
        try {
            _sequencer.open();
        } catch (MidiUnavailableException e) {
            System.out.println(e);
        }
        try {
            _sequencer.setSequence(seq);
        } catch (InvalidMidiDataException e) {
            System.out.println(e);
        }

        if (_transmitter == null) {
            try {
                _transmitter = _sequencer.getTransmitter();
            } catch (MidiUnavailableException e) {
                System.out.println("Sequencer could not open a transmitter " + e);
            }
            _transmitter.setReceiver(this);
        }
    }

    public void send(MidiMessage message, long timeStamp) {
        //System.out.println("MidiMessage: " + message);
        timeStamp = timeStamp == -1 ? _sequencer.getMicrosecondPosition() : timeStamp;
        if (message instanceof ShortMessage) {
            ShortMessage msg = (ShortMessage) message;
            Object[] args = {new MIDIMessage(msg, timeStamp)};
            _midiEventListener.call("listener", args);
        }
    }

    public void meta(MetaMessage meta) {
        //System.out.println("MetaMessage: " + meta);
        Object[] args = {meta};
        _metaEventListener.call("listener", args);
    }

    public Sequence getSequence() {
        return _sequence;
    }

    public boolean addDirectConnection(MIDIDevice device) {
        
        System.out.println("addDirectConnection:" + device);

        if (_receiver != null) {
            _receiver.close();
        }

        _receiver = device.getReceiver();   
        if(_receiver == null){
            return false;
        }

        if (_transmitter2 == null) {
            try {
                _transmitter2 = _sequencer.getTransmitter();
            } catch (MidiUnavailableException e) {
                System.out.println("Sequencer could not open a transmitter " + e);
                return false;
            }
        }
        _transmitter2.setReceiver(_receiver);
        
        return true;
    }
    
    public void removeDirectConnection(){
        if(_transmitter2 != null){
            _transmitter2.close();
        }
        if (_receiver != null) {
            _receiver.close();
        }
        _transmitter2 = null;
        _receiver = null;
    }
    
    public boolean hasDirectConnection(){
        return _receiver != null;
    }

    //composition:
    public void play() {
        if (_sequencer.isOpen()) {
            _sequencer.start();
        }
    }

    public void pause() {
        if (_sequencer.isOpen()) {
            _sequencer.stop();
        }
    }

    public void stop() {
        if (_sequencer.isOpen()) {
            _sequencer.setMicrosecondPosition(0L);
            _sequencer.stop();
        }
    }

    public Long getMicrosecondPosition() {
        return _sequencer.getMicrosecondPosition();
    }

    public void setMicrosecondPosition(int pos) {
        _sequencer.setMicrosecondPosition(Long.parseLong("" + pos));
    }

    public float getTempoInBPM() {
        return _sequencer.getTempoInBPM();
    }

    public void setTempoInBPM(int bpm) {
        _sequencer.setTempoInBPM(Float.parseFloat("" + bpm));
    }

    public float getTempoFactor() {
        return _sequencer.getTempoFactor();
    }

    public void setTempoFactor(float factor) {
        _sequencer.setTempoFactor(factor);
    }

    public void muteTrack(int index) {
        _sequencer.setTrackMute(index, true);
    }

    public void unmuteTrack(int index) {
        _sequencer.setTrackMute(index, false);
    }

    //to be implemented
    public void createNewSequence() {
        //_sequence = new Sequence();
        _sequence.getMicrosecondLength();
        _sequence.getTickLength();
    }
}
