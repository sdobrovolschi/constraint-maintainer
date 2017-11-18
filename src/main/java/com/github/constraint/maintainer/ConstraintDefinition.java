package com.github.constraint.maintainer;

/**
 * @author Stanislav Dobrovolschi
 */
public interface ConstraintDefinition {

    String toDisableQuery();

    String toEnableQuery();
}
