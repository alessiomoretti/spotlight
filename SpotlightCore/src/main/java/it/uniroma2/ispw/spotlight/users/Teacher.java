package it.uniroma2.ispw.spotlight.users;

import it.uniroma2.ispw.spotlight.Constants;

import static it.uniroma2.ispw.spotlight.Constants.TEACHER_ROLE;

public class Teacher extends User {

    private String department;
    private static final Integer role = TEACHER_ROLE;

    public Teacher(String username, String firstName, String lastName, String emailAddress, String department) {
        super(username, firstName, lastName, emailAddress);
        this.setDepartment(department);
    }

    public Teacher() { }

    /**
     * The teacher role is returned
     * @return Integer
     */
    @Override
    public Integer getRole() {
        return role;
    }

    /**
     * Return an extended version of user name, including the department
     * @return String, "last_name, first name, department"
     */
    @Override
    public String getName() {
        return super.getName() + ", " + this.getDepartment();
    }

    /**
     * Return the teacher department
     * @return String
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Set the teacher department
     * @param department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

}
