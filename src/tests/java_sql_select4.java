package tests;

import longa.domains.sql.insert_chain;
import longa.lang.List;

import longa.domains.sql.insert_chain;
        import longa.lang.List;

public class java_sql_select4 {
    private void ___fieldinit() {
    }

    public java_sql_select4() {
        this.___fieldinit();
    }

    public void main() {
        List List_0 = new List();
        List_0.add(true);
        List_0.add("Tom");
        insert_chain insert_0 = new insert_chain();
        insert_0.__start();
        insert_0.into("table1");
        insert_0.values(List_0);
        insert_0.__end();
    }
}