package it.uniroma2.ispw.database;

import javax.swing.plaf.nimbus.State;
import java.sql.*;

import static it.uniroma2.ispw.Constants.*;

public abstract class DAO<T> {

    private Connection db = null;

    public DAO() { }

    public Connection getConnection() throws ClassNotFoundException, SQLException{

        // dynamic loading of the db driver
        Class.forName(DB_DRIVER_CLASS_NAME);
        if (this.db == null)
            this.db = DriverManager.getConnection(DB_HOST, DB_USER, DB_PWD);
        return db;
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

    public abstract void update(T o) throws Exception;
    public abstract void delete(T o) throws Exception;

}
