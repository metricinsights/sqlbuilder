package com.healthmarketscience.sqlbuilder.custom.sqlserver;

import com.healthmarketscience.common.util.AppendableExt;
import com.healthmarketscience.sqlbuilder.SqlObject;
import com.healthmarketscience.sqlbuilder.SqlObjectList;
import com.healthmarketscience.sqlbuilder.custom.BaseModifyColumnAction;

import java.io.IOException;

public class MsSqlModifyColumnAction extends BaseModifyColumnAction {

    public MsSqlModifyColumnAction(Object column) {
        super(column);
    }

    @Override
    public void appendTo(AppendableExt app) throws IOException {
        SqlObjectList<SqlObject> columns = getColumnsForModification();

        if (columns != null && !columns.isEmpty()) {
            super.appendTo(app, "ALTER COLUMN");
        } else {
            app.append(" ALTER COLUMN ").append(column);
        }
    }
}
