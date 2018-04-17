package it.uniroma2.ispw.services;

import it.uniroma2.ispw.database.DAO;
import it.uniroma2.ispw.exceptions.AuthRequiredException;
import it.uniroma2.ispw.users.User;

import static it.uniroma2.ispw.Constants.*;

public abstract class DataAccessService<T> {

    private DAO<T> databaseInterface;
    private User currentUser;
    private Integer minRoleRequired = GENERIC_ROLE;

    public DataAccessService() { }

    public User getCurrentUser() {
        return currentUser;
        // TODO singleton call to retrieve an instance of LoginService
    }

    public boolean hasCapability(User user) {

        // check if user has the required capability to operate with this service
        return user.getRole() >= minRoleRequired;
    }
}
