/*!
 *  copyright 2012 abudaan http://abumarkub.net
 *  code licensed under MIT 
 *  http://abumarkub.net/midibridge/license
 * 
 * 
 *  
 */

package net.abumarkub.midi;

import java.applet.Applet;
import java.applet.AppletContext;
import java.net.MalformedURLException;
import java.net.URL;

public class MIDIApplet extends Applet{

    private AppletContext _context;
    private MIDIAccess _midiAccess;
    private MIDISequencer _midiSequencer;
    private Boolean _match;
    private static final long serialVersionUID = 1L;

    public static void main(String args[]) {
        MIDIApplet midiApplet = new MIDIApplet();
        midiApplet.init();
    }

    @Override
    public synchronized void init() {
        String javaVersion = System.getProperty("java.version");
        int currentVersion = getVersionAsNumber(javaVersion);
        int requiredVersion = getVersionAsNumber(getParameter("minJavaVersion"));
        _match = currentVersion >= requiredVersion;
        System.out.println("[init] MIDIBridge 0.6.3 Java Version OK: " + _match);
    }

    @Override
    public void start() {
        
        _context = getAppletContext();
        
        if(_match) {
            _midiAccess = new MIDIAccess(_context);
            _midiSequencer = new MIDISequencer(_context);
        }
        System.out.println("[start] " + _context.toString() + " : " + System.getProperty("java.version") + " : " + System.getProperty("java.vendor"));
        String url;
        if (_match) {
            url = "javascript:midiBridge.ready()";
        } else {
            url = "javascript:midiBridge.error('Please update your Java plugin (your version: " + System.getProperty("java.version") + " required version: " + getParameter("minJavaVersion") + ")')";
        }
        
        try {
            _context.showDocument(new URL(url));
        } catch (MalformedURLException me) {
            System.out.println(me);
        } catch (Exception e) {
            System.out.println(e);
        }        
    }

    @Override
    public void stop() {
        _midiAccess.closeInputs();
        _midiAccess.closeOutputs();
        _midiSequencer.close();
        _context = null;
        System.gc();
        System.out.println("[stop]");
    }

    @Override
    public void destroy() {
        System.gc();
        System.runFinalization();
        System.out.println("[destroy]");
    }
    
    public boolean ready(){
        return true;
    }
    
    public MIDIAccess getMIDIAccess(){
        return _midiAccess;
    }

    public MIDISequencer getSequencer(){
        return _midiSequencer;
    }

    private int getVersionAsNumber(String version) {
        String tmp = version.substring(0, 1);
        tmp += version.substring(2, 3);
        return Integer.parseInt(tmp);
    }
}
