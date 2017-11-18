package com.github.constraint.maintainer.jdbc;

import com.github.constraint.maintainer.DatabaseException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Stanislav Dobrovolschi
 */
public class JdbcTemplate {

    private static final int BATCH_SIZE = 40;

    private final DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        List<T> result = new ArrayList<>();

        try (Connection con = dataSource.getConnection()) {
            try (Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    result.add(rowMapper.mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return result;
    }

    public <T> T query(String sql, ResultSetExtractor<T> resultSetExtractor) {
        try (Connection con = dataSource.getConnection()) {
            try (Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                return resultSetExtractor.extractData(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void execute(Collection<String> queries) {
        Connection con = null;

        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);

            try (Statement stmt = con.createStatement()) {
                int count = 0;
                for (String query : queries) {
                    stmt.addBatch(query);

                    if (++count % BATCH_SIZE == 0) {
                        stmt.executeBatch();
                    }
                }

                stmt.executeBatch();
            }
            con.setAutoCommit(true);
        } catch (SQLException e) {
            rollback(con);
            throw new DatabaseException(e);
        }
    }

    private void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }
    }
}
