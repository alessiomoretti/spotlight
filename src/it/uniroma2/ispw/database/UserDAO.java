package it.uniroma2.ispw.database;

import it.uniroma2.ispw.exceptions.AuthServiceException;
import it.uniroma2.ispw.users.InfoPointCrewMember;
import it.uniroma2.ispw.users.Teacher;
import it.uniroma2.ispw.users.AdministrativeStaffMember;
import it.uniroma2.ispw.users.User;

import static it.uniroma2.ispw.Constants.*;

import java.sql.*;

public class UserDAO extends DAO<User> {

    public User authenticateUser(String username, String hashed_pwd) throws AuthServiceException {

        // retrieving database connection
        Connection db = null;
        try {
            db = getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new AuthServiceException("Exception caught opening db connection");
        }

        // preparing query
        String sql = "SELECT * FROM user WHERE username=" + username + " AND password=" + hashed_pwd;

        // retrieving results
        try {
            ResultSet results = retrieve(sql);
            // check if any result is available
            if (results.first()) {
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
            } else {
                return null;
            }


        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new AuthServiceException("Exception caught retrieving user list");
        }

    }

    @Override
    public void update(User o) throws Exception { /* no implementation needed */ }
    @Override
    public void delete(User o) throws Exception { /* no implementation needed */}
}
