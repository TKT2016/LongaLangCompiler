package longa.domains.sql;

import com.sun.rowset.CachedRowSetImpl;
import longa.domains.sql.models.InsertModel;
import longa.domains.sql.models.SelectModel;
import longa.domains.sql.tools.MySQLHelper;
import longa.lang.List;
import longa.langtags.LgaChain;
import longa.langtags.LgaMethodReqKind;
import longa.langtags.LgaNode;

@LgaChain( domain = "languages.sql" )
public class insert_chain
{
    InsertModel insertModel;

    public insert_chain __start( ) {
        insertModel = new InsertModel();
        return this;
    }

    @LgaNode(once =  LgaMethodReqKind.mustOnce)
    public insert_chain into(String tableName) {
        insertModel.tableName = tableName;
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce )
    public insert_chain values(List<Object> values ) {
        insertModel.values = values;
        return this;
    }

    public int __end() {
        String sql = insertModel.toString();
        return MySQLHelper.executeUpdate(sql);
    }
}

