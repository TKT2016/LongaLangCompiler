package longa.domains.sql;

import com.sun.rowset.CachedRowSetImpl;
import longa.domains.sql.models.ColumnModel;
import longa.domains.sql.models.DataTypeKind;
import longa.domains.sql.tools.MySQLHelper;
import longa.langtags.LgaChain;
import longa.langtags.LgaMethodReqKind;
import longa.langtags.LgaNode;
@LgaChain( domain = "languages.sql" )
public class column_chain {
    ColumnModel columnModel;

    public column_chain __start( String colName) {
        columnModel = new ColumnModel();
        columnModel.colName = colName;
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce)
    public column_chain dataType(DataTypeKind dataTypeKind ) {
        columnModel.dataTypeKind = dataTypeKind;
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce)
    public column_chain dataType(DataTypeKind dataTypeKind,int size) {
        columnModel.dataTypeKind = dataTypeKind;
        columnModel.size = size;
        return this;
    }

    @LgaNode()
    public column_chain NULL() {
        columnModel.allowNull = true;
        return this;
    }

    @LgaNode()
    public column_chain NotNULL() {
        columnModel.allowNull = false;
        return this;
    }

    @LgaNode()
    public column_chain comment(String comment) {
        columnModel.comment = comment;
        return this;
    }

    public ColumnModel __end() {
        return columnModel;
    }
}
