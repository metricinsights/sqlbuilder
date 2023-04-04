package com.healthmarketscience.sqlbuilder.custom.snowflake;

import com.healthmarketscience.common.util.AppendableExt;
import com.healthmarketscience.sqlbuilder.Converter;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.SqlObject;
import com.healthmarketscience.sqlbuilder.ValidationContext;
import com.healthmarketscience.sqlbuilder.ValidationException;
import com.healthmarketscience.sqlbuilder.ValueObject;
import com.healthmarketscience.sqlbuilder.Verifiable;
import com.healthmarketscience.sqlbuilder.custom.CustomSyntax;
import com.healthmarketscience.sqlbuilder.custom.HookType;
import com.healthmarketscience.sqlbuilder.custom.sqlserver.MsSqlLimitWithOffsetClause;

import java.io.IOException;

public class SnowflakeLimitWithOffsetClause extends CustomSyntax implements Verifiable<MsSqlLimitWithOffsetClause> {
    private SqlObject rowCount;
    private SqlObject offset;

    public SnowflakeLimitWithOffsetClause(Object rowCount) {
        this(null, rowCount);
    }

    public SnowflakeLimitWithOffsetClause(Object offset, Object rowCount) {
        this.offset = ((offset != null) ? Converter.toValueSqlObject(offset) : null);
        this.rowCount = Converter.toValueSqlObject(rowCount);
    }

    @Override
    public void apply(SelectQuery query) {
        query.addCustomization(SelectQuery.Hook.FOR_UPDATE, HookType.BEFORE, this);
    }

    @Override
    public void appendTo(AppendableExt appendableExt) throws IOException {
        if (offset == null) {
            offset = new ValueObject(0);
        }

        appendableExt.append(" LIMIT ")
                .append(rowCount)
                .append(" OFFSET ")
                .append(offset);
    }

    @Override
    protected void collectSchemaObjects(ValidationContext vContext) {

    }

    @Override
    public MsSqlLimitWithOffsetClause validate() throws ValidationException {
        return null;
    }

    @Override
    public void validate(ValidationContext vContext) throws ValidationException {

    }
}
