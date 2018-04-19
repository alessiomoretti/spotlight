package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.database.DAO;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.users.User;

public abstract class DataAccessService<T> {

    private DAO<T> databaseInterface;
    private User currentUser;
    private Integer minRoleRequired = Constants.GENERIC_ROLE;

    public DataAccessService() { }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() throws AuthRequiredException {
        if (currentUser == null) throw new AuthRequiredException("No user is logged");
        else return currentUser;
    }

    public boolean hasCapability(User user) {
        // check if user has the required capability to operate with this service
        return user.getRole() >= minRoleRequired;
    }

    public void setDatabaseInterface(DAO<T> databaseInterface) {
        this.databaseInterface = databaseInterface;
    }

    public DAO<T> getDatabaseInterface() {
        return this.databaseInterface;
    }
}
