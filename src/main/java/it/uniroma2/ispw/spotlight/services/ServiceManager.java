package it.uniroma2.ispw.spotlight.services;

import it.uniroma2.ispw.spotlight.services.DataAccesServices.*;

public class ServiceManager {

    // initializing singletons to let services interact
    // a new instance of the ServiceManager will be used for each access to the application
    private LoginService loginService;
    private EventLookupService eventLookupService;
    private UserEventLookupService userEventLookupService;
    private EventManagementService eventManagementService;
    private RoomLookupService roomLookupService;
    private RoomManagementService roomManagementService;

    public LoginService getLoginService() {
        if (this.loginService == null)
            loginService = new LoginService();
        return loginService;
    }

    public EventLookupService getEventLookupService() {
        if (this.eventLookupService == null)
            eventLookupService = new EventLookupService();
        return eventLookupService;
    }

    public UserEventLookupService getUserEventLookupService() {
        if (this.userEventLookupService == null)
            userEventLookupService = new UserEventLookupService();
        return userEventLookupService;
    }

    public EventManagementService getEventManagementService() {
        if (this.eventManagementService == null)
            eventManagementService = new EventManagementService();
        return eventManagementService;
    }

    public RoomLookupService getRoomLookupService() {
        if (this.roomLookupService == null)
            roomLookupService = new RoomLookupService();
        return roomLookupService;
    }

    public RoomManagementService getRoomManagementService() {
        if (this.roomManagementService == null)
            roomManagementService = new RoomManagementService();
        return roomManagementService;
    }

}
