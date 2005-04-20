/*
 * EventProfile.java
 *
 * Created on 27 Март 2005 г., 23:25
 */

package ui;

/**
 *
 * @author Eugene Stahov
 */
public class EventProfile {
    
    public final static String[] MEDIATYPES =
    {  "audio/x-wav",  "audio/amr", "audio/midi"  };
    public final static String[] MEDIAEXTS =
    {  "wav",  "amr", "mid"  };
    
    public int lenVibra;
    public boolean enableLights;
    
    public String soundName;
    public String soundType;
    
    /*
     * default
     *audio/x-tone-seq
     *audio/x-wav
     *audio/midi
     *audio/sp-midi
     *
     * suemens
     *audio/x-wav
     *audio/x-mid
     *
     * alcatel
     *audio/x-wav
     *audio/x-tone-seq
     *audio/midi
     *audio/amr
     */
    
    /** Creates a new instance of EventProfile */
    public EventProfile(String soundName, int vibra, boolean lights) {
        lenVibra=vibra;
        enableLights=lights;
        if (soundName==null) return;
        int extpos=soundName.lastIndexOf('.');
        if (extpos<0) return;
        extpos++;
        
        for (int i=0; i<MEDIAEXTS.length; i++) {
            String ext=MEDIAEXTS[i];
            if (soundName.regionMatches(true, extpos, ext, 0, ext.length() ) ){
                this.soundName=soundName;
                this.soundType=MEDIATYPES[i];
                return;
            }
        }
    }
    
}
