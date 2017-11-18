package com.github.constraint.maintainer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Stanislav Dobrovolschi
 */
public interface RowMapper<T> {

    T mapRow(ResultSet rs) throws SQLException;
}
