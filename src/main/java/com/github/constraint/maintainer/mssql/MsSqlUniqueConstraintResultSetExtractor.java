package com.github.constraint.maintainer.mssql;

import com.github.constraint.maintainer.jdbc.ResultSetExtractor;
import com.github.constraint.maintainer.mssql.MsSqlUniqueConstraintDefinition.ColumnDefinition;
import com.github.constraint.maintainer.mssql.MsSqlUniqueConstraintDefinition.ConstraintType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Stanislav Dobrovolschi
 */
public class MsSqlUniqueConstraintResultSetExtractor implements ResultSetExtractor<List<MsSqlUniqueConstraintDefinition>> {

    private final String schemaName;

    private final Map<String, List<ColumnDefinition>> columns = new HashMap<>();
    private final Map<String, MsSqlUniqueConstraintDefinition> map = new HashMap<>();

    public MsSqlUniqueConstraintResultSetExtractor(String schemaName) {
        this.schemaName = schemaName;
    }

    @Override
    public List<MsSqlUniqueConstraintDefinition> extractData(ResultSet rs) throws SQLException {
        while (rs.next()) {
            String constraintName = rs.getString("constraint_name");

            List<ColumnDefinition> columnNames = columns.computeIfAbsent(constraintName, k -> new ArrayList<>());
            columnNames.add(new ColumnDefinition(rs.getString("column_name"), rs.getBoolean("is_descending_key")));

            map.put(constraintName, new MsSqlUniqueConstraintDefinition(
                            schemaName,
                            constraintName,
                            ConstraintType.valueOf(rs.getString("type_desc")),
                            rs.getString("table_name"),
                            columnNames
                    )
            );
        }

        return new ArrayList<>(map.values());
    }
}
