package it.uniroma2.ispw.spotlight.users;

import it.uniroma2.ispw.spotlight.Constants;

public class AdministrativeStaffMember extends User {


    public AdministrativeStaffMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    @Override
    public Integer getRole() {
        return Constants.ADMINISTRATIVE_ROLE;
    }


    public AdministrativeStaffMember() { }
}
