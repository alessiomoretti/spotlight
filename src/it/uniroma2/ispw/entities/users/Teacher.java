package it.uniroma2.ispw.entities.users;

public class Teacher extends User {

    private String department;
    private static final String ROLE = "TEACHER_ROLE";

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
