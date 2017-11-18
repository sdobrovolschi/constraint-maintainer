package com.github.constraint.maintainer;

import java.util.List;

/**
 * @author Stanislav Dobrovolschi
 */
public interface ConstraintsRecorder {

    void record(ConstraintType constraintType, String sql);

    List<String> getSqls();

    enum ConstraintType {

        NOT_NULL,
        UNIQUE,
        FOREIGN_KEY

    }
}
