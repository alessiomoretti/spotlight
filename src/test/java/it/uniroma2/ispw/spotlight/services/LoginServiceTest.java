package it.uniroma2.ispw.spotlight.services;

import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.AuthServiceException;
import org.junit.jupiter.api.Test;
import sun.rmi.runtime.Log;

class LoginServiceTest {

    private LoginService loginService;
    private String testUsername = "johndoe";
    private String testPassword = "d763ec748433fb79a04f82bd46133d55";

    @Test
    public void myTest() throws AuthServiceException, AuthRequiredException {
        this.loginService = new LoginService();
        if (this.loginService.authenticateUser(testUsername, testPassword))
            System.out.println("User Authenticated!");
        System.out.println("Authenticated: " + this.loginService.getCurrentUser().getUsername());
    }

}