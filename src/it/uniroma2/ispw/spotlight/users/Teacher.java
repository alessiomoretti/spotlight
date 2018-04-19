package it.uniroma2.ispw.spotlight.users;

import it.uniroma2.ispw.spotlight.Constants;

public class Teacher extends User {

    private String department;
    private Integer role = Constants.TEACHER_ROLE;

    public Teacher(String username, String firstName, String lastName, String emailAddress, String department) {
        super(username, firstName, lastName, emailAddress);
        this.setDepartment(department);
    }

    public Teacher() { }

    @Override
    public String getName() {
        return super.getName() + ", " + this.getDepartment();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}
