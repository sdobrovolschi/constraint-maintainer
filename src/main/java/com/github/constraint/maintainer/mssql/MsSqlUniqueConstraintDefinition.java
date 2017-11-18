package com.github.constraint.maintainer.mssql;

import com.github.constraint.maintainer.ConstraintDefinition;

import java.util.List;

/**
 * @author Stanislav Dobrovolschi
 */
public class MsSqlUniqueConstraintDefinition implements ConstraintDefinition {

    private final String schemaName;
    private final String uniqueConstraintName;
    private final ConstraintType constraintType;
    private final String tableName;
    private final List<ColumnDefinition> columnDefinitions;

    public MsSqlUniqueConstraintDefinition(String schemaName, String uniqueConstraintName, ConstraintType constraintType, String tableName, List<ColumnDefinition> columnDefinitions) {
        this.schemaName = schemaName;
        this.uniqueConstraintName = uniqueConstraintName;
        this.constraintType = constraintType;
        this.tableName = tableName;
        this.columnDefinitions = columnDefinitions;
    }

    @Override
    public String toDisableQuery() {
        return new StringBuilder("alter table ")
                .append(MsSqlDatabase.qualified(schemaName, tableName))
                .append(" drop constraint ")
                .append(MsSqlDatabase.bracketed(uniqueConstraintName))
                .toString();
    }

    @Override
    public String toEnableQuery() {
        StringBuilder builder = new StringBuilder("alter table ")
                .append(MsSqlDatabase.qualified(schemaName, tableName))
                .append(" add constraint ")
                .append(MsSqlDatabase.bracketed(uniqueConstraintName))
                .append(" unique ")
                .append(constraintType.name())
                .append(" (");

        columnDefinitions.forEach(e -> builder.append(e.toString()));

        builder.append(")");

        return builder.toString();
    }

    enum ConstraintType {

        CLUSTERED,
        NONCLUSTERED
    }

    static class ColumnDefinition {

        private final String columnName;
        private final boolean isDescending;

        public ColumnDefinition(String columnName, boolean isDescending) {
            this.columnName = columnName;
            this.isDescending = isDescending;
        }

        @Override
        public String toString() {
            return MsSqlDatabase.bracketed(columnName) + (isDescending ? " desc" : " asc");
        }
    }
}
