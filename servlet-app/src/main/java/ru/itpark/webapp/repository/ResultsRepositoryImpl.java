package ru.itpark.webapp.repository;

import ru.itpark.util.JdbcTemplate;
import ru.itpark.util.RowMapper;
import ru.itpark.webapp.exception.DataAccessException;
import ru.itpark.webapp.model.ResultModel;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ResultsRepositoryImpl implements ResultsRepository {
    private JdbcTemplate jdbcTemplate;
    private DataSource ds;
    private RowMapper<ResultModel> mapper = rs -> new ResultModel(
            rs.getInt("id"),
            rs.getString("phrase"),
            rs.getString("status"),
            rs.getString("url")
    );

    public ResultsRepositoryImpl() {
        this.jdbcTemplate = new JdbcTemplate();
        try {
            final InitialContext context = new InitialContext();
            this.ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
            initialize();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        String sql = "CREATE TABLE IF NOT EXISTS results (id INTEGER PRIMARY KEY AUTOINCREMENT, phrase TEXT NOT NULL, status TEXT, url TEXT);";
        try {
            jdbcTemplate.update(ds, sql, statement -> statement);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e);
        }
    }

    @Override
    public Optional<ResultModel> getById(int id) {
        try {
            return jdbcTemplate.queryForObject(ds, "SELECT * FROM results WHERE id = ?;", stmt -> {
                stmt.setInt(1, id);
                return stmt;
            }, mapper);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public int makeNewQuery(String phrase) {
        try {
            final Integer id = jdbcTemplate.<Integer>updateForId(ds, "INSERT INTO results(phrase, status) VALUES (?, 'Queued');", stmt -> {
                stmt.setString(1, phrase);
                return stmt;
            });
            return id;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public List<ResultModel> getAll() {
        try {
            return jdbcTemplate.queryForList(ds, "SELECT * FROM results;", mapper);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
