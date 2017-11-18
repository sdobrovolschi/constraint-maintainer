package com.github.constraint.maintainer;

/**
 * @author Stanislav Dobrovolschi
 */
public class DefaultConstraintEnabler implements ConstraintsEnabler {

    private final Database database;

    public DefaultConstraintEnabler(Database database) {
        this.database = database;
    }

    @Override
    public void enableConstraints() {
        database.enableConstraints();
    }
}
