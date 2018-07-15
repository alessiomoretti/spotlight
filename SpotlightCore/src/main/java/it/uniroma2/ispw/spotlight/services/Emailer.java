package it.uniroma2.ispw.spotlight.services;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stubbed class to emulate an email service sending confirmation and cancellation emails
 * to the Event DLs and User email addresses
 */
public class Emailer {

    private final String confirmationTemplate = "";
    private final String cancellationTemplate = "";

    private static final Logger LOGGER = Logger.getLogger(Emailer.class.getName());


    public static void sendConfirmationMail(String mailAddress, String action, String item) {
        String mail = "TO: {0}; This mail confirm the success {1} of {2}.";
        LOGGER.log(Level.INFO, mail, new Object[]{mailAddress, action, item});
    }

    public static void sendCancellationMail(String mailAddress, String itemType, String itemName, String reason) {
        String mail = "TO: {0}; This mail confirm the cancelattion for {1} {2}, {3}\n" +
                      "Please contact your administration representative for further details";
        LOGGER.log(Level.INFO, mail, new Object[]{mailAddress, itemType, itemName, reason});
    }
}
