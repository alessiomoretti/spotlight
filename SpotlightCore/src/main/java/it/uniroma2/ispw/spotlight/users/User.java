package it.uniroma2.ispw.spotlight.users;

import static it.uniroma2.ispw.spotlight.Constants.*;

/**
 * This abstract class can be used to realize the representation of a User
 * instance inside the Spotlight system
 */
public abstract class User {


    private String username;
    private String firstName;
    private String lastName;
    private String emailAddress;


    public User() { }

    /**
     * A base user is defined by the following parameters
     * @param username String
     * @param firstName String
     * @param lastName String
     * @param emailAddress String
     */
    public User(String username, String firstName, String lastName, String emailAddress) {
        this.username     = username;
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.emailAddress = emailAddress;
    }

    /**
     * A role should be defined for a user, it allows to define user privileges
     * @return Integer, UserRole constant
     */
    public abstract Integer getRole();

    /**
     * Return a printable extended version of the user name
     * @return String, "last_name, first_name"
     */
    public String getName() {
        return this.getLastName() + " " + this.getFirstName();
    }

    /**
     * Return the username used to log into the system
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username used to log into the system
     * @param username String
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Return the user first name
     * @return String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the user first name
     * @param firstName String
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * return the user last name
     * @return String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the user last name
     * @param lastName String
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Return the user email address used inside the organization
     * @return String
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Set the user email address used inside the organization
     * @param emailAddress String
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

}
