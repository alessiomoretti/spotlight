package spotlightweb;

import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.AuthServiceException;
import it.uniroma2.ispw.spotlight.helpers.MD5Helper;
import it.uniroma2.ispw.spotlight.services.LoginService;
import it.uniroma2.ispw.spotlight.users.User;

public class LoginBean {

    private String username;
    private String password;
    private User currentUser;

    public LoginBean() {
        this.username = "";
        this.password = "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getCurrentUser() { return currentUser; }

    public boolean authenticate() {
        if (getUsername().length() != 0 && getPassword().length() != 0) {
            // hashing pwd
            String hashedPassword = MD5Helper.getHashedString(getPassword());
            try {
                // proceeding with authentication
                LoginService loginService = new LoginService();
                if (loginService.authenticateUser(getUsername(), hashedPassword)) {
                    currentUser = loginService.getCurrentUser();
                    return true;
                }
                return false;
            } catch (AuthServiceException | AuthRequiredException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // if not correctly populated user and pwd
            return false;
        }
    }
}
