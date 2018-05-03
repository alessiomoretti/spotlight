package it.uniroma2.ispw.spotlight.users;

import it.uniroma2.ispw.spotlight.Constants;

public class Teacher extends User {

    private String department;

    public Teacher(String username, String firstName, String lastName, String emailAddress, String department) {
        super(username, firstName, lastName, emailAddress);
        this.setDepartment(department);
    }

    public Teacher() { }

    @Override
    public Integer getRole() {
        return Constants.TEACHER_ROLE;
    }

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
