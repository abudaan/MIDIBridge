MIDIBridge
==========

An implementation in Java and Javascript of the W3C proposal for MIDI support in browsers by Jussi Kalliokoski.

You can see the proposal at https://gist.github.com/1752949


The only files you need to get started are:

/lib/midibridge-0.6.0.min.js
/java/midiapplet-0.6.0.jar

/lib/MidiBridge.js is the non-minified version of /lib/midibridge-0.6.0.min.js


index.html shows you how you embed the MIDIBridge in your html page; you can use this basic example as a starting point of your own code.

The MIDIBridge is written in native Javascript, so you can use it conflict-free with any Javascript framework. If you use a framework, you have to embed it before you embed the MIDIBridge.

In the /examples folder you will find more advanced examples. These examples also use MIDIBridge plugins, I have provided 3 plugins that makes it easier to develop your own projects and keeping your code as clean as possible.


If you come across bugs or issues, or if you have feature requests or other questions, file an issue on Github, or email me at daniel@abumarkub.net