package com.github.constraint.maintainer.mssql;


import com.github.constraint.maintainer.ConstraintDefinition;

import static java.util.Arrays.stream;

/**
 * @author Stanislav Dobrovolschi
 */
public class MsSqlForeignKeyDefinition implements ConstraintDefinition {

    private final String schemaName;
    private final String foreignKeyName;
    private final String tableName;
    private final String columnName;
    private final String referenceTableName;
    private final String referenceColumnName;
    private final ReferentialActionRule deleteReferentialAction;
    private final ReferentialActionRule updateReferentialAction;

    MsSqlForeignKeyDefinition(String schemaName, String foreignKeyName, String tableName, String columnName, String referenceTableName,
                              String referenceColumnName, ReferentialActionRule deleteReferentialAction, ReferentialActionRule updateReferentialAction) {

        this.schemaName = schemaName;
        this.foreignKeyName = foreignKeyName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.referenceTableName = referenceTableName;
        this.referenceColumnName = referenceColumnName;
        this.deleteReferentialAction = deleteReferentialAction;
        this.updateReferentialAction = updateReferentialAction;
    }

    @Override
    public String toDisableQuery() {
        return new StringBuilder("alter table ")
                .append(MsSqlDatabase.qualified(schemaName, tableName))
                .append(" drop constraint ")
                .append(MsSqlDatabase.bracketed(foreignKeyName))
                .toString();
    }

    @Override
    public String toEnableQuery() {
        return new StringBuilder("alter table ")
                .append(MsSqlDatabase.qualified(schemaName, tableName))
                .append(" add constraint ")
                .append(MsSqlDatabase.bracketed(foreignKeyName))
                .append(" foreign key")
                .append(" (")
                .append(MsSqlDatabase.bracketed(columnName))
                .append(")")
                .append("references ")
                .append(MsSqlDatabase.qualified(schemaName, referenceTableName))
                .append(" (")
                .append(MsSqlDatabase.bracketed(referenceColumnName))
                .append(")")
                .append(" on delete ")
                .append(deleteReferentialAction.toDbKey())
                .append(" on update ")
                .append(updateReferentialAction.toDbKey())
                .toString();
    }

    enum ReferentialActionRule {

        NO_ACTION("no action"),
        CASCADE("cascade"),
        SET_NULL("set null"),
        SET_DEFAULT("set default");

        private final String dbKey;

        ReferentialActionRule(String dbKey) {
            this.dbKey = dbKey;
        }

        public String toDbKey() {
            return dbKey;
        }

        public static ReferentialActionRule valueOf(int ordinal) {
            return stream(values())
                    .filter(e -> e.ordinal() == ordinal)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No enum constant " + ReferentialActionRule.class.getCanonicalName() + " with ordinal" + ordinal));
        }
    }
}
