package longac.attrs;

import longac._core.TypeFound;
import longac.lex.TokenKind;
import longac.lgac.*;
import longac.lgac.makeModels.LongaListAddModel;
import longac.lgac.makeModels.LongaListNewMakeModel;
import longac.lgac.makers.LongaListAddMaker;
import longac.lgac.makers.LongaNewListMaker;
import longac.lgac.trees.TypeChainTree;
import longac.symbols.*;
import longac.trees.*;
import longac.utils.Assert;
import longac.utils.CompileContext;
import longac.utils.Debuger;
import longac.visitors.TreeTranslator;
import tools.collectx.ArrayListUtil;

import java.util.ArrayList;

public class StmtExpAttrTranslator extends TreeTranslator<ExprVisitContext>
{
    public final TypeFound typeFound;

    CompileContext context;

    public StmtExpAttrTranslator(CompileContext context) {
        this.context = context;
        typeFound = new TypeFound();
    }

    @Override
    public JCTree translateCompilationUnit(JCFileTree compilationUnit , ExprVisitContext arg) {
        for(JCTree tree :compilationUnit.defs )
        {
           tree.translate(this,arg);
        }
        return compilationUnit;
    }

    @Override
    public JCTree translateClass(JCClassDecl tree, ExprVisitContext arg) {
        DeclClassSymbol declClassSymbol = tree.getDeclClassSymbol();
        arg = new ExprVisitContext();
        arg.classSymbol = declClassSymbol;
        arg.frame = declClassSymbol;
        super.translateClass(tree,arg);
        return tree;
    }

    @Override
    public JCTree translateMethod(JCMethodDecl tree, ExprVisitContext arg)
    {
        DeclMethodSymbol methodSymbol = tree.getMethodSymbol();
        arg.methodSymbol = methodSymbol;
        arg.frame = methodSymbol;
        tree.body.translate(this,arg);
        return tree;
    }

    @Override
    public JCTree translateBlock(JCBlock tree,  ExprVisitContext arg)
    {
        //BlockFrame blockFrame = new BlockFrame(arg.frame);
        ExprVisitContext context = arg.createBlock(); //arg.clone(blockFrame);
        context.insertStatements = new ArrayList<>();
        tree.frame =(BlockFrame)context.frame;
        ArrayList<JCStatement> newStatements = new ArrayList<>();
        for(JCStatement stmt:tree.stats)
        {
            stmt.translate(this,context);
            if(ArrayListUtil.nonEmpty(context.insertStatements ))
            {
                newStatements.addAll(context.insertStatements);
                context.insertStatements.clear();
            }
            newStatements.add(stmt);
        }
        tree.stats = newStatements ;
        return tree;
    }

    @Override
    public JCTree translateVariable(JCVariableDecl tree, ExprVisitContext arg) {
        String varName = tree.name;
        TypeSymbol typeSymbol = typeFound.findType(tree.vartype,arg.frame);

        if(tree.dimKind.equals(VarKind.localVar))
        {
            BlockFrame blockFrame = (BlockFrame)arg.frame;
            DeclMethodSymbol methodSymbol = SymbolFrameUtil.getDeclMethodSymbol(blockFrame);
            DeclVarSymbol varSymbol = new DeclVarSymbol(tree.name, methodSymbol, VarKind.localVar , typeSymbol);
            if(SymbolFrameUtil.containsVar(arg.frame, varName,new FindKinds(true,false)))
            {
                tree.error(String.format("已经定义了变量 '%s'",tree.name));
            }
            else
            {
                blockFrame.addVar(varSymbol);
            }
            //blockSymbolFrame.localVars.put(varSymbol.name,varSymbol);
            tree.setSymbol(varSymbol);
            if(tree.init!=null) {
                tree.init =(JCExpression) tree.init.translate(this,arg);
                scanAssign(tree.nameExpr,tree.init,arg);
                //tree.init.scan(this,arg);
                //scanAssign(tree.vartype,tree.init);
            }
        }
        else if(tree.dimKind.equals(VarKind.field))
        {
            if(tree.init!=null) {
                scanAssign(tree.nameExpr,tree.init,arg);
                //tree.init.scan(this,arg);
                //scanAssign(tree.vartype,tree.init);
            }
        }
        else if(tree.dimKind.equals(VarKind.parameter))
        {

        }
        else
            Assert.error();
        return tree;
    }

    @Override
    public JCTree translateWhile(JCWhile tree, ExprVisitContext arg)
    {
        if(tree.cond!=null)
            tree.cond.translate(this,arg);
        if(tree.body!=null)
            tree.body.translate(this,arg);
        return tree;
    }

