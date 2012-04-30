/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  example that show the most basic way of using MIDIAccess; it connects the first MIDI input to the first MIDI output
 *  
 *  if the first MIDI input is a keyboard and the firt MIDI output is a synthesizer, or is connected to a synthesizer,
 *  you should be able to hear what you play on your keyboard.
 *  
 *  if not, try the setup-connections example in the examples folder
 * 
 *  dependecies:
 *  - MIDIBridge.js
 *  
 */

window.addEventListener('load', function() {
    
    var content = document.getElementById("content"),
    messages = document.createElement("div");
    messages.setAttribute("id","messages");
    

    midiBridge.init(function(MIDIAccess){
        
        var input = MIDIAccess.getInput(MIDIAccess.enumerateInputs()[0]);
        content.innerHTML += "<div class='device'><span class='device-type'>input:</span><span class='device-name'>" + input.deviceName + "</span></div>";        
            
        var output = MIDIAccess.getOutput(MIDIAccess.enumerateOutputs()[0]);
        content.innerHTML += "<div class='device'><span class='device-type'>output:</span><span class='device-name'>" + output.deviceName + "</span></div>";                                   
        
        content.appendChild(messages);

        input.addEventListener("midimessage",function(e){
            messages.innerHTML += "<div class='message'>" + e.toString() + "</div>";
            output.sendMIDIMessage(e);
        });            
    });           
});