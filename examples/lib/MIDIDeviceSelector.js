/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 *  MIDIBridge plugin that creates a dropdown menu for selecting MIDI inputs or MIDI outputs
 *  
 *  dependencies:
 *  - MIDIBridge.js
 * 
 */

(function(mb){
    
    mb.createMIDIDeviceSelector = function(select,devices,type,callback){
        
        select.appendChild(createOption("-1", "choose a MIDI " + type));
        
        //for IE8
        midiBridge.wrapElement(select);
        
        for(var i = 0, maxi = devices.length; i < maxi; i++) {
            var device = devices[i];
            select.appendChild(createOption(i, device.deviceName));
        }
  
        //helper function
        function createOption(id, label) {
            var option = document.createElement("option");
            option.setAttribute("id", id);
            option.innerHTML = label;
            return option;
        }
        
        function getSelectedDevice(){
            var device = select.options[select.selectedIndex];
            return device.id;
        }
              
        select.addEventListener("change", function(e) {
            callback(getSelectedDevice());
        }, false);
        
        return {
            getSelectedDevice:getSelectedDevice
        }
    }
    
})(midiBridge);
