/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  example of how you can load and play a MIDI file
 * 
 *  dependecies:
 *  - MIDIBridge.js
 *  - MIDIFileChooser.js
 *  - MIDIDeviceSelector.js
 *  - MIDIProgramSelector.js
 * 
 */

window.addEventListener('load', function() {

    var midiAccess = null,
    sequencer = null,
    output = null,
    outputs = null,
    selectOutput = document.getElementById("outputs"),
    selectProgram = document.getElementById("programs"),
    btnPlay = document.getElementById("btn-play"),
    btnPause = document.getElementById("btn-pause"),
    btnStop = document.getElementById("btn-stop"),
    slider = document.getElementById("slider"),
    chooseFile = document.getElementById("choose-file"),
    info = document.getElementById("info"),
    lastMessage = document.getElementById("last-message"),
    uploadUrl = "php/midiToBase64.php",
    duration,position; 
    
    
    if(midiBridge.userAgent === "msie8/win"){
        midiBridge.wrapElement(btnPlay);
        midiBridge.wrapElement(btnPause);
        midiBridge.wrapElement(btnStop);
    }    
    
      
    //setup playback controls for the sequencer
    btnPlay.addEventListener("click", function() {
        sequencer.play();
    }, false);

    btnPause.addEventListener("click", function() {
        sequencer.pause();
    }, false);

    btnStop.addEventListener("click", function() {
        sequencer.stop();
        slider.setPercentage(0);
        info.getElementsByTagName("span")[7].innerHTML = "0:00:000";
        lastMessage.innerHTML = "";
    }, false);
    
   
    
    //setup song position slider
    slider = midiBridge.createSlider("position", 500, 8, 0, 100, 0);
    
    slider.addEventListener("changed", function(value) {
        sequencer.setMicrosecondPosition(value * 1000) //value in microseconds as String!
    });

    slider.addEventListener("startDrag", function(value) {
        sequencer.pause();
    });

    slider.addEventListener("stopDrag", function(value) {
        sequencer.play()
    });
    

  
    //add a program select dropdown menu and add an event listener to the change event
    midiBridge.createMIDIProgramSelector(selectProgram,function(programId){
        if(output && midiAccess){
            output.sendMIDIMessage(midiAccess.createMIDIMessage(midiBridge.PROGRAM_CHANGE, 0, programId, 0));
        }
    });
    
   
    
    midiBridge.init(function(_midiAccess){
        
        midiAccess = _midiAccess;
        outputs = midiAccess.enumerateOutputs();
        sequencer = midiBridge.getSequencer();
        console.log(sequencer.getTempoInBPM());
         
        //create dropdown menu for MIDI outputs and add an event listener to the change event
        midiBridge.createMIDIDeviceSelector(selectOutput,outputs,"ouput",function(deviceId){
            if(output){
                output.close();
            }
            output = midiAccess.getOutput(outputs[deviceId]);
            console.log(output.toString());
        });
        
        
        //create a MIDI file chooser
        midiBridge.createMIDIFileChooser(chooseFile, uploadUrl, sequencer, function(args){
            if(args.fileName === undefined){
                info.style.color = "#f00";
                info.innerHTML = args;
                return;
            }
            
            info.innerHTML = "<span class='label'>file:</span> <span class='value'>" + args.fileName + "</span> ";
            info.innerHTML += "<span class='label'>length:</span><span class='value'>" + midiBridge.formatMicroseconds(args.microsecondLength) + "</span> ";
            info.innerHTML += "<span class='label'>ticks:</span><span class='value'>" + args.tickLength + "</span> ";
            info.innerHTML += "<span class='label'>position:</span><span id='time' class='value'>0:00:000</span>";

            duration = args.duration;
            slider.setRange(0, duration);
            position = info.querySelector("#time");            
        });


        sequencer.addEventListener("midimessage",function(e){
            lastMessage.innerHTML = e.toString() + "<br/>";
            var timeStamp = parseInt(e.timeStamp);
            slider.setPercentage(((timeStamp / 1000) >> 0) / duration, false);
            position.innerHTML = midiBridge.formatMicroseconds(timeStamp);
            if(output){
                output.sendMIDIMessage(e);
            }
        });        
    });
}, false);
