package it.uniroma2.ispw.users;

import static it.uniroma2.ispw.Constants.ADMINISTRATIVE_ROLE;

public class AdministrativeStaffMember extends User {

    public static final Integer ROLE = ADMINISTRATIVE_ROLE;


    public AdministrativeStaffMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    public AdministrativeStaffMember() { }
}
