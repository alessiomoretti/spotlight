package it.uniroma2.ispw.entities.Room;

public class RoomProperties {

    private Integer capacity;
    private boolean projector;
    private boolean whiteboard;
    private boolean interactiveWhiteboard;
    private boolean videoCallCapable;
    private boolean microphone;

    public RoomProperties(Integer capacity, boolean projector, boolean whiteboard, boolean interactiveWhiteboard, boolean videocallCapable, boolean microphone) {
        this.capacity              = capacity;
        this.projector             = projector;
        this.whiteboard            = whiteboard;
        this.interactiveWhiteboard = interactiveWhiteboard;
        this.videoCallCapable      = videocallCapable;
        this.microphone            = microphone;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public boolean hasProjector() {
        return projector;
    }

    public boolean hasWhiteboard() {
        return whiteboard;
    }

    public boolean hasInteractiveWhiteboard() {
        return interactiveWhiteboard;
    }

    public boolean hasVideocallCapable() {
        return videoCallCapable;
    }

    public boolean hasMicrophone() {
        return microphone;
    }

}
