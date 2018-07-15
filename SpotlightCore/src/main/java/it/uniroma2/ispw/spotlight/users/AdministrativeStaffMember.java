package it.uniroma2.ispw.spotlight.users;

import it.uniroma2.ispw.spotlight.Constants;

import static it.uniroma2.ispw.spotlight.Constants.ADMINISTRATIVE_ROLE;

public class AdministrativeStaffMember extends User {

    private static final Integer role = ADMINISTRATIVE_ROLE;

    public AdministrativeStaffMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    /**
     * The administrative staff role is returned
     * @return Integer
     */
    @Override
    public Integer getRole() {
        return role;
    }


    public AdministrativeStaffMember() { }
}
