package longa.domains.sql;

import longa.domains.sql.models.JoinKind;
import longa.domains.sql.models.JoinModel;
import longa.langtags.LgaChain;
import longa.langtags.LgaMethodReqKind;
import longa.langtags.LgaNode;

@LgaChain(domain = "languages.sql")
public class join_chain extends JoinModel
{
    select_chain select;
    public join_chain(select_chain select)
    {
        this.select = select;
    }

    @LgaNode(prepCount = 1)
    public join_chain __start(JoinKind kind, String tableName, String alias) {
        join_chain newJoin = new join_chain(this.select);
        newJoin.kind = kind;
        newJoin.tableName = tableName;
        newJoin.tableAlias = alias;
        select.joins.add(newJoin );
        return newJoin;
    }

    @LgaNode( once = LgaMethodReqKind.mustOnce )
    public join_chain on(String on) {
        this.on = on;
        return this;
    }

    @LgaNode()
    public JoinModel __end() {
        select.joins.add(this);
        return this;
    }
}