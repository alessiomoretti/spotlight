package it.uniroma2.ispw.spotlight.users;

import it.uniroma2.ispw.spotlight.Constants;

public class InfoPointCrewMember extends User {


    public InfoPointCrewMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    @Override
    public Integer getRole() {
        return Constants.INFOPOINT_ROLE;
    }


    public InfoPointCrewMember() { }
}