    @Override
    public JCTree translateForLoop(JCForLoop tree, ExprVisitContext arg)
    {
        ExprVisitContext context = arg.createBlock(); //arg.clone(blockFrame);
        tree.frame =(BlockFrame)context.frame;

        //BlockFrame blockFrame = new BlockFrame(arg.frame);
        //ExprVisitContext context = arg.clone(blockFrame);
        //tree.frame = blockFrame;

        if (tree.init != null) {
            tree.init.translate(this,context);
        }

        if (tree.cond != null) {
            tree.cond.translate(this,context);
        }
        if (tree.step != null) {
            tree.step.translate(this,context);
        }
        tree.body=(JCBlock) tree.body.translate(this,context);
        return tree;
    }

    @Override
    public JCTree translateIf(JCIf tree, ExprVisitContext arg)
    {
        tree.cond.translate(this,arg);
        tree.thenpart.translate(this,arg);
        if(tree.elsepart!=null)
            tree.elsepart.translate(this,arg);
        return tree;
    }

    @Override
    public JCTree translateExpressionStatement(JCExpressionStatement tree, ExprVisitContext arg)
    {
        tree.expr=(JCExpression) tree.expr.translate(this,arg);
        return tree;
    }

    @Override
    public JCTree translateReturn(JCReturn tree, ExprVisitContext arg)
    {
        tree.declMethodSymbol = arg.methodSymbol;
        if(tree.declMethodSymbol.isContructor())
        {
            if (tree.expr != null) {
                tree.expr.error("构造函数内不能有返回值" );
            }
        }
        else if(SymbolUtil.isVoid( tree.declMethodSymbol.returnTypeSymbol))
        {
            if (tree.expr != null) {
                tree.expr.error("意外的返回值" );
            }
        }
        else {
            if (tree.expr == null) {
                tree.expr.error("缺少返回值" );
            }
            if (tree.expr != null) {
                ExprVisitContext context = arg.clone();
                context.findKinds = new FindKinds(true, false);
                tree.expr.translate(this, context);

                if( tree.declMethodSymbol.returnTypeSymbol instanceof JavaClassSymbol)
                    tree.expr.requireConvertTo = (JavaClassSymbol)tree.declMethodSymbol.returnTypeSymbol;
            }
        }
        return tree;
    }

    @Override
    public JCTree translateFieldAccess(JCFieldAccess tree, ExprVisitContext arg)
    {
        ExprVisitContext treeSelectedcontext = arg.clone(new FindKinds(true, false,true));
        //treeSelectedcontext.findKinds = new FindKinds(true, false,true);
        tree.selected.translate(this,treeSelectedcontext);

        Symbol selectedSymbol = tree.selected.getSymbol();
        String name = tree.name;
        if(selectedSymbol instanceof PackageSymbol)
        {
            TypeSymbol typeSymbol2 = ((PackageSymbol)selectedSymbol).findTypeFromSimpleClassName(name);
            tree.setSymbol( typeSymbol2);
        }
        else if(selectedSymbol instanceof TypeSymbol)
        {
            TypeSymbol typeSymbol1 = (TypeSymbol)selectedSymbol;
            attrMemberSymbol(tree,name,typeSymbol1,typeSymbol1,true);
        }
        else if(selectedSymbol instanceof VarSymbol)
        {
            VarSymbol varSymbol = (VarSymbol)selectedSymbol;
            TypeSymbol typeSymbol1 = varSymbol.getTypeSymbol();
            attrMemberSymbol(tree,name,typeSymbol1,varSymbol,false);
        }
        else {
           Assert.error();
        }
        return tree;
    }

    private ArrayList<TypeSymbol> attrArgs(ArrayList<JCExpression> args, ExprVisitContext context)
    {
        ArrayList<TypeSymbol> argTypes = new ArrayList<>();
        for (JCExpression jcExpression : args)
        {
            ExprVisitContext argContext = context.clone(new FindKinds(true,false,false ));
            JCExpression newExpression = (JCExpression)jcExpression.translate(this, argContext);
            if(newExpression.getSymbol()==null)
                Assert.error();
            argTypes.add(newExpression.getSymbol().getTypeSymbol());
        }
        return argTypes;
    }

