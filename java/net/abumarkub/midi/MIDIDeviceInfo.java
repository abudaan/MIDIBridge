/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  Wrapper for javax.sound.midi.MidiDevice.Info
 * 
 */
package net.abumarkub.midi;

import javax.sound.midi.MidiDevice;

public class MIDIDeviceInfo {

    public int id;
    public String deviceType;
    public String deviceName;
    public String deviceManufacturer;
    public String deviceVersion;
    public String deviceDescription;

    public MIDIDeviceInfo(int id_, String type, MidiDevice.Info info) {
        id = id_;
        deviceType = type;
        deviceName = info.getName();
        deviceManufacturer = info.getVendor();
        deviceVersion = info.getVersion();
        deviceDescription = info.getDescription();
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder();

        message.append("type:");
        message.append(deviceType);
        message.append(" ");
        message.append("name:");
        message.append(deviceName);
        message.append(" ");
        message.append("manufacturer:");
        message.append(deviceManufacturer);
        message.append(" ");
        message.append("version:");
        message.append(deviceVersion);
        message.append(" ");
        message.append("description:");
        message.append(deviceDescription);

        return message.toString();
    }
}
