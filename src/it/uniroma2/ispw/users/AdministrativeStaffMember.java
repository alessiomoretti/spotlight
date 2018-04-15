package it.uniroma2.ispw.users;

public class AdministrativeStaffMember extends User {

    public static final String ROLE = "ADMINISTRATIVE_ROLE";


    public AdministrativeStaffMember(String username, String firstName, String lastName, String emailAddress) {
        super(username, firstName, lastName, emailAddress);
    }

    public AdministrativeStaffMember() { }
}
