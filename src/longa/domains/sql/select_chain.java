package longa.domains.sql;

import com.sun.rowset.CachedRowSetImpl;
import longa.domains.sql.models.ColumnModel;
import longa.domains.sql.models.SelectModel;
import longa.domains.sql.tools.MySQLHelper;
import longa.lang.List;
import longa.langtags.LgaChain;
import longa.langtags.LgaMethodReqKind;
import longa.langtags.LgaNode;

@LgaChain( domain = "languages.sql" )
public class select_chain extends SelectModel {

    public select_chain __start(String fields) {
        this.fields = fields;
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce )
    public select_chain from(String tableName) {
        this.tableName = tableName;
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce )
    public select_chain from(String tableName, String alias) {
        this.tableName = tableName;
        this.tableAlias = alias;
        return this;
    }

    @LgaNode()
    public join_chain join = new join_chain(this);

    @LgaNode()
    public select_chain where(String wheres) {
        this.where = wheres;
        return this;
    }

    @LgaNode()
    public select_chain orderby(String order_by_fields) {
        this.order_by_fields = order_by_fields;
        return this;
    }

    public CachedRowSetImpl __end() {
        String sql = this.toString();
        System.out.println(sql);
        CachedRowSetImpl cachedRowSet = MySQLHelper.executeQuery(sql);
        return cachedRowSet;
    }
}

