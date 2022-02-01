package longa.domains.sql;

import longa.domains.sql.models.ColumnModel;
import longa.domains.sql.models.DataTypeKind;
import longa.domains.sql.models.DropTableModel;
import longa.domains.sql.tools.MySQLHelper;
import longa.langtags.LgaChain;
import longa.langtags.LgaMethodReqKind;
import longa.langtags.LgaNode;
@LgaChain( domain = "languages.sql" )
public class drop_chain {
    DropTableModel dropTableModel;

    public drop_chain __start(  ) {
        dropTableModel = new DropTableModel();
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce)
    public drop_chain table(String tableName) {
        dropTableModel.tableName = tableName;
        return this;
    }

    @LgaNode()
    public drop_chain ifExists() {
        dropTableModel.ifExists = true;
        return this;
    }

    public int __end() {
        String sql = dropTableModel.toString();
        return MySQLHelper.executeUpdate(sql);
    }
}
