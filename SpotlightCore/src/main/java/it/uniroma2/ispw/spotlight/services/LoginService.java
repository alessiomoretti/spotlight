package it.uniroma2.ispw.spotlight.services;

import it.uniroma2.ispw.spotlight.database.UserDAO;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.AuthServiceException;
import it.uniroma2.ispw.spotlight.users.User;

/**
 * This is a boundary service to manage login requests
 */
public class LoginService {

    private UserDAO databaseInterface;
    private User currentUser;

    public LoginService() {
        this.databaseInterface = new UserDAO();
        this.currentUser = null;
    }

    /**
     * Return true if the user is correctly authenticated given the username and password
     * @param username String
     * @param hashed_pwd String
     * @return Boolean
     * @throws AuthServiceException
     */
    public boolean authenticateUser(String username, String hashed_pwd) throws AuthServiceException {
        User u = databaseInterface.authenticateUser(username, hashed_pwd);
        if (u != null) {
            setCurrentUser(u);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the currently authenticated user, if any
     * @return User
     * @throws AuthRequiredException
     */
    public User getCurrentUser() throws AuthRequiredException {
        if (currentUser == null)
            throw new AuthRequiredException("Forbidden: authentication required");
        else return currentUser;
    }

    /**
     * Set the currently authenticated user
     * @param currentUser User
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
