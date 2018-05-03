package it.uniroma2.ispw.spotlight.services;

import it.uniroma2.ispw.spotlight.database.UserDAO;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.AuthServiceException;
import it.uniroma2.ispw.spotlight.users.User;

public class LoginService {

    private UserDAO databaseInterface;
    private User currentUser;

    public LoginService() {
        this.databaseInterface = new UserDAO();
        this.currentUser = null;
    }

    public boolean authenticateUser(String username, String hashed_pwd) throws AuthServiceException {
        User u = databaseInterface.authenticateUser(username, hashed_pwd);
        if (u != null) {
            setCurrentUser(u);
            return true;
        } else {
            return false;
        }
    }

    public User getCurrentUser() throws AuthRequiredException {
        if (currentUser == null)
            throw new AuthRequiredException("Forbidden: authentication required");
        else return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
