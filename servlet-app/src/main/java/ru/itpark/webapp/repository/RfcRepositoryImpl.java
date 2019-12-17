package ru.itpark.webapp.repository;

import ru.itpark.util.JdbcTemplate;
import ru.itpark.util.RowMapper;
import ru.itpark.webapp.exception.DataAccessException;
import ru.itpark.webapp.model.DocumentModel;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RfcRepositoryImpl implements RfcRepository {
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    private RowMapper<DocumentModel> mapper = rs -> new DocumentModel(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("size"),
            rs.getString("date"),
            rs.getString("url")
    );

    public RfcRepositoryImpl() {
        this.jdbcTemplate = new JdbcTemplate();
        try {
            final InitialContext context = new InitialContext();
            this.ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<DocumentModel> getById(int id) {
        try {
            return jdbcTemplate.queryForObject(ds, "SELECT * FROM rfcs WHERE id = ?;", stmt -> {
                stmt.setInt(1, id);
                return stmt;
            }, mapper);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void removeById(int id) {
        try {
            jdbcTemplate.update(ds, "DELETE FROM rfcs WHERE id = ?;", stmt -> {
                stmt.setInt(1, id);
                return stmt;
            });
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public int getTotalNumber() {
        int total=0;
        String sql = "SELECT COUNT(*) FROM rfcs;";
        try {
            final Optional<Integer> opt = jdbcTemplate.queryForObject(ds, sql, o -> o.getInt(1));
            if (opt.isPresent()){
                total = opt.get();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e);
        }
        return total;
    }

    @Override
    public void initialize() {
        String sql = "CREATE TABLE IF NOT EXISTS rfcs (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, size TEXT NOT NULL, date TEXT NOT NULL,url TEXT NOT NULL);";
        try {
            jdbcTemplate.update(ds, sql, statement -> statement);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e);
        }
    }

    @Override
    public void saveItem(String submittedFileName, String size, String date, String url) {
        try {
                jdbcTemplate.<Integer>updateForId(ds, "INSERT INTO rfcs(name, size, date, url) VALUES (?, ?, ?, ?);", stmt -> {
                    stmt.setString(1, submittedFileName);
                    stmt.setString(2, size);
                    stmt.setString(3, date);
                    stmt.setString(4, url);
                    return stmt;
                });
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public List<DocumentModel> getFromTo(int from, int to) {
        try {
            return jdbcTemplate.queryForList(ds, String.format("SELECT id, name, size, date, url FROM rfcs LIMIT %d,50;", from-1), mapper);
//            return jdbcTemplate.queryForList(ds, String.format("SELECT id, name, size, date, url FROM rfcs WHERE (id>=%d AND id<=%d);", from, to), mapper);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
