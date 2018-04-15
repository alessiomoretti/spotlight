package it.uniroma2.ispw.database;

import javax.swing.plaf.nimbus.State;
import java.sql.*;

public abstract class DAO {

    private static final String DB_USER              = "dbuser";
    private static final String DB_PWD               = "dbpassword";
    private static final String DB_HOST              = "jdbc:postgresql://localhost:5432/spotlight";
    private static final String DB_DRIVER_CLASS_NAME = "org.postgresql.Driver";

    public DAO() { }

    public static Connection getConnection() throws ClassNotFoundException, SQLException{

        // dynamic loading of the db driver
        Class.forName(DB_DRIVER_CLASS_NAME);
        return DriverManager.getConnection(DB_HOST, DB_USER, DB_PWD);
    }

    public static void closeConnection(Connection db) throws SQLException {
        db.close();
    }

    public ResultSet retrieve(Connection db, String query) throws SQLException {
        Statement stmt = db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet res = stmt.executeQuery(query);

        stmt.close();
        db.close();

        return res;
    }

    public abstract void update(Connection db, Object o) throws Exception;
}
