package it.uniroma2.ispw.entities.users;

public class InfoPointCrewMember extends User {

    private static final String ROLE = "INFOPOINT_ROLE";


    public InfoPointCrewMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    public InfoPointCrewMember() { }
}
