package ru.itpark;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JdbcTransactionHelper {
    private DataSource ds;

    public JdbcTransactionHelper() throws NamingException {
        final InitialContext context = new InitialContext();
        this.ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
    }

    public int createEntry(String status, String url){
        return 0;
    }
    public void updateStatus(String status, int id){

    }
}
