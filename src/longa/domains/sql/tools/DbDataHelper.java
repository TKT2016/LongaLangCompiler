package longa.domains.sql.tools;

import com.sun.rowset.CachedRowSetImpl;

import java.io.PrintStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DbDataHelper {

    public static void dump(PrintStream printStream, CachedRowSetImpl rs) throws SQLException
    {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        //String[] columns = rs.getMetaData().getcolumn.getMatchColumnNames();
        int colsize = resultSetMetaData.getColumnCount();
       // printStream.print(colsize);
        for(int i=0;i<colsize;i++)
        {
            String col = resultSetMetaData.getColumnName(i+1);
            printStream.print(col);
            printStream.print("\t");
        }
        printStream.println();

        while(rs.next()){
            for(int i=0;i<colsize;i++)
            {
                Object obj = rs.getObject(i+1);
                printStream.print(obj);
                printStream.print("\t");
            }
            printStream.println();
        }
    }
}
