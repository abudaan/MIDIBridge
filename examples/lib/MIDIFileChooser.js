/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  MIDIBridge plugin that loads a MIDI file from the user's local machine
 *  If the FileReader API is not supported, the file is send to a php script returns the file as a base64 encoded string.
 *  The php script in its simplest form:
 *  
 *  <?php
 *       $tmpFile = "tmp.mid";
 *  
 *       file_put_contents(
 *           $tmpFile, file_get_contents("php://input",r)
 *       );
 *  
 *       echo base64_encode(file_get_contents($tmpFile));
 *  ?>
 *  
 *  dependencies:
 *  - MIDIBridge.js
 *  - a php script that base64 encodes a MIDI file
 *  
 *  
 *  @TODO: add ohter ways of loading MIDI files for non-supported browsers, i.e. IE9
 *
 */

(function(mb){
       
    mb.createMIDIFileChooser = function(fileSelector, uploadUrl, sequencer, callback){

        var file = null,
        fileName = "";

        fileSelector.onchange = function(e) {
            
            if(e){
                e.preventDefault();
            }
        
            if (fileSelector.files) {
                file = fileSelector.files[0];
            } else {
                file = fileSelector.value;
            }

            fileName = file.name;
            //console.log(fileName);

            if (fileName === undefined) {
                callback("uploading files not supported natively; use a library like jQuery");
                return;
            }

            if (typeof window.FileReader === 'undefined') {
                console.log("via server");
                var request = new XMLHttpRequest();
                request.addEventListener("readystatechange", function(e) {
                    if (request.readyState == 4 && request.status === 200) {
                        loadMIDIFile(e.target.response);
                    }
                });

                request.open('POST', uploadUrl, true);
                request.setRequestHeader("Cache-Control", "no-cache");
                request.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                request.setRequestHeader("X-File-Name", fileName);
                request.send(file);

            } else {
                var reader = new FileReader();
                reader.onerror = function(error) {
                    callback("error reading file: " + error);
                }

                reader.onload = function(e) {
                    loadMIDIFile(e.target.result);
                };

                reader.readAsDataURL(file);
            }
        };

        function loadMIDIFile(base64data) {   
            
            var sequence = sequencer.loadBase64String(base64data),
            microsecondLength = sequence.getMicrosecondLength();
            
            callback({
                fileName : fileName,
                sequence : sequence,
                tickLength : sequence.getTickLength(),
                microsecondLength : microsecondLength,
                duration : microsecondLength/1000
            });
        }
    }
    
})(midiBridge);

