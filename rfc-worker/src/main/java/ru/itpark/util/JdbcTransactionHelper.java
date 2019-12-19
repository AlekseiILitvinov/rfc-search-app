package ru.itpark.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcTransactionHelper {
    private DataSource ds;

    public JdbcTransactionHelper() throws NamingException {
        final InitialContext context = new InitialContext();
        this.ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
    }

    public int createEntry(String status, String url){
        return 0;
    }

    synchronized public void updateStatus(String status, int id){
        String sql="UPDATE results SET status = ? WHERE id = ?;";

        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, status);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initStatus(int id, int numberOfFiles) {
        String sql="UPDATE results SET status = ? WHERE id = ?;";

        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, "0 / " + numberOfFiles);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUrl(String url, int resultDbId) {
        String sql="UPDATE results SET url = ? WHERE id = ?;";

        try (
                Connection connection = ds.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, url);
            statement.setInt(2, resultDbId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