    @Override
    public JCTree translateMethodInvocation(JCMethodInvocation tree, ExprVisitContext arg) {
        /* 分析参数 */
        ArrayList<TypeSymbol> argTypes = attrArgs(tree.args,arg);
        ExprVisitContext treeMethcontext = arg.clone(new FindKinds(false, true,false));
        tree.meth.translate(this, treeMethcontext);
        MethodSymbol methodSymbol = null;
        Symbol symbol = tree.meth.getSymbol();
        if (symbol instanceof MutilSymbol) {
            MutilSymbol mutilSymbol = (MutilSymbol) symbol;
            ArrayList<MethodSymbol> methodSymbols = mutilSymbol.matchArgTypes(argTypes);
            if (methodSymbols.size() == 1) {
                 methodSymbol = methodSymbols.get(0);
                tree.meth.setSymbol(methodSymbol);
                tree.setSymbol(methodSymbol.getTypeSymbol());
            }
            else if (methodSymbols.size() == 0) {
                tree.error( "找不到合适的方法");
                tree.meth.setSymbol(new ErrorSymbol());
                tree.setSymbol(new ErrorSymbol());
            }
            else {
                tree.error( "方法调用有歧义");
                tree.meth.setSymbol(new ErrorSymbol());
                tree.setSymbol(new ErrorSymbol());
            }
        }
        else if(symbol instanceof ErrorSymbol)
        {
            tree.setSymbol(symbol);
            return tree;
        }
        else if (!(symbol instanceof MethodSymbol)) {
            tree.error( String.format("符号'%s'不是方法", symbol.name));
            tree.setSymbol(new ErrorSymbol());
            return tree;
        }
        else {
            methodSymbol = (MethodSymbol) symbol;
            if (methodSymbol.matchArgTypes(argTypes) < 0) {
                tree.error( String.format("方法'%s'的参数不匹配", symbol.name));
                tree.meth.setSymbol(new ErrorSymbol());
                tree.setSymbol(new ErrorSymbol());
            }
            else
            {
                tree.meth.setSymbol(methodSymbol);
                tree.setSymbol(methodSymbol.returnTypeSymbol);
            }
        }

        if(methodSymbol!=null)
        {
            for (int i=0;i<tree.args.size();i++)
            {
                JCExpression jcExpression =tree.args.get(i);
                TypeSymbol typeSymbol = methodSymbol.getParameterType(i);
                jcExpression.requireConvertTo = typeSymbol;
            }
        }
        return tree;
    }

    @Override
    public JCTree translateNewClass(JCNewClass tree, ExprVisitContext arg)
    {
        TypeSymbol typeSymbol = typeFound.findType(tree.clazz,arg.frame); //tree.clazz.scan(this,arg);
        ArrayList<TypeSymbol> argTypes = attrArgs(tree.args,arg);
        ArrayList<MethodSymbol> methodSymbols = typeSymbol.findConstructor(argTypes);
        if(methodSymbols.size()==1)
        {
            tree.constructorSymbol=methodSymbols.get(0);
        }
        else if(methodSymbols.size()==0)
        {
          tree.error("无法将构造器应用到给定类型");
        }
        else
        {
            tree.error("构造器不明确");
        }

        if(tree.constructorSymbol!=null)
        {
            for (int i=0;i<tree.args.size();i++)
            {
                JCExpression jcExpression =tree.args.get(i);
                TypeSymbol typeSymbol2 = tree.constructorSymbol.getParameterType(i);
                jcExpression.requireConvertTo = typeSymbol2;
            }
        }
        tree.setSymbol(typeSymbol);
        return tree;
    }

    static int AnonymousListVarIndex=0;

    @Override
    public JCTree translateNewArray(JCNewList tree, ExprVisitContext arg)
    {
        if(tree.listVarSymbol!=null) return tree;
        //listSymbol
        JavaClassSymbol listSymbol =JavaClassSymbol.listSymbol;
        tree.setSymbol(listSymbol);

        //make new list
        LongaNewListMaker longaNewListMaker = new LongaNewListMaker(tree,arg);
        LongaListNewMakeModel longaListNewMakeModel = longaNewListMaker.make();
        arg.insertStatements.add(longaListNewMakeModel.declVarStmt);

        JavaMethodSymbol addMethodSymbol = listSymbol.findMethods("add",false).get(0);

        //make add
        ArrayList<JCExpression> newExprs = new ArrayList<>();
        for(JCExpression argitem:tree.elems)
        {
            JCExpression newexpr = (JCExpression)argitem.translate(this, arg);
            newExprs.add(newexpr);

            LongaListAddMaker longaListAddMaker = new LongaListAddMaker(arg ,tree,newexpr,addMethodSymbol );
            LongaListAddModel longaListAddModel = longaListAddMaker.make();
            arg.insertStatements.add(longaListAddModel.invokeStmt);
        }
        tree.elems = newExprs ;

        return longaListNewMakeModel.nameIdent;
    }

