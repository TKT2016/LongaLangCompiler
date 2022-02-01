package longa.domains.sql.models;

public class DropTableModel {
    public String tableName;
    public boolean ifExists;

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DROP TABLE ");
        if(ifExists)
        {
            builder.append(" IF EXISTS ");
        }
        builder.append(tableName);
        return builder.toString();
    }
}
