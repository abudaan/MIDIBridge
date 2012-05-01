/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  Class that has static methods that converts the data in a MIDI message to a human readable String
 *  
 */

package net.abumarkub.midi;

public class MIDIData {

    public static final int NOTE_OFF = 0x80;//128
    public static final int NOTE_ON = 0x90;//144
    public static final int POLY_PRESSURE = 0xA0;//160
    public static final int CONTROL_CHANGE = 0xB0;//176
    public static final int PROGRAM_CHANGE = 0xC0;//192
    public static final int CHANNEL_PRESSURE = 0xD0;//208
    public static final int PITCH_BEND = 0xE0;//224
    public static final int SYSTEM_EXCLUSIVE = 0xF0;//240
    public static final String NOTE_OFF_VERBOSE = "NOTE OFF";
    public static final String NOTE_ON_VERBOSE = "NOTE ON";
    public static final String POLY_PRESSURE_VERBOSE = "POLY PRESSURE";
    public static final String CONTROL_CHANGE_VERBOSE = "CONTROL CHANGE";
    public static final String PROGRAM_CHANGE_VERBOSE = "PROGRAM CHANGE";
    public static final String CHANNEL_PRESSURE_VERBOSE = "CHANNEL PRESSURE";
    public static final String PITCH_BEND_VERBOSE = "PITCH BEND";
    public static final String SYSTEM_EXCLUSIVE_VERBOSE = "SYSTEM EXCLUSIVE";

    public static String getCommand(int command) {
        String description = "";
        switch (command) {
            case 128:
                description = "NOTE OFF";
                break;
            case 144:
                description = "NOTE ON";
                break;
            case 160:
                description = "POLY AFTERTOUCH";
                break;
            case 176:
                description = "CONTROL CHANGE";
                break;
            case 192:
                description = "PROGRAM CHANGE";
                break;
            case 208:
                description = "CHANNEL AFTERTOUCH";
                break;
            case 224:
                description = "PITCH BEND";
                break;
            case 240:
                description = "SYSTEM EXCLUSIVE";
                break;
            case 241:
                description = "MIDI TIME CODE";
                break;
            case 242:
                description = "SONG POSITION POINTER";
                break;
            case 243:
                description = "SONG SELECT";
                break;
            case 244:
                description = "RESERVED";
                break;
            case 245:
                description = "RESERVED";
                break;
            case 246:
                description = "TUNE REQUEST";
                break;
            case 247:
                description = "EOX";
                break;
            case 248:
                description = "TIMING CLOCK";
                break;
            case 249:
                description = "RESERVED";
                break;
            case 250:
                description = "START";
                break;
            case 251:
                description = "CONTINUE";
                break;
            case 252:
                description = "STOP";
                break;
            case 253:
                description = "RESERVED";
                break;
            case 254:
                description = "ACTIVE SENSING";
                break;
            case 255:
                description = "SYSTEM RESET";
                break;
        }

        return description;
    }

