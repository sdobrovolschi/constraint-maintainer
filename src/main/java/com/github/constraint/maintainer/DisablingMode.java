package com.github.constraint.maintainer;

/**
 * @author Stanislav Dobrovolschi
 */
public enum DisablingMode {

    ALL {
        @Override
        public void disableConstraints(Database database) {
            database.disableConstraints();
        }
    },
    REFERENTIAL {
        @Override
        public void disableConstraints(Database database) {
            database.disableReferentialConstraints();
        }
    },
    VALIDATION {
        @Override
        public void disableConstraints(Database database) {
            database.disableValueConstraints();
        }
    };

    public abstract void disableConstraints(Database database);
}
