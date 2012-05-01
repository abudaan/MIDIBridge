/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 *  
 */

package net.abumarkub.midi;

import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;


public class MIDIAccess {
    
    protected ArrayList<MIDIDevice> _inputs;
    protected ArrayList<MIDIDevice> _outputs;
    private ArrayList<MIDIDeviceInfo> _inputInfos;
    private ArrayList<MIDIDeviceInfo> _outputInfos;
    private ArrayList<MIDIDevice> _synths;
    private ArrayList<MIDIDevice> _sequencers;
    private ArrayList<MIDIDevice> _otherDevices;
    
    private Object [] _inputInfosArray;
    private Object [] _outputInfosArray;
    

    public MIDIAccess() {
                
        MidiDevice device;
        MidiDevice.Info[] infos = javax.sound.midi.MidiSystem.getMidiDeviceInfo();
        
        _inputs = new ArrayList<MIDIDevice>();
        _outputs = new ArrayList<MIDIDevice>();
        _inputInfos = new ArrayList<MIDIDeviceInfo>();
        _outputInfos = new ArrayList<MIDIDeviceInfo>();
        _synths = new ArrayList<MIDIDevice>();
        _sequencers = new ArrayList<MIDIDevice>();
        _otherDevices = new ArrayList<MIDIDevice>();

        for(int i = 0; i < infos.length; i++) {
                        
            try {
                device = javax.sound.midi.MidiSystem.getMidiDevice(infos[i]);
            } catch (MidiUnavailableException e) {
                System.out.println("could not get device " + infos[i]);
                continue;
            }
            
            int numRecv = device.getMaxReceivers();
            int numTrans = device.getMaxTransmitters();
            MIDIDevice midiDevice;
            String type;
            Boolean available;


            if (numRecv == -1 && numTrans == -1) {
                if (device instanceof Synthesizer) {
                    type = "synth";
                    available = checkDeviceAvailability(device);
                    if (available) {
                        _synths.add(new MIDIDevice(device,_synths.size(),type));
                    }
                } else if (device instanceof Sequencer) {
                    type = "sequencer";
                    available = checkDeviceAvailability(device);
                    if (available) {
                        _sequencers.add(new MIDIDevice(device,_sequencers.size(),type));
                    }
                } else {
                    type = "other";
                    available = checkDeviceAvailability(device);
                    if (available) {
                        _otherDevices.add(new MIDIDevice(device,_otherDevices.size(),type));
                    }
                }
            } else if (numRecv == 0) {
                type = "input";
                available = checkDeviceAvailability(device);
                //System.out.println(device.getDeviceInfo().getName() + " " + available);
                if (available) {
                    //System.out.println("adding input");
                    midiDevice = new MIDIDevice(device,_inputs.size(),type); 
                    _inputs.add(midiDevice);
                    _inputInfos.add(midiDevice.info);
                }
            } else if (numTrans == 0) {
                type = "output";
                available = checkDeviceAvailability(device);
                if (available) {
                    //System.out.println("adding output");
                    midiDevice = new MIDIDevice(device,_outputs.size(),type); 
                    _outputs.add(midiDevice);
                    _outputInfos.add(midiDevice.info);
                }
            }
        }
        
        _inputInfosArray = _inputInfos.toArray();
        _outputInfosArray = _outputInfos.toArray();
    }
    
    public boolean closeInputs(){
        Iterator i = _inputs.iterator();
        while(i.hasNext()){
            MIDIDevice device = (MIDIDevice) i.next();
            device.close();
        }
        return true;
    }

    public boolean closeOutputs(){
        Iterator i = _outputs.iterator();
        while(i.hasNext()){
            MIDIDevice device = (MIDIDevice) i.next();
            device.close();
        }
        return true;
    }

    public MIDIDevice getInput(MIDIDeviceInfo info) {
        MIDIDevice device = _inputs.get(info.id);
        if(device.open()){
            return device;
        }
        return null;
    }

    public MIDIDevice getOutput(MIDIDeviceInfo info) {
        MIDIDevice device = _outputs.get(info.id);
        if(device.open()){
            return device;
        }
        return null;
    }
    
    public Object[] enumerateInputs(){
        return _inputInfosArray;
    }

    public Object[] enumerateOutputs(){
        return _outputInfosArray;
    }    
    
    public MIDIMessage createMIDIMessage(int command, int channel, int data1, int data2, int timeStamp){
        return new MIDIMessage(command,channel,data1,data2,Long.parseLong("" + timeStamp));
    }

    private boolean checkDeviceAvailability(MidiDevice device) {
        //if the device is currently in use, try to close it
        if (device.isOpen()) {
            try {
                device.close();
                return true;
            } catch (Exception e) {
                System.out.println("MidiDevices.checkDevice() can not close device " + device.getDeviceInfo().getName() + " " + e);
                return false;
            }
        }
        
        //if the device is not currently in use, try to open it
        try {
            device.open();
        } catch (MidiUnavailableException e) {
            System.out.println("MidiDevices.checkDevice() can not open device " + device.getDeviceInfo().getName() + " " + e);
            return false;
        }
        
        //device is available and can be opened, close it until we need it
        device.close();

        return true;
    }
}
