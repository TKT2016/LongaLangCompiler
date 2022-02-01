package longa.domains.sql.models;

public class ColumnModel {
    public String colName;
    public DataTypeKind dataTypeKind;
    public int size=-1;
    public boolean allowNull=true;
    public String comment;

    public String toString()
    {
        if(dataTypeKind== DataTypeKind.integer)
            size=0;

        StringBuilder builder = new StringBuilder();
        builder.append(" `");
        builder.append(colName);
        builder.append("` ");
        builder.append(dataTypeKind.name());
        if(size!=-1)
        {
            builder.append(" (");
            builder.append(size);
            builder.append(") ");
        }

        if(allowNull)
        {
            builder.append(" NULL ");
        }
        else
        {
            builder.append(" NOT NULL ");
        }

        if(comment!=null)
        {
            builder.append(" COMMENT ");
            builder.append(" '");
            builder.append(comment);
            builder.append("' ");
        }
        return builder.toString();
    }
}
