/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  
 */

package net.abumarkub.midi;

import javax.sound.midi.*;
import netscape.javascript.JSObject;

public class MIDIDevice implements Receiver {

    private MidiDevice _device;
    private MidiDevice.Info _deviceInfo;
    private Receiver _receiver;
    private Transmitter _transmitter;
    private JSObject _eventListener;
    public MIDIDeviceInfo info;
    public int id;
    public String deviceType;
    public String deviceName;
    public String deviceManufacturer;
    public String deviceVersion;
    public String deviceDescription;

    public MIDIDevice(MidiDevice device, int index, String type) {
        
        _device = device;
        _deviceInfo = device.getDeviceInfo();
        
        info = new MIDIDeviceInfo(index, type,_deviceInfo);

        id = index;
        deviceType = type;
        deviceName = info.deviceName;
        deviceManufacturer = info.deviceManufacturer;
        deviceVersion = info.deviceVersion;
        deviceDescription = info.deviceDescription;
        
        System.out.println("[" + type + "]" + info.deviceName);

        _receiver = null;
        _transmitter = null;
    }

    @Override
    public void close() {
        if (_receiver != null) {
            _receiver.close();
        }
        if (_transmitter != null) {
            _transmitter.close();
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

    public boolean addEventListener(String id, JSObject eventListener) {

        //System.out.println("addEventListener");
        if (id.equals("midimessage")) {
            _eventListener = eventListener;
            return true;
        }
        return false;
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

    public void sendMIDIMessage(MIDIMessage message) {
        //System.out.println("sendMIDIMessage:" + message.command);
        ShortMessage sm = new ShortMessage();
        try {
            sm.setMessage(message.command, message.channel, message.data1, message.data2);
            _receiver.send(sm, message.timeStamp);
        } catch (InvalidMidiDataException e) {
            System.out.println("error sending MIDI message: " + e);
        } catch (NullPointerException e) {
            System.out.println("error sending MIDI message: " + e);
        }
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        //System.out.println(message.toString() + " " + _eventListener);
        if (message instanceof ShortMessage) {
            ShortMessage msg = (ShortMessage) message;
            Object[] args = {new MIDIMessage(msg,timeStamp)};
            _eventListener.call("listener", args);
        }
    }
    
    @Override
    public String toString(){
        return info.toString();       
    }
}
