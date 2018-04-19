package it.uniroma2.ispw.spotlight.users;

import it.uniroma2.ispw.spotlight.Constants;

public class AdministrativeStaffMember extends User {

    private Integer role = Constants.ADMINISTRATIVE_ROLE;


    public AdministrativeStaffMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    public AdministrativeStaffMember() { }
}
