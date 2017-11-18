package com.github.constraint.maintainer;

/**
 * @author Stanislav Dobrovolschi
 */
public abstract class Database {

    protected final ConstraintsRecorder constraintsRecorder;

    protected String schemaName;

    public Database(ConstraintsRecorder constraintsRecorder) {
        this.constraintsRecorder = constraintsRecorder;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public ConstraintsDisabler createConstraintsDisabler() {
        return new DefaultConstraintsDisabler(this);
    }

    public ConstraintsEnabler createConstraintsEnabler() {
        return new DefaultConstraintEnabler(this);
    }

    public abstract void disableConstraints();

    public abstract void disableReferentialConstraints();

    public abstract void disableValueConstraints();

    public abstract void enableConstraints();
}
