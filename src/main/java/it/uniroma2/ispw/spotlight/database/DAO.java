package it.uniroma2.ispw.spotlight.database;

import java.sql.*;

import static it.uniroma2.ispw.spotlight.Constants.*;

public abstract class DAO<T> {

    private Connection db = null;

    public DAO() { }

    public Connection getConnection() throws ClassNotFoundException, SQLException{

        // dynamic loading of the db driver
        Class.forName(DB_DRIVER_CLASS_NAME);
        if (this.db == null) {
            this.db = DriverManager.getConnection(DB_HOST, DB_USER, DB_PWD);
            this.db.setAutoCommit(false);
        }
        return db;
    }

    public abstract void update(T o) throws Exception;
    public abstract void delete(T o) throws Exception;

}
