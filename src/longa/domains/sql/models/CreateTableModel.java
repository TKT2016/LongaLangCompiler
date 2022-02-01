package longa.domains.sql.models;

import longa.lang.List;

public class CreateTableModel {
    public String tableName;

    public List<ColumnModel> columns;

    public String comment;

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(" CREATE TABLE ");
        builder.append(" `");
        builder.append(tableName);
        builder.append("` ");
        builder.append(" ( ");

        for(int i=0;i<columns.size();i++)
        {
            String colsql = columns.get(i).toString();
            builder.append(colsql);

            if(i!=columns.size()-1)
                builder.append(" , ");
        }

        builder.append(" ) ");

        if(comment!=null)
        {
            builder.append(" COMMENT = ");
            builder.append(" '");
            builder.append(comment);
            builder.append("' ");
        }
        return builder.toString();
    }
}
