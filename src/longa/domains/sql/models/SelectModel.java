package longa.domains.sql.models;

import java.util.ArrayList;

public class SelectModel {
    public String fields;
    public String tableName;
    public String tableAlias;

    public ArrayList<JoinModel> joins = new ArrayList<>();

    public String where;

    public String order_by_fields;

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("SELECT "+fields);
        buf.append(" FROM "+tableName);
        if (tableAlias!=null)
            buf.append(" "+tableAlias);
        if(joins.size()>0)
        {
            for(JoinModel joinModel:joins) {
                buf.append(" ");
                buf.append(joinModel.toString());
                buf.append(" ");
            }
        }
        if(where!=null)
            buf.append(" WHERE "+ where);
        if(where!=null)
            buf.append(" ORDER BY "+order_by_fields);
        return buf.toString();
    }
}
