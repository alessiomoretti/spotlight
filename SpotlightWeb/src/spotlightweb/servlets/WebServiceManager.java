package spotlightweb.servlets;

import it.uniroma2.ispw.spotlight.services.ServiceManager;
import it.uniroma2.ispw.spotlight.users.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static spotlightweb.servlets.ServletConstants.CURRENT_USER;
import static spotlightweb.servlets.ServletConstants.SESSION_SERVICE_MANAGER;

public class WebServiceManager {

    public static ServiceManager getWebInstance(HttpServletRequest request, User user) {
        // retrieving request session
        HttpSession session = request.getSession();
        // check if any service manager is available in request session context
        if (session.getAttribute(SESSION_SERVICE_MANAGER) == null) {
            ServiceManager sm = new ServiceManager();
            // setting user in service manager
            sm.getLoginService().setCurrentUser(user);
            request.getSession().setAttribute(SESSION_SERVICE_MANAGER, sm);
            return sm;
        } else {
            return (ServiceManager) request.getSession().getAttribute(SESSION_SERVICE_MANAGER);
        }
    }

    public static void storeServiceManager(HttpServletRequest request, ServiceManager sm) {
        request.getSession().setAttribute(SESSION_SERVICE_MANAGER, sm);
    }
}
