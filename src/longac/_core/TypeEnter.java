package longac._core;

import longac.diagnostics.SimpleLog;
import longac.symbols.*;
import longac.utils.CompileContext;

import java.util.ArrayList;
import longac.trees.*;
public class TypeEnter
{
    SimpleLog log;
    CompileContext context;

    public TypeEnter(CompileContext context) {
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

        for(JCTree def : tree.defs)
        {
            if(def instanceof JCClassDecl)
                visitClassDef((JCClassDecl)def,(SourceFileSymbol)tree.getSymbol());
           // else if(def instanceof JCClassDecl)
           //     visitImport((JCImport)def);
        }
    }

   /* public void visitImport(JCImport tree) {

    }*/

    public void visitClassDef(JCClassDecl tree, SourceFileSymbol compilationUnitSymbol) {
        PackageSymbol packageSymbol = compilationUnitSymbol.packageSymbol;
        String className = tree.name;
        DeclClassSymbol classSymbol = new DeclClassSymbol(className,compilationUnitSymbol );
        classSymbol.packageSymbol = packageSymbol;
        if(packageSymbol.contains(className))
            tree.error(String.format("包内已经定义了类型'%s'",className));
        else
            packageSymbol.add(classSymbol);
        tree.setSymbol( classSymbol);
    }
}
