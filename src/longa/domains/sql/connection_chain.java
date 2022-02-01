package longa.domains.sql;

import longa.domains.sql.models.ConnectionModel;
import longa.domains.sql.models.SelectModel;
import longa.domains.sql.tools.MySQLHelper;
import longa.langtags.LgaChain;
import longa.langtags.LgaMethodReqKind;
import longa.langtags.LgaNode;
@LgaChain( domain = "languages.sql" )
public class connection_chain {
    ConnectionModel connectionModel = new ConnectionModel();

    @LgaNode(once = LgaMethodReqKind.mustOnce)
    public connection_chain __start(String host) {
        connectionModel.host = host;
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce)
    public connection_chain port(int port) {
        connectionModel.port = port;
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce)
    public connection_chain use(String database) {
        connectionModel.database = database;
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce)
    public connection_chain user(String user) {
        connectionModel.user = user;
        return this;
    }

    @LgaNode(once = LgaMethodReqKind.mustOnce)
    public connection_chain password(String password) {
        connectionModel.password = password;
        return this;
    }

    public void __end() {
        MySQLHelper.connection = connectionModel;
        //System.out.println(MySQLHelper.connection.toUrl());
    }
}
