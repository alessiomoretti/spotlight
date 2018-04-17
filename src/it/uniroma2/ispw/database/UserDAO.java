package it.uniroma2.ispw.database;

import it.uniroma2.ispw.exceptions.AuthServiceException;
import it.uniroma2.ispw.exceptions.UserRetrievalException;
import it.uniroma2.ispw.users.InfoPointCrewMember;
import it.uniroma2.ispw.users.Teacher;
import it.uniroma2.ispw.users.AdministrativeStaffMember;
import it.uniroma2.ispw.users.User;

import static it.uniroma2.ispw.Constants.*;

import java.sql.*;

public class UserDAO extends DAO<User> {

    public User getUserByUsername(String username) throws UserRetrievalException {

        // preparing query to retrieve the user with the given username
        String sql = "SELECT 1 FROM users WHERE username=" + username;

        // retrieving results
        try {
            ResultSet results = retrieve(sql);
            return createUserFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new UserRetrievalException("Exception caught retrieving user list");
        }
    }

    public User authenticateUser(String username, String hashed_pwd) throws AuthServiceException {

        // preparing query to authenticate user
        String sql = "SELECT 1 FROM users WHERE username=" + username + " AND password=" + hashed_pwd;

        // retrieving results
        try {
            ResultSet results = retrieve(sql);
            return createUserFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new AuthServiceException("Exception caught retrieving user list");
        }

    }

    public static User createUserFromResultSet(ResultSet results) throws SQLException {

        // check if result set is not empty
        if (!results.first())
            return null;

        // creating user object according to role
        switch (results.getInt("role")) {
            case TEACHER_ROLE:
                return new Teacher(
                        results.getString("username"),
                        results.getString("firstname"),
                        results.getString("lastname"),
                        results.getString("email"),
                        results.getString("department"));
            case ADMINISTRATIVE_ROLE:
                return new AdministrativeStaffMember(
                        results.getString("username"),
                        results.getString("firstname"),
                        results.getString("lastname"),
                        results.getString("email"));
            case INFOPOINT_ROLE:
                return new InfoPointCrewMember(
                        results.getString("username"),
                        results.getString("firstname"),
                        results.getString("lastname"),
                        results.getString("email"));
            default:
                return null;
        }
    }

    @Override
    public void update(User o) throws Exception { /* no implementation needed */ }
    @Override
    public void delete(User o) throws Exception { /* no implementation needed */}
}
