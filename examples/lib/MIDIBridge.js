/* 
 *  copyright 2012 abudaan
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 *  
 *  
 *  This version is supported by all browsers that support native JSON parsing:
 *   - Firefox 3.5+
 *   - Chrome 4.0+
 *   - Safari 4.0+ (only on OSX)
 *   - Opera 10.5+ 
 *   - Internet Explorer 8.0+
 * 
 *  If you want this version to work with other browsers, you can use the JSON parsing methods of your favorite Javascript
 *  framework (e.g. jQuery, Dojo, YUI, Mootools, etc.)
 *  
 *  This version does not work on Safari/Windows because it does not fully support Live Connect
 * 
 *  Note for IE8 users: if you include MidiBridge.js (or preferably the minified version of it: midibridge-latest.min.js) in your html,
 *  the method addEventListener will be added to the window object. In fact this method is just a wrapper around the attachEvent method,
 *  see code at the bottom of this file.
 * 
 * 
 */

(function() {
    
    try {
        console.log("");
    } catch (e) {
        console = {
            'log': function(args) {}
        };
    }
    
    var midiBridge = {
        
        //all MIDI commands
        NOTE_OFF : 0x80, //128
        NOTE_ON : 0x90, //144
        POLY_PRESSURE : 0xA0, //160
        CONTROL_CHANGE : 0xB0, //176
        PROGRAM_CHANGE : 0xC0, //192
        CHANNEL_PRESSURE : 0xD0, //208
        PITCH_BEND : 0xE0, //224
        SYSTEM_EXCLUSIVE : 0xF0, //240
        MIDI_TIMECODE : 241,
        SONG_POSITION : 242,
        SONG_SELECT : 243,
        TUNE_REQUEST : 246,
        EOX : 247,
        TIMING_CLOCK : 248,
        START : 250,
        CONTINUE : 251,
        STOP : 252,
        ACTIVE_SENSING : 254,
        SYSTEM_RESET : 255,
        
        //other statics
        NOTE_NAMES_SHARP : "sharp",
        NOTE_NAMES_FLAT : "flat",
        NOTE_NAMES_SOUNDFONT : "soundfont",
        NOTE_NAMES_ENHARMONIC_SHARP : "enh-sharp",
        NOTE_NAMES_ENHARMONIC_FLAT : "enh-flat",
        
        //rest
        version : "0.6.0",
        noteNameModus : "sharp",
        userAgent : ""
    },
    midiStatusCodes = [],
    midiCommands = [],
    noteNames = {},
    parseJSON = null,//variable that holds a reference to the JSON parser method of your liking, defaults to native JSON parsing
    javaDir = "java",//directory of the applet, relative to the directory of the html file
    debug = false,
    onReady = null,
    onError = null,    
    passStatusCodes = null,//these are the status codes that the midibridge passes on to the application
    midiBridgeJar = "midiapplet-" + midiBridge.version + ".jar",
    applet = null,
    MIDIAccess = null,
    Sequencer = null,
    sequencerJs = null,
    ua = navigator.userAgent.toLowerCase(),
    userAgent;
    
    
    if(ua.indexOf("chrome") !== -1){
        userAgent = "chrome";
    }else if(ua.indexOf("safari") !== -1){
        userAgent = "safari";
    }else if(ua.indexOf("firefox") !== -1){
        userAgent = "firefox";
    }else if(ua.indexOf("opera") !== -1){
        userAgent = "opera";
    }else if(ua.indexOf("msie 7") !== -1){
        userAgent = "msie7";
    }else if(ua.indexOf("msie 8") !== -1){
        userAgent = "msie8";
    }else if(ua.indexOf("msie 9") !== -1){
        userAgent = "msie9";
    }

    if(ua.indexOf("windows") !== -1){
        userAgent += "/win";
    }else if(ua.indexOf("macintosh") !== -1){ 
        userAgent += "/osx";
    }
    
    midiBridge.userAgent = userAgent;
    //console.log(ua," => ",userAgent);
    
    
    //human readable representation of status byte in MIDI data
    midiStatusCodes[0x80] = "NOTE OFF";
    midiStatusCodes[0x90] = "NOTE ON";
    midiStatusCodes[0xA0] = "POLY PRESSURE";//POLYPHONIC AFTERTOUCH
    midiStatusCodes[0xB0] = "CONTROL CHANGE";
    midiStatusCodes[0xC0] = "PROGRAM CHANGE";
    midiStatusCodes[0xD0] = "CHANNEL PRESSURE";//AFTERTOUCH
    midiStatusCodes[0xE0] = "PITCH BEND";
    midiStatusCodes[0xF0] = "SYSTEM EXCLUSIVE";
    midiStatusCodes[241] = "MIDI TIMECODE";
    midiStatusCodes[242] = "SONG POSITION";
    midiStatusCodes[243] = "SONG SELECT";
    midiStatusCodes[244] = "RESERVED 1";
    midiStatusCodes[245] = "RESERVED 2";
    midiStatusCodes[246] = "TUNE REQUEST";
    midiStatusCodes[247] = "EOX";
    midiStatusCodes[248] = "TIMING CLOCK";
    midiStatusCodes[249] = "RESERVED 3";
    midiStatusCodes[250] = "START";
    midiStatusCodes[251] = "CONTINUE";
    midiStatusCodes[252] = "STOP";
    midiStatusCodes[254] = "ACTIVE SENSING";
    midiStatusCodes[255] = "SYSTEM RESET";
    
    
    //create status codes for all channels
    for(var statusCode in midiStatusCodes){
        var command = parseInt(statusCode);
        switch(command){
            case midiBridge.NOTE_OFF:
            case midiBridge.NOTE_ON:
            case midiBridge.POLY_PRESSURE: //POLYPHONIC AFTERTOUCH
            case midiBridge.CONTROL_CHANGE:
            case midiBridge.PROGRAM_CHANGE:
            case midiBridge.CHANNEL_PRESSURE: //AFTERTOUCH
            case midiBridge.PITCH_BEND:
                midiCommands.push(command);
                for(var channel = 0; channel < 16; channel++){
                    midiStatusCodes[command + channel] = midiStatusCodes[statusCode];
                }
                break;
            default:
                midiCommands.push(command);
                break;
        }
    }
    
    
    //notenames in different modi
    noteNames = {
        "sharp" : ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"],
        "flat" : ["C", "D&#9837;", "D", "E&#9837;", "E", "F", "G&#9837;", "G", "A&#9837;", "A", "B&#9837;", "B"],
        "soundfont" : ["C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"],
        "enh-sharp" : ["B#", "C#", "C##", "D#", "D##", "E#", "F#", "F##", "G#", "G##", "A#", "A##"],
        "enh-flat" : ["D&#9837;&#9837;", "D&#9837;", "E&#9837;&#9837;", "E&#9837;", "F&#9837;", "G&#9837;&#9837;", "G&#9837;", "A&#9837;&#9837;", "A&#9837;", "B&#9837;&#9837;", "B&#9837;", "C&#9837;"]
    };

    /**
     *  static method called to initialize the MidiBridge
     *  possible arguments:
     *  1) callback [function] callback when the MidiBridge is ready
     *  2) configuration object
     *      - onError : [function] callback in case of an error
     *      - debug : [true,false] midiBridge prints out error messages
     *      - javaDir : [string] the folder where you store the midiapplet.jar on your webserver, defaults to "java"
     *      - passCommands : [array] an array that contains MIDI commands that are send to the application
     *      - filterCommands : [array] an array that contains MIDI commands that are *not* send to the application
     */
    midiBridge.init = function () {
                
        function browserNotSupported(browser){
            document.body.style.color = "#f00";
            document.body.style.fontSize = "20px";
            document.body.innerHTML = browser + " is not supported.";            
        }
        if(userAgent === "safari/win"){
            browserNotSupported("Safari for Windows");
            return;
        }else if(userAgent === "msie7/win"){
            browserNotSupported("Internet Explorer 7");
            return;
        }
        //IE8 requires this to be done after the document has fully loaded
        parseJSON = JSON.parse;
        
        var args = Array.prototype.slice.call(arguments);
        
        if (args.length === 1 && typeof args[0] === "function") {
            
            onReady = args[0];
            
        } else if (args.length === 2  && typeof args[0] === "object" && typeof args[1] === "function") {
            
            var config = args[0],
            i,maxi,j,maxj,command1,command2;
            
            onReady = args[1];
            onError = config.onError;
            debug = config.debug;
            javaDir = config.javaDir || javaDir;
            
            passStatusCodes = {};
            
            var checkChannelEvents = function(command,commands){
                switch(command){
                    case midiBridge.NOTE_OFF:
                    case midiBridge.NOTE_ON:
                    case midiBridge.POLY_PRESSURE: //POLYPHONIC AFTERTOUCH
                    case midiBridge.CONTROL_CHANGE:
                    case midiBridge.PROGRAM_CHANGE:
                    case midiBridge.CHANNEL_PRESSURE: //AFTERTOUCH
                    case midiBridge.PITCH_BEND:
                        for(channel = 0; channel < 16; channel++){
                            commands[command + channel] = 1;
                        }                            
                        break;
                    default:
                        commands[command] = 1;               
                }
            }

            if(config.filterCommands !== undefined){
                for(i = 0, maxi = midiCommands.length; i < maxi; i++){
                    command1 = midiCommands[i];
                    var filterCommand = false;
                    for(j = 0, maxj = config.filterCommands.length; j < maxj; j++){
                        command2 = config.filterCommands[j];
                        if(command2 == command1){
                            filterCommand = true;
                            break;
                        }
                    }
                    if(!filterCommand){
                        checkChannelEvents(command1,passStatusCodes);
                    }
                }
            }else if(config.passCommands !== undefined){
                for(i = 0, maxi = config.passCommands.length; i < maxi; i++){
                    checkChannelEvents(config.passCommands[i],passStatusCodes);
                }
            }else{
                passStatusCodes = null;
            }           

            if(debug){
                console.log(passStatusCodes,userAgent);
            }
        }

        
        //very simple java plugin detection
        if (!navigator.javaEnabled()) {
            if (onError) {
                onError("no java plugin found; install or enable the java plugin");
            } else {
                console.log("no java plugin found; install or enable the java plugin");
            }
            return;
        }

        //if you are using the JSON parse method of your favorite Javascript framework replace the following lines by only loadJava();
        var reportBrowserError = function(){
            if(onError) {
                onError("supported browsers: Firefox 3.5+, Chrome 4.0+, Safari 4.0+, Opera 10.5+, Internet Explorer 8.0+");
            } else if(debug) {
                console.log("supported browsers: Firefox 3.5+, Chrome 4.0+, Safari 4.0+, Opera 10.5+, Internet Explorer 8.0+");
            }            
        }
        try {
            if(parseJSON === undefined){
                reportBrowserError();
                return;
            }
            loadJava();
        } catch(e) {
            reportBrowserError();
        }
    };
    
    //called by the applet in case of error (i.e. the user doesn't have the right version of the Java plugin)
    midiBridge.error = function(message){
        if(onError){
            onError(message);
        }else if(debug){
            console.log(message);
        }
    }

    //called by the applet when the applet is initialized
    midiBridge.ready = function(){
        
        function getApplet(callback){
            if(debug){
                console.log("applet:",applet === null);
            }
            
            try {
                applet = getObject("midibridge-applet");
                applet.ready();
            } catch(e) {
                if(debug){
                    console.log(e)
                }
                setTimeout(function(){
                    getApplet(callback);
                },25);
                return;
            }

            if(!applet){
                if(debug){
                    console.log(applet);
                }
                setTimeout(function(){
                    getApplet(callback);
                },25);
            }else{
                callback();
            }
        }
        
        getApplet(initDevices);
    }
        
    function initDevices(){

        MIDIAccess = applet.getMIDIAccess();
        Sequencer = applet.getSequencer();


        //wrap the Java Sequencer object
        
        var stripBase64Header = function(data){
            return data.replace(/data:audio\/mid[i]?;base64,/,"");
        }

        sequencerJs = {
            addEventListener:function(eventId,callback){
                Sequencer.addEventListener(eventId,{
                    listener:callback
                });
            },
            stop:function(){
                Sequencer.stop();
            },
            play:function(){
                Sequencer.play();
            },
            pause:function(){
                Sequencer.pause();
            },
            loadBase64String:function(data){
                return Sequencer.loadBase64String(stripBase64Header(data));
            },
            playBase64String:function(data){
                return Sequencer.playBase64String(stripBase64Header(data));
            },
            getMicrosecondPosition:function(){
                return Sequencer.getMicrosecondPosition();
            },
            setMicrosecondPosition:function(microseconds){
                Sequencer.setMicrosecondPosition(microseconds);
            },
            setTempoInBPM:function(bpm){
                Sequencer.setTempoInBPM(bpm);
            },
            getTempoFactor:function(){
                return Sequencer.getTempoFactor();
            },
            setTempoFactor:function(factor){
                Sequencer.setTempoFactor(factor);
            },
            getTempoInBPM:function(){
                return Sequencer.getTempoInBPM();
            },
            getTracks:function(){
                return Sequencer.getTracks();
            },
            muteTrack:function(index){
                return Sequencer.muteTrack(index);
            },
            unmuteTrack:function(index){
                return Sequencer.unmuteTrack(index);
            },
            getSequence:function(){
                return Sequencer.getSequence();
            }
        };
        
        
        //wrap the Java MIDI input and output devices

        var wrapDevice = function(device){
            if(!device){
                if(debug){
                    console.log("device does not exist");
                    return null;
                }                
                return null;
            }
            try{
                if(device.deviceType == "input"){
                    device = MIDIAccess.getInput(device);
                }else if(device.deviceType == "output"){
                    device = MIDIAccess.getOutput(device);
                }else{
                    if(debug){
                        console.log("error while getting device",device.deviceName);
                        return null;
                    }                
                    return null;
                }
            }catch(e){
                if(debug){
                    console.log("error while getting device",device.deviceName);
                    return null;
                }                
                return null;
            }

            return {
                close:function(){
                    device.close();
                },
                open:function(){
                    if(device.open()){
                        return true;
                    }else{
                        if(debug){
                            console.log("could not open device", device.deviceName);
                        }
                        return false;
                    }
                },
                addEventListener:function(eventId,callback){
                    device.addEventListener(eventId,{
                        listener:function(e){
                            if(passStatusCodes && passStatusCodes[e.status] !== 1){
                                if(debug){
                                    console.log("MIDI message intercepted", e.status, e.channel, e.data1, e.data2);
                                }
                            }else{
                                callback(e);
                            }
                        }
                    });
                },
                sendMIDIMessage:function(event){
                    if(!device.getDevice().isOpen()){
                        return;
                    }
                    device.sendMIDIMessage(event);
                },
                toString:function(){
                    return device.toString();
                },
                deviceType:device.deviceType,
                deviceName:device.deviceName,
                deviceManufacturer:device.deviceManufacturer,
                deviceDescription:device.deviceDescription
            }
        };
        
        
        //wrap the Java MIDIAccess object and pass it to the callback of midiBridge.init();
        
        onReady({
            enumerateInputs:function(){
                return MIDIAccess.enumerateInputs();
            },
            enumerateOutputs:function(){
                return MIDIAccess.enumerateOutputs();
            },
            getInput:function(input){
                return wrapDevice(input);                   
            },
            getOutput:function(output){
                return wrapDevice(output);                   
            },
            closeInputs:function(){
                MIDIAccess.closeInputs();
            },
            closeOutputs:function(){
                MIDIAccess.closeOutputs();
            },
            createMIDIMessage : function(command,channel,data1,data2,timeStamp){
                timeStamp = timeStamp || -1;
                //var MIDIMessage = java.lang.Thread.currentThread().getContextClassLoader().loadClass("net.abumarkub.midi.MIDIMessage"); 
                return MIDIAccess.createMIDIMessage(command,channel,data1,data2,timeStamp);
            }
        });
    }
        
    midiBridge.getSequencer = function(){
        
        if(!sequencerJs){
            if(debug){
                console.log("Sequencer not (yet) available");
                return null;
            }
            return null;
        }
        
        return sequencerJs;
    }
        
    midiBridge.getNoteName = function(noteNumber, mode) {
        var octave = Math.floor(((noteNumber) / 12) - 1),
        noteName = noteNames[mode][noteNumber % 12];
        return noteName + "" + octave;
    };

    midiBridge.getNoteNumber = function(noteName, octave) {
        var index = -1,
        noteNumber;
        noteName = noteName.toUpperCase();
        for(var key in noteNames) {
            var modus = noteNames[key];
            for(var i = 0, max = modus.length; i < max; i++) {
                if(modus[i] === noteName) {
                    index = i;
                    break;
                }
            }
        }
        if(index === -1) {
            return "invalid note name";
        }
        noteNumber = (12 + index) + (octave * 12);
        return noteNumber;
    };


    midiBridge.getStatus = function(code) {
        return midiStatusCodes[code];
    };
    
    midiBridge.getNiceTime = function(microseconds)
    {
        //console.log(microseconds);
        var r = "",     
        t     = (microseconds / 1000 / 1000) >> 0,
        h     = (t / (60 * 60)) >> 0,
        m     = ((t % (60 * 60)) / 60) >> 0,
        s     = t % (60),
        ms    = (((microseconds /1000) - (h * 3600000) - (m * 60000) - (s * 1000)) + 0.5) >> 0;
    
        //console.log(t,h,m,s,ms);
        
        r += h > 0 ?  h + ":" : "";
        r += h > 0 ? m < 10 ? "0" + m : m : m;
        r += ":";
        r += s < 10 ? "0" + s : s;
        r += ":";
        r += ms === 0 ? "000" : ms < 10 ? "00" + ms : ms < 100 ? "0" + ms : ms;
        
        return r;
    };

    //a div with the applet object is added to the body of your html document
    function loadJava(){
        //console.log("loadJava");
        var javaDiv = document.createElement("div"),
        html = "";
        
        //if(userAgent.indexOf("chrome") === -1){
        if(userAgent !== "safari/osx" && userAgent.indexOf("chrome") === -1){
            html += '<object tabindex="0" id="midibridge-applet" type="application/x-java-applet" height="1" width="1">';
            html += '<param name="codebase" value="' + javaDir + '/" />';
            html += '<param name="archive" value="' + midiBridgeJar + '" />';
            html += '<param name="code" value="net.abumarkub.midi.MIDIApplet" />';
            html += '<param name="scriptable" value="true" />';
            html += '<param name="minJavaVersion" value="1.6" />';
            html += 'Your browser needs the Java plugin to use the midibridge. You can download it <a href="http://www.java.com/en/" target="blank" title="abumarkub midibridge download java" rel="abumarkub midibridge download java">here</a>';
            html += '</object>';
        }else{
            html += '<applet id="midibridge-applet" code="net.abumarkub.midi.MIDIApplet.class" archive="' + midiBridgeJar + '" codebase="' + javaDir + '" width="1" height="1" mayscript>';
            html += '<param name="minJavaVersion" value="1.6">';
            html += '</applet>';
        }
                
        javaDiv.setAttribute("id", "midibridge-java");
        javaDiv.innerHTML = html;
        document.body.appendChild(javaDiv);
    }

    function getObject(objectName) {
        if(userAgent.indexOf("msie") !== -1 || userAgent.indexOf("chrome") !== -1 || userAgent.indexOf("safari") !== -1) {
            return window[objectName];
        } else {
            return document[objectName];
        }
    }

    //add addEventListener to IE8
    if(!window.addEventListener) {
        window.addEventListener = function(id, callback, bubble) {
            window.attachEvent("onload", callback);
        };
    }
    
    midiBridge.wrapElement = function(element){
        if(userAgent !== "msie8/win"){
            return;
        }
        element.addEventListener = function(id, callback, bubble){
            element.attachEvent(id, callback);
        }
    }
    
    window.midiBridge = midiBridge;

})(window);
