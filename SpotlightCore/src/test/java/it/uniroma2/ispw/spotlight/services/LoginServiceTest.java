package it.uniroma2.ispw.spotlight.services;

import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.AuthServiceException;

import it.uniroma2.ispw.spotlight.users.Teacher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static it.uniroma2.ispw.spotlight.Constants.*;

class LoginServiceTest {

    private LoginService loginService;
    private String testUsername = "johndoe";
    private String testPassword = "d763ec748433fb79a04f82bd46133d55";
    private String name    = "Doe John, History";
    private String email        = "john.doe@uni.com";
    private Integer role        = TEACHER_ROLE;
    private String department   = "History";


    @Test
    public void authTest() throws AuthServiceException, AuthRequiredException {
        this.loginService = new LoginService();
        if (this.loginService.authenticateUser(testUsername, testPassword)) {
            Teacher teacher = (Teacher) this.loginService.getCurrentUser();
            Assertions.assertNotNull(teacher);
            Assertions.assertEquals(this.name, teacher.getName());
            Assertions.assertEquals(this.role, teacher.getRole());
            Assertions.assertEquals(this.department, teacher.getDepartment());
            System.out.println("User Authenticated!");
        }
    }

}