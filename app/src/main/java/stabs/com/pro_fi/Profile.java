package stabs.com.pro_fi;

import org.w3c.dom.ProcessingInstruction;

/**
 * Blueprint of a profile
 */

public class Profile {

    private int id;
    private String name;
    private int ringtone;
    private int media;
    private int notification;
    private int system;

    /**
     * Profile constructor without id
     * @param name the unique name of the profile
     * @param ringtone the integer value of the ringtone volume
     * @param media the integer value of the media volume
     * @param notification the integer value of the notification volume
     * @param system the integer value of the system volume
     */
    public Profile(String name, int ringtone, int media, int notification, int system) {
        this.name = name;
        this.ringtone = ringtone;
        this.media = media;
        this.notification = notification;
        this.system = system;
    }

    /**
     * Profile constructor with id
     * @param id the id the profile
     * @param name the unique name of the profile
     * @param ringtone the integer value of the ringtone volume
     * @param media the integer value of the media volume
     * @param notification the integer value of the notification volume
     * @param system the integer value of the system volume
     */
    public Profile(int id, String name, int ringtone, int media, int notification, int system) {
        this(name, ringtone, media, notification, system);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRingtone() {
        return ringtone;
    }

    public void setRingtone(int ringtone) {
        this.ringtone = ringtone;
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getSystem() {
        return system;
    }

    public void setSystem(int system) {
        this.system = system;
    }
}
