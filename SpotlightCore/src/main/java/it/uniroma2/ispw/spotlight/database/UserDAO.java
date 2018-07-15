package it.uniroma2.ispw.spotlight.database;

import it.uniroma2.ispw.spotlight.exceptions.AuthServiceException;
import it.uniroma2.ispw.spotlight.exceptions.UserRetrievalException;
import it.uniroma2.ispw.spotlight.users.InfoPointCrewMember;
import it.uniroma2.ispw.spotlight.users.Teacher;
import it.uniroma2.ispw.spotlight.users.AdministrativeStaffMember;
import it.uniroma2.ispw.spotlight.users.User;

import static it.uniroma2.ispw.spotlight.Constants.*;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import static java.sql.Statement.NO_GENERATED_KEYS;

import java.sql.*;

/**
 * This DAO acts as a controller of the User objects at persistence level,
 * handling user authentication as weel
 */
public class UserDAO extends DAO<User> {

    /**
     * Return the user associated to the given username
     * @param username String
     * @return User
     * @throws UserRetrievalException
     */
    public User getUserByUsername(String username) throws UserRetrievalException {
        // preparing query to retrieve the user with the given username
        String sql = "SELECT * FROM users WHERE username=?";

        // retrieving results
        try {
            PreparedStatement pstm = getConnection().prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, username);
            ResultSet results = pstm.executeQuery();
            return createUserFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new UserRetrievalException("Exception caught retrieving user list");
        }
    }

    /**
     * Return a User (if any) associated to the username with the given password
     * @param username String
     * @param hashed_pwd String
     * @return User
     * @throws AuthServiceException
     */
    public User authenticateUser(String username, String hashed_pwd) throws AuthServiceException {
        // preparing query to authenticate user
        String sql = "SELECT * FROM users WHERE username=? AND password=?";

        // retrieving results
        try {
            PreparedStatement pstm = getConnection().prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, username);
            pstm.setString(2, hashed_pwd);
            ResultSet results = pstm.executeQuery();
            return createUserFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new AuthServiceException("Exception caught retrieving user list");
        }

    }

    /**
     * Return a User given a ResultSet
     * @param results ResultSet
     * @return User
     * @throws SQLException
     */
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
