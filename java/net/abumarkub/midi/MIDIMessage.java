/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  
 */


package net.abumarkub.midi;

import javax.sound.midi.ShortMessage;

public class MIDIMessage {
        
    public int status;
    public int channel;
    public int data1;
    public int data2;
    public int command;
    public Long timeStamp = -1L;

    public MIDIMessage(ShortMessage sm, Long ts) {
        command = sm.getCommand();
        channel = sm.getChannel();
        data1 = sm.getData1();
        data2 = sm.getData2();
        status = sm.getStatus();
        timeStamp = ts;
        if(command == 144 && data2 == 0){
            status = 128 + channel;
            command = 128;
        }
    }

    public MIDIMessage(int command_, int channel_, int data1_, int data2_, Long ts) {
        command = command_;
        channel = channel_;
        data1 = data1_;
        data2 = data2_;
        status = command_ + channel_;
        timeStamp = ts;
        if(command == 144 && data2 == 0){
            status = 128 + channel;
            command = 128;
        }
    }
    
    @Override
    public String toString(){
        StringBuilder message = new StringBuilder();
        
        if(command == 176){
            message.append("CMD:");
            message.append(MIDIData.getCommand(command));
            message.append(" CHAN:");
            message.append(channel);
            message.append(" ");
            message.append(MIDIData.getControlChangeMessage(data1));
            message.append(":");
            message.append(data2);
            //message.append(" | STATUS:");
            //message.append(status);
            message.append(" TIME:");
            message.append(timeStamp);                      
        }else if(command == 128 || command == 144){
            message.append("CMD:");
            message.append(MIDIData.getCommand(command));
            message.append(" CHAN:");
            message.append(channel);
            message.append(" NOTE:");
            message.append(data1);
            message.append(" VELOCITY:");
            message.append(data2);
            //message.append(" | STATUS:");
            //message.append(status);
            message.append(" TIME:");
            message.append(timeStamp);          
        }else{
            message.append("CMD:");
            message.append(MIDIData.getCommand(command));
            message.append(" CHAN:");
            message.append(channel);
            message.append(" DATA1:");
            message.append(data1);
            message.append(" DATA2:");
            message.append(data2);
            //message.append(" | STATUS:");
            //message.append(status);
            message.append(" TIME:");
            message.append(timeStamp);          
        }
        
        return message.toString();
    }
    
    public int[] toArray(){
        int[] message = new int[5];
        message[0] = command;
        message[1] = channel;
        message[2] = data1;
        message[3] = data2;
        message[4] = status;
//        message[0] = timeStamp;
        return message;
    }    
}