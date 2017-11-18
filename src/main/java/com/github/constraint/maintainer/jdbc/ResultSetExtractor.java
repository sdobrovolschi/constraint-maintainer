package com.github.constraint.maintainer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Stanislav Dobrovolschi
 */
public interface ResultSetExtractor<T> {

    T extractData(ResultSet rs) throws SQLException;
}
