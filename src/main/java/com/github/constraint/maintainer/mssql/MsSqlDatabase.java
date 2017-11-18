package com.github.constraint.maintainer.mssql;

import com.github.constraint.maintainer.ConstraintDefinition;
import com.github.constraint.maintainer.ConstraintsRecorder.ConstraintType;
import com.github.constraint.maintainer.Database;
import com.github.constraint.maintainer.InMemoryConstraintsRecorder;
import com.github.constraint.maintainer.jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Stanislav Dobrovolschi
 */
public class MsSqlDatabase extends Database {

    private static final Logger logger = LoggerFactory.getLogger(MsSqlDatabase.class);

    private final JdbcTemplate jdbcTemplate;

    public MsSqlDatabase(DataSource dataSource) {
        super(new InMemoryConstraintsRecorder());
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void disableConstraints() {
        disableReferentialConstraints();
        disableValueConstraints();
    }

    @Override
    public void disableReferentialConstraints() {
        disableForeignKeys();
    }

    @Override
    public void disableValueConstraints() {
        disableUniqueKeys();
        disableNotNullConstraints();
    }

    public void disableForeignKeys() {
        String query = "select fk.name as foreign_key_name, t.name as table_name, c.name as column_name, rt.name as reference_table_name, rc.name as reference_column_name,"
                + "     fk.delete_referential_action, fk.update_referential_action"
                + " from sys.foreign_keys fk"
                + "     join sys.foreign_key_columns fkc on fkc.constraint_object_id = fk.object_id"
                + "     join sys.tables t on t.object_id = fk.parent_object_id"
                + "     join sys.columns c on c.object_id = fkc.parent_object_id and c.column_id = fkc.constraint_column_id"
                + "     join sys.tables rt on rt.object_id = fk.referenced_object_id"
                + "     join sys.columns rc on rc.object_id = fkc.referenced_object_id and rc.column_id = fkc.referenced_column_id"
                + " where fk.schema_id = schema_id('" + schemaName + "')";

        List<MsSqlForeignKeyDefinition> constraintDefinitions = jdbcTemplate.query(query, new MsSqlForeignKeyRowMapper(schemaName));

        constraintDefinitions.stream()
                .map(ConstraintDefinition::toEnableQuery)
                .forEach(e -> constraintsRecorder.record(ConstraintType.NOT_NULL, e));

        logger.info("Disabling {} of foreign keys", constraintDefinitions.size());

        jdbcTemplate.execute(constraintDefinitions.stream()
                .map(ConstraintDefinition::toDisableQuery)
                .collect(toList()));
    }

    public void disableUniqueKeys() {
        String query = "select kc.name as constraint_name, i.type_desc, t.name as table_name, col_name(ic.object_id, ic.column_id) as column_name," +
                "       ic.is_descending_key"
                + " from sys.key_constraints kc"
                + "     join sys.index_columns ic on ic.object_id = kc.parent_object_id and ic.index_id = kc.unique_index_id"
                + "     join sys.indexes i on i.object_id = ic.object_id and i.index_id = ic.index_id"
                + "     join sys.tables t ON t.object_id = kc.parent_object_id"
                + " where kc.type = 'UQ'"
                + "     and kc.schema_id = schema_id('" + schemaName + "')";

        List<MsSqlUniqueConstraintDefinition> constraintDefinitions = jdbcTemplate.query(query, new MsSqlUniqueConstraintResultSetExtractor(schemaName));

        constraintDefinitions.stream()
                .map(ConstraintDefinition::toEnableQuery)
                .forEach(e -> constraintsRecorder.record(ConstraintType.UNIQUE, e));

        logger.info("Disabling {} of unique constraints", constraintDefinitions.size());

        jdbcTemplate.execute(constraintDefinitions.stream()
                .map(ConstraintDefinition::toDisableQuery)
                .collect(toList()));
    }

    public void disableNotNullConstraints() {
        String query = "select t.name as table_name, c.name as column_name, upper(type_name(c.user_type_id)) as data_type, c.max_length, c.precision, c.scale"
                + " from sys.columns c"
                + "     join sys.tables t on t.object_id = c.object_id"
                + "     left join ("
                + "         select i.object_id, ic.column_id"
                + "         from sys.key_constraints c"
                + "             join sys.indexes i on i.object_id = c.parent_object_id and i.index_id = c.unique_index_id"
                + "             join sys.index_columns ic on ic.object_id = i.object_id and ic.index_id = i.index_id"
                + "         ) pk on pk.object_id = c.object_id and pk.column_id = c.column_id"
                + " where c.is_nullable = 0"
                + "     and c.is_identity = 0"
                + "     and c.is_computed = 0"
                + "     and t.schema_id = schema_id('" + schemaName + "')"
                + "     and pk.object_id is null";

        List<MsSqlNotNullConstraintDefinition> constraintDefinitions = jdbcTemplate.query(query, new MsSqlNotNullConstraintRowMapper(schemaName));

        constraintDefinitions.stream()
                .map(ConstraintDefinition::toEnableQuery)
                .forEach(e -> constraintsRecorder.record(ConstraintType.NOT_NULL, e));

        logger.info("Disabling {} of not null constraints", constraintDefinitions.size());

        jdbcTemplate.execute(constraintDefinitions.stream()
                .map(ConstraintDefinition::toDisableQuery)
                .collect(toList()));
    }

    public static String bracketed(String databaseObjectName) {
        return "[" + databaseObjectName + "]";
    }

    public static String qualified(String schemaName, String databaseObjectName) {
        return bracketed(schemaName) + "." + bracketed(databaseObjectName);
    }

    @Override
    public void enableConstraints() {
        logger.info("Enabling {} constraints", constraintsRecorder.getSqls().size());
        jdbcTemplate.execute(constraintsRecorder.getSqls());
    }
}
