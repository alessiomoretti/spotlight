package it.uniroma2.ispw.spotlight;

public class Constants {

    // database info
    public static final String DB_USER              = "spotlight_admin";
    public static final String DB_PWD               = "spotlight_admin123";
    public static final String DB_HOST              = "jdbc:postgresql://localhost:5432/spotlight";
    public static final String DB_DRIVER_CLASS_NAME = "org.postgresql.Driver";

    // User roles to define their capabilities
    public static final int GENERIC_ROLE        = 0;
    public static final int INFOPOINT_ROLE      = 1;
    public static final int TEACHER_ROLE        = 2;
    public static final int ADMINISTRATIVE_ROLE = 3;
}
