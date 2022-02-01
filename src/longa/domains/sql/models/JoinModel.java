package longa.domains.sql.models;

public class JoinModel {
    public JoinKind kind;
    public String tableName;
    public String tableAlias;

    public String on;

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(kind.name());
        buf.append(" JOIN ");
        buf.append(tableName);
        if (tableAlias!=null)
            buf.append(" "+tableAlias);
        buf.append(" ON "+on);
        return buf.toString();
    }
}
