package com.healthmarketscience.sqlbuilder.custom;

import com.healthmarketscience.common.util.AppendableExt;
import com.healthmarketscience.sqlbuilder.Converter;
import com.healthmarketscience.sqlbuilder.SqlObject;
import com.healthmarketscience.sqlbuilder.SqlObjectList;
import com.healthmarketscience.sqlbuilder.ValidationContext;

import java.io.IOException;

public class BaseModifyColumnAction extends ColumnsModificationAction {

    protected SqlObject column;

    public BaseModifyColumnAction(Object column) {
        this.column = Converter.toCustomTypedColumnSqlObject(column);
    }

    @Override
    protected void collectSchemaObjects(ValidationContext vContext) {
        SqlObject.collectSchemaObjects(column, vContext);
    }

    @Override
    public void appendTo(AppendableExt app) throws IOException {
        SqlObjectList<SqlObject> columns = getColumnsForModification();

        if (columns != null && !columns.isEmpty()) {
            super.appendTo(app, "MODIFY COLUMN");
        } else {
            app.append(" MODIFY COLUMN ").append(column);
        }
    }
}
