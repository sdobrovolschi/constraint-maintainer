package com.github.constraint.maintainer.junit;

import com.github.constraint.maintainer.ConstraintMaintainer;
import com.github.constraint.maintainer.Database;
import com.github.constraint.maintainer.DisablingMode;
import com.github.constraint.maintainer.mssql.MsSqlDatabase;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.sql.DataSource;
import java.util.function.Supplier;

/**
 * @author Stanislav Dobrovolschi
 */
public class ConstraintMaintainerRule implements TestRule {

    private final DataSource dataSource;

    private String schemaName;

    private ConstraintMaintainerRule(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static ConstraintMaintainerRule instance(DataSource dataSource) {
        return new ConstraintMaintainerRule(dataSource);
    }

    public static ConstraintMaintainerRule instance(Supplier<DataSource> dataSourceSupplier) {
        return new ConstraintMaintainerRule(dataSourceSupplier.get());
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base, description);
    }

    private Statement statement(final Statement base, final Description description) {
        Database database = new MsSqlDatabase(dataSource);
        database.setSchemaName(schemaName);

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before(description, database);
                try {
                    base.evaluate();
                } finally {
                    after(description, database);
                }
            }
        };
    }

    private void before(final Description description, final Database database) throws Throwable {
        ConstraintMaintainer annotation = description.getAnnotation(ConstraintMaintainer.class);

        if (annotation != null) {
            DisablingMode mode = annotation.value();

            mode.disableConstraints(database);
        }
    }

    private void after(final Description description, final Database database) {
        database.enableConstraints();
    }
}
