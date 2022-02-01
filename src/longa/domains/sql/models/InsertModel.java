package longa.domains.sql.models;

import longa.lang.List;
import longac.utils.Debuger;

import java.util.ArrayList;

public class InsertModel {
    public String tableName;

    public List<Object> values;

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("INSERT INTO "+tableName);
        buf.append(" VALUES ( " );

        for( int i = 0;i<values.size();i++) {
            Object item = values.get(i);

            buf.append(SQLScriptGenUtil.toValueString(item));
            if(i<values.size()-1)
                buf.append(" , ");
        }
        buf.append(" ) " );
        return buf.toString();
    }
}
