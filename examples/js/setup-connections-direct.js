/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  example of how you can connect MIDI inputs and outputs with MIDIAccess
 * 
 *  The differece with the setup-connections example, is that in this example the output 
 *  gets connected directly in Java via setDirectOutput(); 
 * 
 *  This results in better playback performance. So if your application does a lot
 *  of graphical updates on incoming MIDI events, you should choose this method.
 *  
 *  Compared to the setup-connections example, changes are in the event listeners of the 
 *  MIDI device dropdown menu's.
 *  
 *  In the code starting at line 55, the MIDI events are not passed on to any MIDI output;
 *  the events are only used to perform graphical updated.
 *  
 *  And in the code starting at line 69, the MIDI input is directly connected to the
 *  selected output.
 * 
 *  dependecies:
 *  - MIDIBridge.js
 *  - MIDIDeviceSelector.js
 *  
 */

window.addEventListener('load', function() {

    var midiAccess = null,
    input = null,
    output = null,
    selectInput = document.getElementById("inputs"),
    selectOutput = document.getElementById("outputs"),
    midiMessages = document.getElementById("messages");
    
    midiBridge.init(function(_midiAccess){
        
        var inputs,outputs;
        
        midiAccess = _midiAccess;
        inputs = midiAccess.enumerateInputs();
        outputs = midiAccess.enumerateOutputs();
 
        //create dropdown menu for MIDI inputs and add an event listener to the change event
        midiBridge.createMIDIDeviceSelector(selectInput,inputs,"input",function(deviceId){
            if(input){
                input.close();
            }
            
            input = midiAccess.getInput(inputs[deviceId]);
            
            if(input){
                //listen for incoming MIDI messages
                input.addEventListener("midimessage",function(e){
                    if(e.command === midiBridge.NOTE_OFF || e.command === midiBridge.NOTE_ON){
                        midiMessages.innerHTML += e.toString() + " NOTENAME:" + midiBridge.getNoteName(e.data1,midiBridge.NOTE_NAMES_FLAT) + "<br/>";
                    }else{
                        midiMessages.innerHTML += e.toString() + "<br/>";
                    }
                    midiMessages.scrollTop = midiMessages.scrollHeight;
                });
            }
        });

        //create dropdown menu for MIDI outputs and add an event listener to the change event
        midiBridge.createMIDIDeviceSelector(selectOutput,outputs,"ouput",function(deviceId){
            if(output){
                output.close();
            }

            output = midiAccess.getOutput(outputs[deviceId]);
            
            if(output && input){
                input.setDirectOutput(output);
            }
        });          
    });

}, false);
