package it.uniroma2.ispw.users;

import static it.uniroma2.ispw.Constants.INFOPOINT_ROLE;

public class InfoPointCrewMember extends User {

    private Integer role = INFOPOINT_ROLE;


    public InfoPointCrewMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    public InfoPointCrewMember() { }
}
