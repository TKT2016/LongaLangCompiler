package longac.lgac;

import longa.lang.LongaSpecialNames;
import longac.attrs.ExprVisitContext;
import longac.attrs.FindKinds;
import longac.attrs.StmtExpAttrTranslator;
import longac.lgac.trees.MethodChainTree;
import longac.lgac.trees.TypeChainTree;
import longac.symbols.*;
import longac.trees.JCChain;
import longac.trees.JCExpression;
import longac.trees.JCIdent;
import longac.trees.JCMethodInvocation;
import longac.utils.Debuger;
import tools.collectx.ArrayListUtil;

import java.util.ArrayList;

import static longa.lang.LongaSpecialNames.*;

public class ChainTreeParser {
    //static int chain_anonymous_var_index=0;
    JCChain tree;
    NodeReader reader;

    TypeChainTree masterTypeNode;

    StmtExpAttrTranslator expVisitor;
    ExprVisitContext context;

    TypeChainTree currentTypeNode;
    MethodChainTree currentMethodNode;

    public static final boolean isStatic = false;

    public ChainTreeParser(JCChain tree, StmtExpAttrTranslator expVisitor, ExprVisitContext arg)
    {
        reader = new NodeReader(tree);
        this.expVisitor=expVisitor;
        this.context =arg;
        this.tree =tree;
    }

    public TypeChainTree parse(){
        parseMaster();
        while (reader.node != null)
            parseNode();

        return masterTypeNode;
    }

    void parseMaster()
    {
        parseMasterIdent();
        parseStartMethod(masterTypeNode);
        reader.next();
    }

    void parseMasterIdent()
    {
        JCIdent topJCIdent  =(JCIdent) reader.node;

        String name_chain = topJCIdent.name+chainTypeSuffix;
        ArrayList<TypeSymbol> typeSymbols = this.expVisitor.typeFound.findType(name_chain , context.frame);
        JavaClassSymbol typeSymbol = (JavaClassSymbol) typeSymbols.get(0);
        masterTypeNode = new TypeChainTree( tree,topJCIdent,typeSymbol,null);
        currentTypeNode = masterTypeNode;
    }

    void parseStartMethod(TypeChainTree typeNode)
    {
        ArrayList<JavaMethodSymbol> startMethods = currentTypeNode.javaClassSymbol.findMethods(startMthodName,isStatic);
        ArrayList<MethodSymbol> methodSymbols2= ArrayListUtil.toList(startMethods);

        JCIdent jcIdent = new JCIdent(startMthodName);
        jcIdent.line = typeNode.startIdent.line;
        jcIdent.pos = typeNode.startIdent.pos;
        jcIdent.log = typeNode.startIdent.log;
        parseMethods(jcIdent,typeNode,methodSymbols2);
    }

    void parseNode() {
        if (reader.node == null) return;
        if (reader.node instanceof JCIdent) {
            parseIdent();
            reader.next();
            //parseNode();
        }
        else {
            if (reader.node instanceof JCMethodInvocation) {
                JCMethodInvocation jcMethodInvocation = (JCMethodInvocation) reader.node;
                if(jcMethodInvocation.meth instanceof JCIdent)
                {
                    if(processMember((JCIdent) jcMethodInvocation.meth))
                    {
                        for(JCExpression arg : jcMethodInvocation.args)
                        {
                            parseMethodArg(arg);
                        }
                        reader.next();
                       return;
                    }
                }
            }

            parseMethodArg(reader.node);
           // JCExpression jcExpression = (JCExpression) reader.node.translate(this.expVisitor, this.context);
            //Debuger.outln("101 parseNode:"+jcExpression);
           // currentMethodNode.args.add(jcExpression);
            reader.next();
            //parseNode();
        }
    }

    JCExpression parseMethodArg(JCExpression expression)
    {
        JCExpression jcExpression = (JCExpression)expression.translate(this.expVisitor, this.context);
        //Debuger.outln("101 parseNode:"+jcExpression);
        currentMethodNode.args.add(jcExpression);
        return jcExpression;
    }

    void parseIdent()
    {
        JCIdent jcIdent =(JCIdent) reader.node;
        if(processMember(jcIdent))
            return;

        String name = jcIdent.name;
        if(currentTypeNode.parent!=null) {
            JavaClassSymbol javaClassSymbol = currentTypeNode.parent.javaClassSymbol;
            ArrayList<Symbol> members2 =javaClassSymbol.findMembersLgaNode(name,isStatic);
            if (members2.size() > 0) {
                currentTypeNode=currentTypeNode.parent;
                parseIdent();
                return;
            }
        }
        FindKinds findKinds = new FindKinds();
        findKinds.isFindVar=true;
        ExprVisitContext newContext = this.context.clone(findKinds);

        jcIdent.translate(this.expVisitor,newContext);
        currentMethodNode.args.add(jcIdent);
    }

    boolean processMember(JCIdent jcIdent)
    {
        String name = jcIdent.name;
        ArrayList<Symbol> members = currentTypeNode.javaClassSymbol.findMembersLgaNode(name,isStatic);
        if(members.size()==1 && members.get(0) instanceof VarSymbol)//字段
        {
            JavaVarSymbol fieldSymbol =(JavaVarSymbol)members.get(0);
            parseFieldType(fieldSymbol,jcIdent);
            return true;
        }
        else if(members.size()>0) //方法
        {
            ArrayList<MethodSymbol> methodSymbols = new ArrayList<>();
            for(Symbol symbol:members)
            {
                methodSymbols.add((MethodSymbol) symbol);
            }
            parseMethods(jcIdent ,currentTypeNode,methodSymbols);
            return true;
        }
        return false;
    }

    void parseMethods(JCIdent jcIdent ,TypeChainTree typeNode,ArrayList<MethodSymbol> methodSymbols)
    {
        MethodChainTree newMethodNode = new MethodChainTree(typeNode ,jcIdent,methodSymbols);
        pushNewMethodNode(newMethodNode);
        typeNode.calls.add(currentMethodNode);
    }

    void parseFieldType(JavaVarSymbol fieldSymbol, JCIdent jcIdent)
    {
        TypeChainTree fieldTypeNode = new TypeChainTree( tree,jcIdent,fieldSymbol,currentTypeNode);
        currentTypeNode.calls.add(fieldTypeNode);
        currentTypeNode = fieldTypeNode;
        parseStartMethod(fieldTypeNode);
    }

    void pushNewMethodNode(MethodChainTree newMethodNode)
    {
        newMethodNode.preMethod = currentMethodNode;
        if(currentMethodNode!=null)
            currentMethodNode.nextMethod = newMethodNode;
        currentMethodNode = newMethodNode;
    }
}
