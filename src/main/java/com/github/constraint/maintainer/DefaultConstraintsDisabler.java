package com.github.constraint.maintainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stanislav Dobrovolschi
 */
public class DefaultConstraintsDisabler implements ConstraintsDisabler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConstraintsDisabler.class);

    private final Database database;

    public DefaultConstraintsDisabler(Database database) {
        this.database = database;
    }

    @Override
    public void disableConstraints() {
        database.disableConstraints();
    }

    @Override
    public void disableReferentialConstraints() {
        logger.info("Disabling referential constraints in database schema");
        database.disableReferentialConstraints();
    }

    @Override
    public void disableValueConstraints() {
        logger.info("Disabling value constraints in database schema");
        database.disableValueConstraints();
    }
}
