package com.healthmarketscience.sqlbuilder.custom.mysql;

import com.healthmarketscience.sqlbuilder.ColumnObject;
import com.healthmarketscience.sqlbuilder.CreateIndexQuery;
import com.healthmarketscience.sqlbuilder.dbspec.Column;
import com.healthmarketscience.sqlbuilder.dbspec.Index;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MySqlCreateIndexQuery extends CreateIndexQuery {
    public MySqlCreateIndexQuery(final Index index, final Integer indexLen) {
        super(index, indexLen);
    }

    @Override
    protected String generateIndexColumnsVal() {
        return StreamSupport.stream(_columns.spliterator(), false)
                .map(obj -> {
                    if (obj instanceof ColumnObject columnObject) {
                        Column col = columnObject.getColumn();

                        if ("TEXT".equalsIgnoreCase(col.getTypeNameSQL()) ||
                                "VARCHAR".equalsIgnoreCase(col.getTypeNameSQL())) {
                            return "%s(%d)".formatted(col.getColumnNameSQL(), indexLen);
                        }

                        return col.getColumnNameSQL();
                    }
                    throw new RuntimeException("Couldn't parse columns.");
                })
                .collect(Collectors.joining(",", "(", ")"));
    }
}
