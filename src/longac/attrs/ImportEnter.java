package longac.attrs;

import longac.diagnostics.SimpleLog;
import longac.symbols.*;
import longac.utils.CompileContext;

import java.util.ArrayList;
import longac.trees.*;
public class ImportEnter
{
    SimpleLog log;
    CompileContext context;

    public ImportEnter(CompileContext context) {
        this.context = context;
        log =context.log;
    }

    public void visit(ArrayList<JCFileTree> trees) {
        for(JCFileTree tree : trees)
        {
            visit(tree);
        }
    }

    public void visit(JCFileTree tree) {
        SourceFileSymbol fileSymbol =(SourceFileSymbol)tree.getSymbol();
        boolean isImportJavaLang = false; //是否包含 import java.lang。*
        for(JCTree def : tree.defs)
        {
            if(def instanceof JCImport)
            {
                JCImport jcImport=(JCImport)def;
                ImportSymbol importSymbol = new ImportSymbol(jcImport,fileSymbol,jcImport.isStatic);
                fileSymbol.importSymbols.add(importSymbol);
                jcImport.setSymbol( importSymbol);

                if(jcImport.toString().equals(java_lang_star))
                    isImportJavaLang=true;
            }
        }
        // 如果没有导入 java.lang，则加入它
        if(!isImportJavaLang )
        {
            ImportSymbol importSymbol = new ImportSymbol("java.lang","*",fileSymbol,false);
            fileSymbol.importSymbols.add(importSymbol);
        }
    }

    private static final String java_lang_star="java.lang.*";
}
