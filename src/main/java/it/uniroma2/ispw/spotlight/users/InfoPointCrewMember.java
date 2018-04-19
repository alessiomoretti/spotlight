package it.uniroma2.ispw.spotlight.users;

import it.uniroma2.ispw.spotlight.Constants;

public class InfoPointCrewMember extends User {

    private Integer role = Constants.INFOPOINT_ROLE;


    public InfoPointCrewMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    public InfoPointCrewMember() { }
}
