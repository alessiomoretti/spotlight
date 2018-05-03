package it.uniroma2.ispw.spotlight.services;

import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.*;

public class ServiceManager {

    // a new instance of the ServiceManager will be used for each access to the application
    private LoginService loginService;
    private EventLookupService eventLookupService;
    private UserEventLookupService userEventLookupService;
    private EventManagementService eventManagementService;
    private RoomLookupService roomLookupService;
    private RoomManagementService roomManagementService;

    private static ServiceManager serviceManager = new ServiceManager();

    public LoginService getLoginService() {
        if (this.loginService == null)
            loginService = new LoginService();
        return loginService;
    }

    public EventLookupService getEventLookupService() throws AuthRequiredException {
        if (this.eventLookupService == null) {
            eventLookupService = new EventLookupService();
            eventLookupService.setCurrentUser(getLoginService().getCurrentUser());
        }
        return eventLookupService;
    }

    public UserEventLookupService getUserEventLookupService() throws AuthRequiredException {
        if (this.userEventLookupService == null) {
            userEventLookupService = new UserEventLookupService();
            userEventLookupService.setCurrentUser(getLoginService().getCurrentUser());
        }
        return userEventLookupService;
    }

    public EventManagementService getEventManagementService() throws AuthRequiredException {
        if (this.eventManagementService == null) {
            eventManagementService = new EventManagementService();
            eventManagementService.setCurrentUser(getLoginService().getCurrentUser());
            eventManagementService.setRoomManagementService(getRoomManagementService());
        }
        return eventManagementService;
    }

    public RoomLookupService getRoomLookupService() throws AuthRequiredException {
        if (this.roomLookupService == null) {
            roomLookupService = new RoomLookupService();
            roomLookupService.setCurrentUser(getLoginService().getCurrentUser());
        }
        return roomLookupService;
    }

    public RoomManagementService getRoomManagementService() throws AuthRequiredException {
        if (this.roomManagementService == null) {
            roomManagementService = new RoomManagementService();
            roomManagementService.setCurrentUser(getLoginService().getCurrentUser());
        }
        return roomManagementService;
    }

    public static ServiceManager getInstance() {
        return serviceManager;
    }

}
