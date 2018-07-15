package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.database.DAO;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.users.User;

/**
 * This abstract class can be used to realize a boundary service to handle and manage entities
 * @param <T>
 */
public abstract class DataAccessService<T> {

    private DAO<T> databaseInterface;
    private User currentUser;
    private Integer minRoleRequired = Constants.GENERIC_ROLE;

    public DataAccessService() { }

    /**
     * Set current user going to manage the data
     * @param user User
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Return the currently set user managing the data
     * @return User
     * @throws AuthRequiredException
     */
    public User getCurrentUser() throws AuthRequiredException {
        if (currentUser == null) throw new AuthRequiredException("No user is logged");
        else return currentUser;
    }

    /**
     * Return true if the current user has the capability for the request her submitted
     * @param user User
     * @return Boolean
     */
    public boolean hasCapability(User user) {
        // check if user has the required capability to operate with this service
        return user.getRole() >= minRoleRequired;
    }

    /**
     * Set the boundary database interface (DAO)
     * @param databaseInterface
     */
    public void setDatabaseInterface(DAO<T> databaseInterface) {
        this.databaseInterface = databaseInterface;
    }

    /**
     * Return the boundary database interface (DAO)
     * @return
     */
    public DAO<T> getDatabaseInterface() {
        return this.databaseInterface;
    }
}
