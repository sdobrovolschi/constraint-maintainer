package com.github.constraint.maintainer.mssql;

import com.github.constraint.maintainer.jdbc.RowMapper;
import com.github.constraint.maintainer.mssql.MsSqlForeignKeyDefinition.ReferentialActionRule;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Stanislav Dobrovolschi
 */
public class MsSqlForeignKeyRowMapper implements RowMapper<MsSqlForeignKeyDefinition> {

    private final String schemaName;

    public MsSqlForeignKeyRowMapper(String schemaName) {
        this.schemaName = schemaName;
    }

    @Override
    public MsSqlForeignKeyDefinition mapRow(ResultSet rs) throws SQLException {
        return new MsSqlForeignKeyDefinition(
                schemaName,
                rs.getString("foreign_key_name"),
                rs.getString("table_name"),
                rs.getString("column_name"),
                rs.getString("reference_table_name"),
                rs.getString("reference_column_name"),
                ReferentialActionRule.valueOf(rs.getInt("delete_referential_action")),
                ReferentialActionRule.valueOf(rs.getInt("update_referential_action")));
    }
}
