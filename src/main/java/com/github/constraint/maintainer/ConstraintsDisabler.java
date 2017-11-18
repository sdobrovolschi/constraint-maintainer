package com.github.constraint.maintainer;

/**
 * @author Stanislav Dobrovolschi
 */
public interface ConstraintsDisabler { //TODO check if needed

    void disableConstraints();

    void disableReferentialConstraints();

    void disableValueConstraints();
}
