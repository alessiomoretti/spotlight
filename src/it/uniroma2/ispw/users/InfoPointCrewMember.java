package it.uniroma2.ispw.users;

public class InfoPointCrewMember extends User {

    public static final String ROLE = "INFOPOINT_ROLE";


    public InfoPointCrewMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    public InfoPointCrewMember() { }
}
