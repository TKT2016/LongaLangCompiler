package longac._core;

import longac.symbols.*;
import longac.symbols.SymbolFrame;
import longac.symbols.JavaArrayTypeSymbol;
import longac.symbols.JavaClassSymbol;
import longac.utils.Assert;

import java.util.ArrayList;
import longac.trees.*;
public class TypeFound {

    public  ArrayList<TypeSymbol> findType(String typeName, SymbolFrame arg)
    {
        TypeNameModel typeNameModel = new TypeNameModel(typeName);
        ArrayList<TypeSymbol> typeSymbols = SymbolFrameUtil.findTypes(arg, typeNameModel);
        return typeSymbols;
    }

    public TypeSymbol findType(JCExpression exp, SymbolFrame arg) {
        //if(exp.toString().equals("Object"))
        //    Debuger.outln(exp);
        TypeNameModel typeNameModel;
        ArrayList<TypeSymbol> typeSymbols = null;

        if (exp instanceof JCPrimitiveTypeTree) {
            JCPrimitiveTypeTree jcPrimitiveTypeTree = (JCPrimitiveTypeTree)exp;
            typeNameModel = new TypeNameModel(jcPrimitiveTypeTree.kind.name);
            JavaClassSymbol symbol = JavaClassSymbol.create(jcPrimitiveTypeTree.kind.getClazz());
            typeSymbols = new ArrayList<>();
            typeSymbols.add(symbol);
        }
        else if (exp instanceof JCIdent) {
            //isFullName = false;
            String typeName = ((JCIdent) exp).name;
            typeNameModel = new TypeNameModel(typeName);
            typeSymbols = SymbolFrameUtil.findTypes(arg, typeNameModel);
        }
        else if (exp instanceof JCFieldAccess) {
            JCFieldAccess jcFieldAccess = (JCFieldAccess) exp;
            typeNameModel = new TypeNameModel(jcFieldAccess.selected.toString(), jcFieldAccess.name);
            typeSymbols = SymbolFrameUtil.findTypes(arg, typeNameModel);
        }
        /*else if (exp instanceof JCArrayTypeTree) {
            JCArrayTypeTree arrayTypeTree = (JCArrayTypeTree) exp;
            typeNameModel = new TypeNameModel(arrayTypeTree.toString());
            //typeSymbols = TreeFrameUtil.findTypes(arg, typeNameModel);
            TypeSymbol etype = findType(arrayTypeTree.elemtype,arg);
            JavaArrayTypeSymbol arrayTypeSymbol = new JavaArrayTypeSymbol(etype);
            typeSymbols = new ArrayList<>();
            typeSymbols.add(arrayTypeSymbol);
        }*/
        else {
            Assert.error();
            typeNameModel = new TypeNameModel("");
        }

        if (typeSymbols.size() == 1) {
            exp.setSymbol(typeSymbols.get(0));
        }
        else if (typeSymbols.size() == 0) {
          exp.error(String.format("找不到类型'%s'", typeNameModel.getNameFull()));
            exp.setSymbol(new ErrorSymbol());
        }
        else {
            exp.error(String.format("对'%s'的引用不明确", typeNameModel.getNameFull()));
            exp.setSymbol(new ErrorSymbol());
        }
        return exp.getSymbol().getTypeSymbol();
    }



}
