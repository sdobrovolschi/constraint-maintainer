package com.github.constraint.maintainer.integration.spring;

import com.github.constraint.maintainer.DisablingMode;
import com.github.constraint.maintainer.Database;
import com.github.constraint.maintainer.mssql.MsSqlDatabase;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;

/**
 * @author Stanislav Dobrovolschi
 */
public class SpringConstraintMaintainer implements InitializingBean, DisposableBean {

    private final DataSource dataSource;
    private String schemaName;
    private DisablingMode disablingMode;

    private Database database;

    public SpringConstraintMaintainer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;

    }

    public void setDisablingMode(DisablingMode disablingMode) {
        this.disablingMode = disablingMode;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        database = new MsSqlDatabase(dataSource);
        database.setSchemaName(schemaName);
        disablingMode.disableConstraints(database);
    }

    @Override
    public void destroy() throws Exception {
        database.enableConstraints();
    }
}
