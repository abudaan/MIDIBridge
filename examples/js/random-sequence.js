/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  example of how you can send Javascript generated MIDI events to a MIDI output
 * 
 *  dependecies:
 *  - MIDIBridge.js
 *  - MIDIDeviceSelector.js
 * 
 */

window.addEventListener('load', function() {
    
    var midiAccess,
    outputs,
    output,
    maxDelay = 1500,
    currentNote = 0,
    sequenceLength = 100,
    sequencePosition = 0,
    selectOutput = document.getElementById("outputs"),
    messages = document.getElementById("messages"),
    btnStart = document.getElementById("start"),
    btnStop = document.getElementById("stop");
    
    
    btnStart.addEventListener("click",function(){   
        if(!output){
            messages.innerHTML = "Please select a MIDI output first!";
            return;
        }
        messages.innerHTML = "";
        currentNote = 0;
        sequencePosition = 0;
        sendRandomMIDIMessage();
    },false);

    
    btnStop.addEventListener("click",function(){    
        currentNote = sequenceLength;
    },false);
    
    
    //this function acts as a basic sequencer
    function sendTimedMIDIEvent(midiMessage,delay,callback){
        
        function send(midiMessage){
            if(output){
                output.sendMIDIMessage(midiMessage);
                messages.innerHTML += midiMessage.toString() + " NOTENAME:" + midiBridge.getNoteName(midiMessage.data1) + "<br/>";
            }
            callback();
        }
        
        if(delay > 0){
            messages.innerHTML += " - - delaying " + delay + " ms<br/>";
            setTimeout(function(){
                send(midiMessage);
            },delay);
        }else{
            send(midiMessage);
        }
        
        messages.scrollTop = messages.scrollHeight;
    }
    
    
    //creates a random MIDI message and sends it with a random delay to the selected MIDI output
    function sendRandomMIDIMessage(){
        
        var delay, noteNumber, velocity, midiMessage;
                
        if(currentNote < sequenceLength){
            
            delay = Math.floor((Math.random() * maxDelay));//delay random between 0 an maxDelay milliseconds
            noteNumber = Math.floor((Math.random() * 87) + 21);//noteNumber random between 21 (A0) an 108 (C8)
            velocity = Math.floor((Math.random() * 127) + 10);//velocity random betweeen 10 and 127
            sequencePosition += delay * 1000;//position in microseconds
            midiMessage = midiAccess.createMIDIMessage(midiBridge.NOTE_ON, 0, noteNumber, velocity, sequencePosition);
                
            sendTimedMIDIEvent(midiMessage, delay, function(){
                //console.log(currentNote,sequenceLength);
                sendRandomMIDIMessage(++currentNote);
            });
        }else{
            messages.innerHTML += " - - stop";
            messages.scrollTop = messages.scrollHeight;
        }
    }
    
    midiBridge.init(function(_midiAccess){
        
        midiAccess = _midiAccess;
        outputs = midiAccess.enumerateOutputs();
        
        //create dropdown menu for MIDI outputs and add an event listener to the change event
        midiBridge.createMIDIDeviceSelector(selectOutput,outputs,"ouput",function(deviceId){
            if(output){
                output.close();
            }
            output = midiAccess.getOutput(outputs[deviceId]);
            messages.innerHTML = "connected to " + output.deviceName + "<br/>";
        });
    });
});

