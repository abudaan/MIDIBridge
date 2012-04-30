MIDIBridge


An implementation in Java and Javascript of the W3C proposal for MIDI support in browsers by Jussi Kalliokoski.

You can see the proposal at https://gist.github.com/1752949


The only files you need to get started are:

/lib/midibridge-0.6.0.min.js
/java/midiapplet-0.6.0.jar

/lib/MidiBridge.js is the non-minified version of /lib/midibridge-0.6.0.min.js


The MIDIBridge is written in native Javascript, so you can use it conflict-free with any Javascript framework. If you use a framework, you have to embed it in your HTML file before you embed the MIDIBridge. 

The examples in the /examples folder show you how to embed the MIDIBridge in a HTML page. 

The basic example shows a basic usage of MIDIAccess, you can use this as a starting point of your own code.

The other examples are a bit more advanced, these examples also use MIDIBridge plugins. I have provided 3 plugins that makes it easier to develop your own projects and keeping your code as clean as possible.


The documentation is in progress. See: https://github.com/abudaan/MIDIBridge/wiki


If you come across bugs or issues, or if you have feature requests or other questions, file an issue on Github, or email me at daniel@abumarkub.net

