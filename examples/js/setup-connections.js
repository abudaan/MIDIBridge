/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  example of how you can connect MIDI inputs and outputs with MIDIAccess
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
    
    
    function connectDevices(){
        if(input){
            input.addEventListener("midimessage",function(e){
                if(output){
                    output.sendMIDIMessage(e);
                }
                if(e.command === midiBridge.NOTE_OFF || e.command === midiBridge.NOTE_ON){
                    midiMessages.innerHTML += e.toString() + " NOTENAME:" + midiBridge.getNoteName(e.data1) + "<br/>";
                }else{
                    midiMessages.innerHTML += e.toString() + "<br/>";
                }
                midiMessages.scrollTop = midiMessages.scrollHeight;
            });
        }
    }

    midiBridge.init({
        debug:true,//prints out some debug info
        filterCommands:[midiBridge.PITCH_BEND,midiBridge.ACTIVE_SENSING]//these commands are filtered out -> they don't show up in the MIDI event handler
    },
    function(_midiAccess){
        
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
            connectDevices();        
        });

        //create dropdown menu for MIDI outputs and add an event listener to the change event
        midiBridge.createMIDIDeviceSelector(selectOutput,outputs,"ouput",function(deviceId){
            if(output){
                output.close();
            }
            output = midiAccess.getOutput(outputs[deviceId]);
            connectDevices();        
        });           
    });
}, false);
