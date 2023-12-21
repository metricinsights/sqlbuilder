package com.healthmarketscience.sqlbuilder.custom.sqlserver;

import com.healthmarketscience.sqlbuilder.ColumnObject;
import com.healthmarketscience.sqlbuilder.CreateIndexQuery;
import com.healthmarketscience.sqlbuilder.dbspec.Column;
import com.healthmarketscience.sqlbuilder.dbspec.Index;

import java.util.ArrayList;
import java.util.List;

public class MsSqlCreateIndexQuery extends CreateIndexQuery {
    public MsSqlCreateIndexQuery(final Index index, final Integer indexLen) {
        super(index, indexLen);
    }

    @Override
    protected String generateIndexColumnsVal() {
        List<String> regularColumns = new ArrayList<>();
        List<String> maxColumns = new ArrayList<>();

        _columns.forEach(obj -> {
            if (obj instanceof ColumnObject columnObject) {
                Column col = columnObject.getColumn();

                if ("NVARCHAR(MAX)".equalsIgnoreCase(col.getTypeNameSQL())) {
                    maxColumns.add(col.getColumnNameSQL());
                } else {
                    regularColumns.add(col.getColumnNameSQL());
                }
            } else {
                throw new RuntimeException("Couldn't parse columns.");
            }
        });

        StringBuilder columnsString = new StringBuilder("(");
        columnsString.append(String.join(", ", regularColumns))
                .append(")");


        if (!maxColumns.isEmpty()) {
            columnsString.append(" INCLUDE (")
                    .append(String.join(", ", maxColumns))
                    .append(")");
        }

        return columnsString.toString();
    }
}
