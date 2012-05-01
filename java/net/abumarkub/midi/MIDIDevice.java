/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  
 */
package net.abumarkub.midi;

import java.applet.AppletContext;
import java.net.MalformedURLException;
import java.net.URL;
import javax.sound.midi.*;
import netscape.javascript.JSObject;

public class MIDIDevice implements Receiver {

    private MidiDevice _device;
    private MidiDevice.Info _deviceInfo;
    private Receiver _receiver;
    private Transmitter _transmitter;
    private Transmitter _transmitter2;
    public MIDIDeviceInfo info;
    private AppletContext _context;
    private JSObject _jsEventListener;
    public int id;
    public String deviceType;
    public String deviceName;
    public String deviceManufacturer;
    public String deviceVersion;
    public String deviceDescription;

    public MIDIDevice(MidiDevice device, int index, String type, AppletContext context) {
        
        _device = device;
        _deviceInfo = device.getDeviceInfo();
        _context = context;
                
        info = new MIDIDeviceInfo(index, type, _deviceInfo);

        id = index;
        deviceType = type;
        deviceName = info.deviceName;
        deviceManufacturer = info.deviceManufacturer;
        deviceVersion = info.deviceVersion;
        deviceDescription = info.deviceDescription;

        System.out.println("[" + type + "]" + info.deviceName);

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

    @Override
    public void close() {
        if (_receiver != null) {
            _receiver.close();
        }
        if (_transmitter != null) {
            _transmitter.close();
        }
        if (_transmitter2 != null) {
            _transmitter2.close();
        }

        if (_device.isOpen()) {
            _device.close();
        }
    }

    public boolean open() {

        if (!_device.isOpen()) {
            try {
                _device.open();
            } catch (MidiUnavailableException e) {
                System.out.println("error opening device: " + e);
                return false;
            }
        }

        if (info.deviceType.equals("input")) {
            try {
                _transmitter = _device.getTransmitter();
                _transmitter.setReceiver(this);
            } catch (MidiUnavailableException e) {
                System.out.println("error getting transmitter: " + e);
                return false;
            }
        } else if (info.deviceType.equals("output")) {
            try {
                _receiver = _device.getReceiver();
            } catch (MidiUnavailableException e) {
                System.out.println("error getting receiver: " + e);
                return false;
            }
        }
        return true;
    }

    public boolean addEventListener(String id) {
        
        if(deviceType.equals("output")){
            System.out.println("Can not add eventlistener to an output");
            return false;
        }
        
        if (id.equals("midimessage")) {
            if (_transmitter == null) {
                try {
                    _transmitter = _device.getTransmitter();
                } catch (MidiUnavailableException e) {
                    System.out.println("Device " + deviceName + " could not open a transmitter " + e);
                    return false;
                }
            }
            _transmitter.setReceiver(this);
            return true;
        }
        return false;
    }
    
    
    //@TODO: currently you can connect only 1 eventlistener!
    public boolean addEventListener(String id, JSObject eventListener) {
        
        _jsEventListener = eventListener;
        
        if(deviceType.equals("output")){
            System.out.println("Can not add eventlistener to an output");
            return false;
        }
        
        if (id.equals("midimessage")) {
            if (_transmitter == null) {
                try {
                    _transmitter = _device.getTransmitter();
                } catch (MidiUnavailableException e) {
                    System.out.println("Device " + deviceName + " could not open a transmitter " + e);
                    return false;
                }
            }
            _transmitter.setReceiver(this);
            return true;
        }
        return false;
    }

    public boolean setDirectOutput(MIDIDevice device) {
        
        if(deviceType.equals("output")){
            System.out.println("Can not add an output to an output!");
            return false;
        }
        
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
                _transmitter2 = _device.getTransmitter();
            } catch (MidiUnavailableException e) {
                System.out.println("Device " + deviceName + " could not open a transmitter " + e);
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

    public MidiDevice getDevice() {
        return _device;
    }

    public Receiver getReceiver() {
        return _receiver;
    }

    public Transmitter getTransmitter() {
        return _transmitter;
    }

    public boolean sendMIDIMessage(MIDIMessage message) {
        
        if(deviceType.equals("input")){
            System.out.println("Can not send a MIDI message to an input!");
            return false;
        }

        //System.out.println("sendMIDIMessage:" + message.command);
        ShortMessage sm = new ShortMessage();
        try {
            sm.setMessage(message.command, message.channel, message.data1, message.data2);
            _receiver.send(sm, message.timeStamp);
        } catch (InvalidMidiDataException e) {
            System.out.println("error sending MIDI message: " + e);
            return false;
        } catch (NullPointerException e) {
            System.out.println("error sending MIDI message: " + e);
            return false;
        }
        return true;
    }

    public boolean sendMIDIMessage(int command, int channel, int data1, int data2, int timeStamp) {
        
        if(deviceType.equals("input")){
            System.out.println("Can not send a MIDI message to an input!");
            return false;
        }

        //System.out.println("sendMIDIMessage:" + message.command);
        ShortMessage sm = new ShortMessage();
        try {
            sm.setMessage(command, channel, data1, data2);
            _receiver.send(sm, timeStamp);
        } catch (InvalidMidiDataException e) {
            System.out.println("error sending MIDI message: " + e);
            return false;
        } catch (NullPointerException e) {
            System.out.println("error sending MIDI message: " + e);
            return false;
        }
        return true;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        //System.out.println(message.toString() + " " + _eventListener);
        if (message instanceof ShortMessage) {
            
            ShortMessage tmp = (ShortMessage) message;
            MIDIMessage msg = new MIDIMessage(tmp, timeStamp);

            //@TODO: loop over all event listeners, check if they want to be updated via AppletContext or Live Connect, and dispatch to all
            
            if(1 == 2){//just a way of ignoring the AppletContext send method
                String jsMsg = id + "," + msg.command + "," + msg.channel + "," + msg.data1 + "," + msg.data2 + "," + timeStamp + ",'" + msg.toString() + "'";            
                sendMessageViaContext("javascript:midiBridge.onMIDIData(" + jsMsg + ")");
            }else{//currently sending via Live Connect is preferred
                Object[] args = {msg};     
                _jsEventListener.call("listener",args);
            }
        }
    }

    @Override
    public String toString() {
        return info.toString();
    }
}