    @Override
    public JCTree translateParens(JCParens tree, ExprVisitContext arg)
    {
        tree.expr.translate(this,arg);
        tree.setSymbol(tree.expr.getSymbol());
        return tree;
    }

    @Override
    public JCTree translateAssign(JCAssign tree, ExprVisitContext arg)
    {
        tree.left.isAssignLeft = true;
        scanAssign(tree.left,tree.right,arg);

        /*FindKinds findKinds = new FindKinds(true, false, false);
        ExprVisitContext leftcontext = arg.clone(findKinds);
        //tree.left.findKinds = new FindKinds(true,false);
        tree.left.scan(this,leftcontext);
        ExprVisitContext rightcontext = arg.clone(findKinds);
        //tree.right.findKinds = new FindKinds(true,false);
        tree.right.scan(this,rightcontext);*/
        /*if(tree.right.getSym() instanceof MutilSymbol)
        {
            tree.right.scan(this,rightcontext);
        }*/
        //scanAssign(tree.left,tree.right);
       // tree.right.requireConvertTo = (tree.left.getTypeSymbol());
        return tree;
    }

    @Override
    public JCTree translateUnary(JCUnary tree, ExprVisitContext arg)
    {
        tree.expr.translate(this,arg);
        tree.setSymbol(tree.expr.getSymbol().getTypeSymbol());
        return tree;
    }

    @Override
    public JCTree translateBinary(JCBinary tree, ExprVisitContext arg)
    {
        ExprVisitContext leftcontext = arg.clone(new FindKinds(true, false, false));
        tree.left.translate(this,leftcontext);
        ExprVisitContext rightcontext = arg.clone(new FindKinds(true, false, false));
        tree.right.translate(this,rightcontext);
        TokenKind op = tree.opcode;
        Symbol leftsym = tree.left.getSymbol();
        Symbol rightsym = tree.right.getSymbol();

        switch (op)
        {
            case AND: case OR:
                if(!SymbolUtil.isBoolean(leftsym.getTypeSymbol()))
                    tree.left.error("数据类型应该是'boolean'");
                if(!SymbolUtil.isBoolean(rightsym.getTypeSymbol()))
                    tree.right.error("数据类型应该是'boolean'");
                tree.setSymbol(JavaClassSymbol.create(boolean.class));
                break;
            case GT:case GTEQ: case EQEQ: case LTEQ: case NOTEQ:case  LT:
                checkNumber(tree.left);
                checkNumber(tree.right);
                tree.setSymbol(JavaClassSymbol.booleanPrimitiveSymbol);
                break;
            case ADD:
                if(SymbolUtil.isString(leftsym.getTypeSymbol())||SymbolUtil.isString(rightsym.getTypeSymbol()))
                {
                    tree.isStringAppend = true;
                    tree.setSymbol(JavaClassSymbol.StringSymbol);
                   // tree.anonymousStringBuilderSymbol = new DeclVarSymbol("#anonymous"+ arg.methodSymbol.getVarCount(), arg.methodSymbol,VarKind.localVar,JavaClassSymbol.StringSymbol);
                   // arg.frame.addVar( tree.anonymousStringBuilderSymbol);
                    tree.left.requireConvertTo = JavaClassSymbol.ObjectSymbol;
                    tree.right.requireConvertTo = JavaClassSymbol.ObjectSymbol;
                }
                else
                {
                    attrNumberBinary(tree);
                }
                break;
            case SUB: case MUL: case DIV://case PERCENT:
                attrNumberBinary(tree);
                break;
        }
        return tree;
    }

    private void attrNumberBinary(JCBinary tree)
    {
        checkNumber(tree.left);
        checkNumber(tree.right);
        TypeSymbol leftType = tree.left.getSymbol().getTypeSymbol();
        TypeSymbol rightType = tree.right.getSymbol().getTypeSymbol();
        //TypeSymbol maxType = getMaxType(leftType,rightType);
        TypeSymbol maxType = JavaClassSymbol.intPrimitiveSymbol;
        tree.left.requireConvertTo = maxType;
        tree.right.requireConvertTo = maxType;
        tree.setSymbol(maxType);
    }

    private boolean checkNumber(JCTree tree)
    {
        boolean b = SymbolUtil.isInt(tree.getSymbol().getTypeSymbol());
        if(!b)
            tree.error("数据类型应该是数字");
        return b;
    }

