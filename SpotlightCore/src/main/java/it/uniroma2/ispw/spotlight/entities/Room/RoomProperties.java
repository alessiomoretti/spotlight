package it.uniroma2.ispw.spotlight.entities.Room;

/**
 * This class can be used to represent the room properties
 * as physical attributes and capabilities
 */
public class RoomProperties {

    private Integer capacity;
    private boolean projector;
    private boolean whiteboard;
    private boolean interactiveWhiteboard;
    private boolean videoCallCapable;
    private boolean microphone;

    /**
     * Room properties handled in the project are the following (not modifiable at runtime)
     * @param capacity Integer
     * @param projector Boolean
     * @param whiteboard Boolean
     * @param interactiveWhiteboard Boolean
     * @param videocallCapable Boolean
     * @param microphone Boolean
     */
    public RoomProperties(Integer capacity, boolean projector, boolean whiteboard, boolean interactiveWhiteboard, boolean videocallCapable, boolean microphone) {
        this.capacity              = capacity;
        this.projector             = projector;
        this.whiteboard            = whiteboard;
        this.interactiveWhiteboard = interactiveWhiteboard;
        this.videoCallCapable      = videocallCapable;
        this.microphone            = microphone;
    }

    /**
     * Return the room capacity
     * @return Integer
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * Return true if room has a projector
     * @return Boolean
     */
    public boolean hasProjector() {
        return projector;
    }

    /**
     * Return true if room has a whiteboard
     * @return Boolean
     */
    public boolean hasWhiteboard() {
        return whiteboard;
    }

    /**
     * Return true if room has an interactive whiteboard
     * @return Boolean
     */
    public boolean hasInteractiveWhiteboard() {
        return interactiveWhiteboard;
    }

    /**
     * Return true if room has videocall peripherals
     * @return Boolean
     */
    public boolean isVideocallCapable() {
        return videoCallCapable;
    }

    /**
     * Return true if room has a microphone
     * @return Boolean
     */
    public boolean hasMicrophone() {
        return microphone;
    }

}
