package longa.domains.sql.models;

public class SQLScriptGenUtil {
    public static String toValueString(Object value)
    {
        if(value instanceof String)
        {
            return "'"+ value.toString() +"'";
        }
        return  value.toString();
    }
}
