package com.github.constraint.maintainer.mssql;

import com.github.constraint.maintainer.ConstraintDefinition;

/**
 * @author Stanislav Dobrovolschi
 */
public class MsSqlNotNullConstraintDefinition implements ConstraintDefinition {

    private final String schemaName;
    private final String tableName;
    private final String columnName;
    private final String dataType;
    private final Integer maxLength;
    private final Integer precision;
    private Integer scale;

    MsSqlNotNullConstraintDefinition(String schemaName, String tableName, String columnName, String dataType, Integer maxLength, Integer precision, Integer scale) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.dataType = dataType;
        this.maxLength = maxLength;
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    public String toDisableQuery() {
        return new StringBuilder("alter table ")
                .append(MsSqlDatabase.qualified(schemaName, tableName))
                .append(" alter column ")
                .append(MsSqlDatabase.bracketed(columnName))
                .append(getDataType(dataType, maxLength, precision, scale))
                .toString();
    }

    private String getDataType(String dataType, Integer maxLength, Integer precision, Integer scale) {
        String result = dataType;
        if ("NUMERIC".equals(dataType) || "DECIMAL".equals(dataType)) {
            result += "(" + precision + ", " + scale + ")";
        } else if (dataType.contains("CHAR") || dataType.contains("BINARY")) {
            if (dataType.equals("NCHAR") || dataType.equals("NVARCHAR")) {
                maxLength = maxLength / 2;
            }
            result += "(" + (maxLength == -1 ? "MAX" : maxLength) + ")";
        }
        return result;
    }

    @Override
    public String toEnableQuery() {
        return new StringBuilder("alter table ")
                .append(MsSqlDatabase.qualified(schemaName, tableName))
                .append(" alter column ")
                .append(MsSqlDatabase.bracketed(columnName))
                .append(" " + getDataType(dataType, maxLength, precision, scale))
                .append(" not null")
                .toString();
    }
}
