package longac._core;

import longac.attrs.FindKinds;
import longac.symbols.*;
import longac.symbols.SymbolFrame;
import longac.utils.Assert;
import longac.utils.CompileContext;
import longac.visitors.TreeScanner;

import java.util.ArrayList;
import longac.trees.*;
public class MemberEnter extends TreeScanner<Object> {
    TypeFound typeFound;
    //SimpleLog log;
    CompileContext context;

    public MemberEnter(CompileContext context) {
        this.context = context;
        //log =context.log;
        typeFound = new TypeFound();
    }

    public void enter(ArrayList<JCFileTree> trees) {
        for(JCFileTree tree : trees)
        {
            tree.scan(this, null);
        }
    }

    @Override
    public void visitClassDef(JCClassDecl tree, Object arg) {
        for(JCTree memberTree : tree.defs)
        {
            memberTree.scan(this, tree.getSymbol());
        }
    }

    @Override
    public void visitVarDef(JCVariableDecl tree, Object arg)
    {
        String varName = tree.name;
        TypeSymbol typeSymbol =typeFound.findType(tree.vartype,(SymbolFrame) arg);

        if(arg instanceof DeclClassSymbol) {
            tree.dimKind = VarKind.field;
            DeclClassSymbol classSymbol = (DeclClassSymbol) arg;
            DeclVarSymbol fieldSymbol = new DeclVarSymbol(tree.name, classSymbol, VarKind.field, typeSymbol);
            if(classSymbol.containsField(fieldSymbol.name))
            {
                tree.error(String.format("已在类 '%s'中定义了字段 '%s'",classSymbol.name,tree.name));
            }
            else
            {
                classSymbol.addVar(fieldSymbol);
            }
            tree.setSymbol(fieldSymbol);
        }
        else if(arg instanceof DeclMethodSymbol)
        {
            tree.dimKind = VarKind.parameter;
            DeclMethodSymbol methodSymbol = (DeclMethodSymbol) arg;
            DeclVarSymbol paramSymbol = new DeclVarSymbol(tree.name, methodSymbol, VarKind.parameter , typeSymbol);
            if(SymbolFrameUtil.containsVar(methodSymbol, varName,new FindKinds(false,true)))// if(methodSymbol.containsVar(varName))
            {
                tree.error(String.format("已经定义了参数 '%s'",tree.name));
            }
            else
            {
                methodSymbol.addVar(paramSymbol);
            }
            tree.setSymbol(paramSymbol);
        }
        else
            Assert.error();
    }

    @Override
    public void visitMethodDef(JCMethodDecl tree, Object arg) {
        DeclClassSymbol classSymbol = (DeclClassSymbol) arg;
        TypeSymbol retSym = null;
        if(tree.isContructor())
        {
            retSym = classSymbol;
        }
        else {
            retSym = typeFound.findType( tree.retTypeExpr, classSymbol);
        }

        DeclMethodSymbol methodSymbol = new DeclMethodSymbol(tree.name, classSymbol, retSym);
        tree.setSymbol(methodSymbol);
        signature(methodSymbol, tree);
        if (!classSymbol.addMethod(methodSymbol)) {
            tree.error( String.format("已在类 '%s'中定义了方法 '%s'", classSymbol.name, tree.getHeadString()));
        }
    }

    DeclMethodSymbol signature(DeclMethodSymbol methodSymbol, JCMethodDecl methodTree ) {
        ArrayList<JCVariableDecl> params = methodTree.params;
       // ArrayList<TypeSymbol> argbuf = new ArrayList<>();
        for (JCVariableDecl variableDecl : params)
        {
            variableDecl.scan(this,methodSymbol);
            String name = variableDecl.name;
            if(methodSymbol.getDeclClassSymbol().containsField(name))
            {
                variableDecl.error(String.format("类中已经定义了字段'%s',不能重复定义",name));
            }
           /* else if(methodSymbol.containsParameter(name))
            {
                log.error(variableDecl,String.format("方法中已经定义了参数'%s',不能重复定义",name));
            }*/
           /* else {
                methodSymbol.addVar((DeclVarSymbol) variableDecl.getSym());
            }*/
           // argbuf.add(( TypeSymbol)variableDecl.vartype.getSym());
        }
        //TypeSymbol retSym =typeFound.findType(log,methodTree.retTypeExpr,methodSymbol);
        //methodSymbol.returnTypeSymbol =retSym;
        return methodSymbol;
    }
}
