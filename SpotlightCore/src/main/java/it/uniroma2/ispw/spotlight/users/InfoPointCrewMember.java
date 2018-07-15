package it.uniroma2.ispw.spotlight.users;

import it.uniroma2.ispw.spotlight.Constants;

import static it.uniroma2.ispw.spotlight.Constants.INFOPOINT_ROLE;

public class InfoPointCrewMember extends User {

    private static final Integer role = INFOPOINT_ROLE;

    public InfoPointCrewMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    /**
     * The infopoint crew role is returned
     * @return Integer
     */
    @Override
    public Integer getRole() {
        return role;
    }


    public InfoPointCrewMember() { }
}