    private Symbol attrMemberSymbol(JCFieldAccess tree,String name, TypeSymbol typeSymbol,Symbol ownerSymbol,boolean isStatic)
    {
        ArrayList<Symbol> symbols =typeSymbol.findMembers(name,isStatic);
        if(symbols.size()==0)
        {
            tree.error(String.format("找不到符号'%s'",name));
           Symbol errorSymbol =  new ErrorSymbol();
            tree.setSymbol(errorSymbol);
            return errorSymbol;
        }
        else if(symbols.size()==1)
        {
            tree.setSymbol(symbols.get(0));
            return symbols.get(0);
        }
        else
        {
            MutilSymbol mutilSymbol = new MutilSymbol(name,ownerSymbol,symbols);
            tree.setSymbol(mutilSymbol);
            return mutilSymbol;
        }
    }

    @Override
    public JCTree translateIdent(JCIdent tree, ExprVisitContext arg)
    {
        //if(tree.name.equals("integer"))
      //      Debuger.outln("505 visitIdent:"+tree);
        if(tree.name.equals(TokenKind.THIS.name))
        {
            DeclClassSymbol classSymbol = arg.classSymbol;
            tree.setSymbol(classSymbol.thisFieldSymbol);
            tree.identKind = JCIdent.IdentKind.THIS;

            if(tree.isAssignLeft)
            {
                tree.error("'this'不能被修改");
            }
        }
        else {
            ArrayList<Symbol> symbols = SymbolFrameUtil.findVars(arg.frame ,tree.name,arg.findKinds);
            if (symbols.size() == 1) {
                Symbol symbol = symbols.get(0);
                tree.setSymbol(symbol);
                if(tree.isAssignLeft)
                {
                    boolean canWrite = false;
                    if(symbol instanceof  VarSymbol)
                    {
                        canWrite =((VarSymbol)symbol).canWrite();
                    }
                    if(!canWrite)
                        tree.error(String.format("'%s'是只读的,不能被修改",tree.name));
                }
            }
            else if (symbols.size() == 0) {
                tree.error("找不到变量'"+tree.name+"'");
                tree.setSymbol(new ErrorSymbol());
            }
            else {
                DeclClassSymbol classSymbol =  arg.classSymbol;
                MutilSymbol mutilSymbol = new MutilSymbol(tree.name,classSymbol,symbols);
                tree.setSymbol(mutilSymbol);
            }
        }
        return tree;
    }

    @Override
    public JCTree translateLiteral(JCLiteral tree, ExprVisitContext arg) {
        JavaClassSymbol javaClassSymbol = JavaClassSymbol.create(tree.getValueClazz());
        tree.setSymbol(SymbolUtil.checkToPrimitive(javaClassSymbol));
        return tree;
    }

    @Override
    public JCTree translateChain(JCChain tree, ExprVisitContext arg)
    {
        ChainTreeParser chainTreeParser = new ChainTreeParser(tree,this,arg);
        TypeChainTree typeChainTree= chainTreeParser.parse();


        //Debuger.outln("571 translateChain:"+typeChainTree);
        ChainSymbolAnalyzer chainSymbolParser = new ChainSymbolAnalyzer(this,arg);
        chainSymbolParser.parse(typeChainTree);

        ChainChecker chainChecker = new ChainChecker(this,arg);
        chainChecker.check(typeChainTree);

        ChainTreeMaker chainTreeMaker = new ChainTreeMaker(typeChainTree,this,arg);
        chainTreeMaker.make();

        return typeChainTree.methodEndModel.methodInvocation;
    }

    @Override
    public JCTree translatePrimitiveType(JCPrimitiveTypeTree tree, ExprVisitContext arg) {
        Symbol symbol = JavaClassSymbol.create(tree.kind.getClazz());
        tree.setSymbol( symbol.getTypeSymbol());
        return tree;
    }

    private void scanAssign(JCExpression left,JCExpression right,ExprVisitContext arg)
    {
        FindKinds findKinds = new FindKinds(true, false, false);
        right.translate(this,arg.clone(findKinds));
        left.translate(this,arg.clone(findKinds));

        checkAssignable(left,right);
         right.requireConvertTo = (left.getTypeSymbol());
    }

    private boolean checkAssignable( JCExpression left,JCExpression right)
    {
        if(right.getSymbol()==null||right.getSymbol().getTypeSymbol()==null)
            Debuger.outln("595 checkAssignable:"+right);
        TypeSymbol leftType = left.getSymbol().getTypeSymbol();
        TypeSymbol rightType = right.getSymbol().getTypeSymbol();

        if(SymbolUtil.isInt(leftType))
        {
            if( SymbolUtil.isInt(rightType))
                return true;
            right.error("类型不能赋值给int");
            return false;
        }
        else if(!leftType.isAssignableFrom(rightType))
        {
            right.error("类型不一致,无法赋值");
            //leftType.isAssignableFrom(rightType);
            return false;
        }
        return true;
    }
}
