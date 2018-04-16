package it.uniroma2.ispw.users;

import it.uniroma2.ispw.Constants.*;

import static it.uniroma2.ispw.Constants.*;

public abstract class User {


    private String username;
    private String firstName;
    private String lastName;
    private String emailAddress;

    public static final Integer ROLE = GENERIC_ROLE;



    public User() { }

    public User(String username, String firstName, String lastName, String emailAddress) {
        this.username     = username;
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.emailAddress = emailAddress;
    }

    public String getName() {
        return this.getLastName() + " " + this.getFirstName();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

}
