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

import java.applet.AppletContext;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.sound.midi.*;
import netscape.javascript.JSObject;
import org.apache.commons.codec.binary.Base64;

public class MIDISequencer implements Receiver, MetaEventListener {

    private Sequence _sequence;
    private Sequencer _sequencer;
    private Transmitter _transmitter;
    private Transmitter _transmitter2;
    private Receiver _receiver;
    private AppletContext _context;
    private JSObject _jsMIDIEventListener;
    private JSObject _jsMetaEventListener;
    private boolean _hasMetaEventListener;

    public MIDISequencer(AppletContext context) {
        _context = context;

        try {
            _sequencer = javax.sound.midi.MidiSystem.getSequencer(false);

        } catch (MidiUnavailableException e) {
            System.out.println(e);
        }

        _receiver = null;
        _transmitter = null;
        _transmitter2 = null;
    }
    
    //sending messages from Java to Javascript is faster via AppletContext then via Live Connect
    private void sendMessageViaContext(String url) {
        try {
            _context.showDocument(new URL(url));
        } catch (MalformedURLException me) {
            System.out.println(me);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean addEventListener(String id) {
        if (id.equals("midimessage")) {
            if (_transmitter == null) {
                try {
                    _transmitter = _sequencer.getTransmitter();
                } catch (MidiUnavailableException e) {
                    System.out.println("Sequencer could not open a transmitter " + e);
                    return false;
                }
            }
            _transmitter.setReceiver(this);
            return true;
        } else if (id.equals("metamessage")) {
            if (!_hasMetaEventListener) {
                _hasMetaEventListener = _sequencer.addMetaEventListener(this);
            }
            return true;
        }
        return false;
    }
    
    //@TODO: currently you can connect only 1 eventlistener!
    public boolean addEventListener(String id, JSObject eventListener) {
        
        if (id.equals("midimessage")) {
            if (_transmitter == null) {
                try {
                    _transmitter = _sequencer.getTransmitter();
                } catch (MidiUnavailableException e) {
                    System.out.println("Sequencer could not open a transmitter " + e);
                    return false;
                }
            }
            _jsMIDIEventListener = eventListener;
            _transmitter.setReceiver(this);
            return true;
        } else if (id.equals("metamessage")) {
            if (!_hasMetaEventListener) {
                _hasMetaEventListener = _sequencer.addMetaEventListener(this);
            }
            _jsMetaEventListener = eventListener;
            return true;
        }
        return false;
    }

    public Sequence loadBase64String(String data) {
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
        if (_transmitter2 != null) {
            _transmitter2.close();
        }
        if (_receiver != null) {
            _receiver.close();
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
    }

    public void send(MidiMessage message, long timeStamp) {
        //System.out.println("MidiMessage: " + message + " " + timeStamp);
        timeStamp = _sequencer.getMicrosecondPosition();
        if (message instanceof ShortMessage) {
            
            ShortMessage tmp = (ShortMessage) message;
            MIDIMessage msg = new MIDIMessage(tmp, timeStamp);

            //@TODO: loop over all event listeners, check if they want to be updated via AppletContext or Live Connect, and dispatch to all
            
            if(1 == 2){//just a way of ignoring the AppletContext send method
                String jsMsg = msg.command + "," + msg.channel + "," + msg.data1 + "," + msg.data2 + "," + timeStamp + ",'" + msg.toString() + "'";            
                sendMessageViaContext("javascript:midiBridge.onSequencerMIDIData(" + jsMsg + ")");
            }else{//currently sending via Live Connect is preferred
                Object[] args = {msg};     
                _jsMIDIEventListener.call("listener",args);
            }
        }
    }
    
    //@TODO: loop over all event listeners, check if they want to be updated via AppletContext or Live Connect, and dispatch to all
    public void meta(MetaMessage meta) {
        if(1 == 2){//just a way of ignoring the AppletContext send method
            StringBuilder jsMsg = new StringBuilder();
            jsMsg.append(meta.getType());
            jsMsg.append(",");
            jsMsg.append(meta.getStatus());

            byte[] message = meta.getMessage();
            for(int i = 0, maxi = message.length; i < maxi; i++){
                jsMsg.append(",");
                jsMsg.append(message[i]);
            }

            sendMessageViaContext("javascript:midiBridge.sequencerMetaData(" + jsMsg.toString() + ")");            
        }else{//currently sending via Live Connect is preferred
            Object[] args = {meta};
            _jsMetaEventListener.call("listener",args);          
        }
       
    }

    public Sequence getSequence() {
        return _sequence;
    }

    public boolean setDirectOutput(MIDIDevice device) {

        System.out.println("setDirectOutput: " + device.deviceName);

        if (_receiver != null) {
            _receiver.close();
        }

        _receiver = device.getReceiver();
        if (_receiver == null) {
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

    public void removeDirectOutput() {
        if (_transmitter2 != null) {
            _transmitter2.close();
        }
        if (_receiver != null) {
            _receiver.close();
        }
        _transmitter2 = null;
        _receiver = null;
    }

    public boolean hasDirectOutput() {
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

    //@TODO: implemented this
    public void createNewSequence() {
        //_sequence = new Sequence();
        _sequence.getMicrosecondLength();
        _sequence.getTickLength();
    }
}
