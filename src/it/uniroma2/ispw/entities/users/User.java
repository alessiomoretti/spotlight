package it.uniroma2.ispw.entities.users;

public abstract class User {


    private String username;
    private String firstName;
    private String lastName;
    private String emailAddress;

    private static final String ROLE = "GENERIC_USER";

    public User(String username, String firstName, String lastName, String emailAddress) {
        this.setUsername(username);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmailAddress(emailAddress);
    }

    public User() { }

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

    public static String getROLE() {
        return ROLE;
    }

}
