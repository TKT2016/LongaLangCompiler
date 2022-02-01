package longa.domains.sql;

import com.sun.rowset.CachedRowSetImpl;
import longa.domains.sql.models.ColumnModel;
import longa.domains.sql.models.CreateTableModel;
import longa.domains.sql.tools.MySQLHelper;
import longa.lang.List;
import longa.langtags.LgaChain;
import longa.langtags.LgaMethodReqKind;
import longa.langtags.LgaNode;
@LgaChain( domain = "languages.sql" )
public class create_chain {
    CreateTableModel createTableModel;

    public create_chain __start( ) {
        createTableModel = new CreateTableModel();
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce)
    public create_chain table(String tableName, List<ColumnModel> columns) {
        createTableModel.tableName = tableName;
        createTableModel.columns = columns;
        return this;
    }

    @LgaNode()
    public create_chain comment(String comment) {
        createTableModel.comment = comment;
        return this;
    }

    public int __end() {
        String sql = createTableModel.toString();
        //System.out.println(sql);
        return MySQLHelper.executeUpdate(sql);
    }
}
