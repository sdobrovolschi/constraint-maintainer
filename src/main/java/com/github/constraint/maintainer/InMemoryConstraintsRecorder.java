package com.github.constraint.maintainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Stanislav Dobrovolschi
 */
public class InMemoryConstraintsRecorder implements ConstraintsRecorder {

    private final Map<ConstraintType, List<String>> sqls = new TreeMap<>();

    @Override
    public void record(ConstraintType constraintType, String sql) {
        List<String> sqls = this.sqls.computeIfAbsent(constraintType, k -> new ArrayList<>());
        sqls.add(sql);
    }

    @Override
    public List<String> getSqls() {
        List<String> result = new ArrayList<>();
        sqls.values().forEach(result::addAll);
        return result;
    }
}
