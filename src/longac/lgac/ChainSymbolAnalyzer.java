package longac.lgac;

import longac.attrs.ExprVisitContext;
import longac.attrs.StmtExpAttrTranslator;

import longac.symbols.*;
import longac.trees.JCExpression;
import longac.trees.JCIdent;
import java.util.ArrayList;
import static longa.lang.LongaSpecialNames.*;
import longac.lgac.trees.*;
import longac.utils.Assert;
import longac.utils.Debuger;

public class ChainSymbolAnalyzer {

    //final TypeChainTree typeChainTree;

    StmtExpAttrTranslator expVisitor;
    ExprVisitContext context;

    public ChainSymbolAnalyzer( StmtExpAttrTranslator expVisitor, ExprVisitContext context)
    {
        //this.typeChainTree = typeChainTree;
        this.expVisitor = expVisitor;
        this.context = context;
    }

    public void parse(TypeChainTree typeChainTree){
        parseTypeChainTree(typeChainTree);
    }

    void parseTypeChainTree(TypeChainTree typeChainTree)
    {
        for(ChainTree tree:typeChainTree.calls)
        {
            if(tree instanceof TypeChainTree)
            {
                parseTypeChainTree((TypeChainTree)tree);
            }
            else if(tree instanceof MethodChainTree)
            {
                parseMethodChainTree((MethodChainTree)tree);
            }
        }

        parseEndMethod(typeChainTree);
    }

    void parseMethodChainTree(MethodChainTree methodChainTree)
    {
        ajuestMethodChainTree(methodChainTree);

        ArrayList<JCExpression> newargs = new ArrayList<>();
        ArrayList<TypeSymbol> argTypes = new ArrayList<>();
        for(JCExpression tree:methodChainTree.args)
        {
            if(tree.getSymbol()==null)
                Assert.error();
                //Debuger.outln("57 parseMethodChainTree:"+tree);
           // if(tree.getSymbol()==null) {
           //     JCExpression newarg = (JCExpression) tree.translate(this.expVisitor, this.context);
                newargs.add(tree);
                argTypes.add(tree.getTypeSymbol());
           // }
        }
        methodChainTree.args = newargs;
        String methodName = methodChainTree.getMethodName();
        //JavaMethodSymbol methodSymbol = parseJavaMethodSymbol(methodChainTree.typeChainTree.javaClassSymbol,methodName,methodChainTree.methodIdent,argTypes);
        MethodSymbol methodSymbol = parseJavaMethodSymbol(methodChainTree,argTypes);
        if(methodSymbol!=null)
        {
            methodChainTree.setMethodSymbol((JavaMethodSymbol) methodSymbol);
            for (int i=0;i<methodChainTree.args.size();i++)
            {
                if(i<methodSymbol.getParameterCount() ) {
                    JCExpression jcExpression = methodChainTree.args.get(i);
                    TypeSymbol typeSymbol = methodSymbol.getParameterType(i);
                    jcExpression.requireConvertTo = typeSymbol;
                }
                else
                {
                    methodChainTree.methodIdent.error(String.format("函数'%s'参数个数不对",methodName));
                }
            }
        }
        else
        {
            methodChainTree.methodIdent.setSymbol(  new ErrorSymbol());
        }
    }

    void ajuestMethodChainTree(MethodChainTree methodChainTree)
    {
        if(methodChainTree.nextMethod!=null)
        {
            int prepCount = methodChainTree.nextMethod.prepCount();
            //if(methodChainTree.getMethodName().equals("from"))
             //   prepCount = methodChainTree.nextMethod.prepCount();
           // Debuger.outln("98 ajuestMethodChainTree:" +methodChainTree.getMethodName()+" -> "+methodChainTree.nextMethod.getMethodName()+" "+ prepCount);
            if(prepCount>0)
            {
                for(int i=0;i<prepCount;i++)
                {
                    JCExpression expression = methodChainTree.args.remove( methodChainTree.args.size()-1);
                    methodChainTree.nextMethod.args.add(0,expression);
                }
            }
        }
    }

    MethodSymbol parseJavaMethodSymbol(MethodChainTree methodChainTree, ArrayList<TypeSymbol> argTypes)
    {
        String methodName= methodChainTree.getMethodName();
        JCIdent methodIdent = methodChainTree.methodIdent;

        //if(methodName.equals("from"))
        //    Debuger.outln("121 parseJavaMethodSymbol:"+methodName);

        ArrayList<MethodSymbol> symbols = methodChainTree.methodSymbols;
       // ArrayList<Symbol> symbols =typeSymbol.findMembers(methodName,ChainTreeParser.isStatic);
        if(symbols.size()==0)
        {
            Assert.error();
            methodIdent.error(String.format("找不到符号'%s'",methodName));
            return null;
        }
        else if(symbols.size()==1)
        {
            return (MethodSymbol)symbols.get(0);
        }
        else
        {
            ArrayList<MethodSymbol> methodSymbols = SymbolUtil.matchArgTypes(argTypes,symbols);
            if (methodSymbols.size() == 1) {
                return methodSymbols.get(0);
            }
            else if (methodSymbols.size() == 0) {
                methodIdent.error( "找不到合适的方法");
                return null;
            }
            else {
                methodIdent.error( "方法调用有歧义");
                return null;
            }
        }
    }

    void parseEndMethod(TypeChainTree typeChainTree)
    {
        JavaClassSymbol chainTypeSymbol = typeChainTree.javaClassSymbol;
        ArrayList<JavaMethodSymbol> endSymbols = chainTypeSymbol.findMethods(endMthodName,ChainTreeParser.isStatic);
        typeChainTree.endMethodSymbol =(JavaMethodSymbol) endSymbols.get(0);
    }
}
