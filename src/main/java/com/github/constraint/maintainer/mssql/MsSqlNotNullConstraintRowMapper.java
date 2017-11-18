package com.github.constraint.maintainer.mssql;

import com.github.constraint.maintainer.jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Stanislav Dobrovolschi
 */
public class MsSqlNotNullConstraintRowMapper implements RowMapper<MsSqlNotNullConstraintDefinition> {

    private final String schemaName;

    public MsSqlNotNullConstraintRowMapper(String schemaName) {
        this.schemaName = schemaName;
    }

    @Override
    public MsSqlNotNullConstraintDefinition mapRow(ResultSet rs) throws SQLException {
        return new MsSqlNotNullConstraintDefinition(
                schemaName,
                rs.getString("table_name"),
                rs.getString("column_name"),
                rs.getString("data_type"),
                rs.getInt("max_length"),
                rs.getInt("precision"),
                rs.getInt("scale")
        );
    }
}