    public static String getControlChangeMessage(int message) {
        String description = "UNDEFINED";
        switch (message) {
            case 0:
                description = "BANK SELECT";
                break;
            case 1:
                description = "MOD WHEEL";
                break;
            case 2:
                description = "BREATH CONTROLLER";
                break;
            case 4:
                description = "FOOT CONTROLLER";
                break;
            case 5:
                description = "PORTAMENTO TIME";
                break;
            case 6:
                description = "DATA ENTRY MSB";
                break;
            case 7:
                description = "CHANNEL VOLUME";
                break;
            case 10:
                description = "PANNING";
                break;
            case 11:
                description = "EXPRESSION CONTROLLER";
                break;
            case 12:
                description = "EFFECT CONTROL 1";
                break;
            case 13:
                description = "EFFECT CONTROL 1";
                break;
            case 16:
                description = "GENERAL PURPOSE CONTROLLER 1";
                break;
            case 17:
                description = "GENERAL PURPOSE CONTROLLER 2";
                break;
            case 18:
                description = "GENERAL PURPOSE CONTROLLER 3";
                break;
            case 19:
                description = "GENERAL PURPOSE CONTROLLER 4";
                break;
            case 32:
                description = "LSB FOR BANK SELECT";
                break;
            case 33:
                description = "LSB FOR MOD WHEEL";
                break;
            case 34:
                description = "LSB FOR BREATH CONTROLLER";
                break;
            case 35:
                description = "LSB FOR CONTROL 3";
                break;
            case 36:
                description = "LSB FOR FOOT CONTROLLER";
                break;
            case 37:
                description = "LSB FOR PORTAMENTO TIME";
                break;
            case 38:
                description = "LSB FOR DATA ENTRY";
                break;
            case 39:
                description = "LSB FOR CHANNEL VOLUME";
                break;
            case 40:
                description = "LSB FOR BALANCE";
                break;
            case 41:
                description = "LSB FOR CONTROL 9";
                break;
            case 42:
                description = "LSB FOR PAN";
                break;
            case 43:
                description = "LSB FOR EXPRESSION CONTROLLER";
                break;
            case 44:
                description = "LSB FOR EFFECT CONTROL 1";
                break;
            case 45:
                description = "LSB FOR EFFECT CONTROL 2";
                break;
            case 46:
                description = "LSB FOR CONTROL 14";
                break;
            case 47:
                description = "LSB FOR CONTROL 15";
                break;
            case 48:
                description = "LSB FOR GEN PURP CTRL 1";
                break;
            case 49:
                description = "LSB FOR GEN PURP CTRL 2";
                break;
            case 50:
                description = "LSB FOR GEN PURP CTRL 3";
                break;
            case 51:
                description = "LSB FOR GEN PURP CTRL 4";
                break;
            case 52:
                description = "LSB FOR CONTROL 20";
                break;
            case 53:
                description = "LSB FOR CONTROL 21";
                break;
            case 54:
                description = "LSB FOR CONTROL 22";
                break;
            case 55:
                description = "LSB FOR CONTROL 23";
                break;
            case 56:
                description = "LSB FOR CONTROL 24";
                break;
            case 57:
                description = "LSB FOR CONTROL 25";
                break;
            case 58:
                description = "LSB FOR CONTROL 26";
                break;
            case 59:
                description = "LSB FOR CONTROL 27";
                break;
            case 60:
                description = "LSB FOR CONTROL 28";
                break;
            case 61:
                description = "LSB FOR CONTROL 29";
                break;
            case 62:
                description = "LSB FOR CONTROL 30";
                break;
            case 63:
                description = "LSB FOR CONTROL 31";
                break;
            case 64:
                description = "SUSTAIN";
                break;
            case 65:
                description = "PORTAMENTO";
                break;
            case 66:
                description = "SOSTENUTO";
                break;
            case 67:
                description = "SOFT PEDAL";
                break;
            case 68:
                description = "LEGATO FOOTSWITCH";
                break;
            case 69:
                description = "HOLD 2";
                break;
            case 70:
                description = "SOUND VARIATION";
                break;
            case 71:
                description = "TIMBRE HARM INTENSITIVITY";
                break;
            case 72:
                description = "RELEASE TIME";
                break;
            case 73:
                description = "ATTACK";
                break;
            case 74:
                description = "BRIGHTNESS";
                break;
            case 75:
                description = "DECAY";
                break;
            case 76:
                description = "VIBRATO RATE";
                break;
            case 77:
                description = "VIBRATO DEPTH";
                break;
            case 78:
                description = "VIBRATO DELAY";
                break;
            case 79:
                description = "SOUND CONTROLLER 10";
                break;

            
            case 91:
                description = "REVERB SEND LEVEL";
                break;



            case 120:
                description = "ALL SOUND OFF";
                break;
            case 121:
                description = "RESET ALL CONTROLLERS";
                break;
            case 122:
                description = "LOCAL CONTROL";
                break;
            case 123:
                description = "ALL NOTES OFF";
                break;
            case 124:
                description = "OMNI MODE OFF";
                break;
            case 125:
                description = "OMNI MODE ON";
                break;
            case 126:
                description = "MONO MODE ON";
                break;
            case 127:
                description = "POLY MODE ON";
                break;
        }
        return description;
    }
}
