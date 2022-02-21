package longac.emits;

import tools.asmx.ASMEmitlus;
import tools.asmx.JVMOpCodeSelecter;
import tools.jvmx.NamesTexts;
import longac.diagnostics.SourceLog;
import longac.emits.gens.EmitContext;
import longac.emits.gens.MjcGenEmit;
import longac.lex.TokenKind;
import longac.symbols.*;
import longac.symbols.JavaArrayTypeSymbol;
import longac.symbols.JavaClassSymbol;
import longac.trees.*;
import longac.utils.*;

import longac.visitors.TreeScanner;
import org.objectweb.asm.*;

import java.io.IOException;
import java.util.HashMap;

import static org.objectweb.asm.Opcodes.*;

public class CodeEmit extends TreeScanner<EmitContext>
{
    CompileContext context;
    boolean COMPUTE_FRAMES = true;
    boolean COMPUTE_MAXS = true;

    public CodeEmit(CompileContext context) {
        this.context = context;
    }

    public void genCompilationUnit(JCFileTree compilationUnit) throws IOException {
        EmitContext arg = null;
        compilationUnit.scan(this,arg);
    }

    public void visitCompilationUnit(JCFileTree compilationFile, EmitContext arg)    {
        for (JCTree tree : compilationFile.defs)
        {
            if (tree instanceof JCClassDecl) {
                tree.scan(this,arg);
            }
        }
    }

    public void visitClassDef(JCClassDecl classDecl, EmitContext arg)
    {
        lineMaps.clear();
        DeclClassSymbol declClassSymbol = classDecl.getDeclClassSymbol();

        ClassWriter classWriter = EmitUtil.emitClass(classDecl,COMPUTE_FRAMES,COMPUTE_MAXS);
        classWriter.visitSource(classDecl.log.getSimpleName(), null);

        /* 生成字段 */
        emitFields(classWriter,classDecl);

        if(declClassSymbol.constructorCount==0)
        {
            emitDefaultConstuctor(classWriter,classDecl);
        }

        /* 生成方法 */
        for (JCTree tree : classDecl.defs)
        {
            EmitContext methodEmitContext = new EmitContext();
            methodEmitContext.classWriter = classWriter;
            if(tree instanceof JCMethodDecl)
            {
                tree.scan(this,methodEmitContext);
            }
        }
        declClassSymbol.compiledClassFile = MjcGenEmit.writeFile(classDecl,classWriter);
        context.projectSymbol.compiledClassFiles.add(declClassSymbol);
    }

    private void emitDefaultConstuctor(ClassWriter classWriter , JCClassDecl classDecl)
    {
        DeclClassSymbol declClassSymbol = classDecl.getDeclClassSymbol();
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, NamesTexts.init, "()V", null, null);
        mv.visitCode();
        emitLineNumber(mv, classDecl.line);
        ASMEmitlus.visitVarInsn(mv,ALOAD,0);// mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        ASMEmitlus.visitVarInsn(mv,ALOAD,0);//mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, declClassSymbol.getSignature(false), fieldInitMethod, "()V", false);

        mv.visitInsn(RETURN);
        EmitUtil.visitMaxs(mv);//  EmitUtil.visitMaxs(mv,1,1);
        //EmitUtil.visitMaxs(mv,COMPUTE_FRAMES,COMPUTE_MAXS,1,1);
       //mv.visitMaxs(1, 1);//设置COMPUTE_MAXS时必须调用此方法触发计算
        //mv.visitMaxs(0, 0);/
        mv.visitEnd();
        //MjcGenEmit.visitEnd4init(mv);
    }

    final String fieldInitMethod ="___fieldinit";

    private void emitFields(ClassWriter classWriter , JCClassDecl classDecl)
    {
        EmitContext fieldEmitContext = new EmitContext();
        fieldEmitContext.classWriter = classWriter;
        MethodVisitor mv = classWriter.visitMethod(ACC_PRIVATE, fieldInitMethod , "()V", null, null);
        mv.visitCode();
        emitLineNumber(mv, classDecl.line);

        fieldEmitContext.mv = mv;
        for (JCTree tree : classDecl.defs)
        {
            if(tree instanceof JCVariableDecl)
            {
                tree.scan(this,fieldEmitContext);
            }
        }
        mv.visitInsn(RETURN);
        EmitUtil.visitMaxs(mv);// EmitUtil.visitMaxs(mv,1,1);
        mv.visitEnd();
    }

    @Override
    public void visitMethodDef(JCMethodDecl tree, EmitContext arg) {
        DeclMethodSymbol methodSymbol = tree.getMethodSymbol();

        DeclClassSymbol classSymbol= SymbolUtil.getDeclClassSymbol(methodSymbol);
        org.objectweb.asm.ClassWriter classWriter = arg.classWriter;

        LocalVarAdrScanner localVarAdrScanner = new LocalVarAdrScanner(context);
        localVarAdrScanner.visit(tree);

        arg.mv = MjcGenEmit.emitMethod(tree, classWriter);
        MethodVisitor mv = arg.mv;
        mv.visitCode();

        if(tree.isContructor())
        {
            ASMEmitlus.visitVarInsn(mv,ALOAD,0);// mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        }
        tree.body.scan(this, arg);
        Label startLabel = tree.body.startLabel;
        Label endLabel = tree.body.endLabel;

        //EmitUtil.visitLabel(mv,endLabel);// mv.visitLabel(endLabel);
        //emitLineNumber(mv,tree.getEndLineNumber(),startLabel);
        MjcGenEmit.emitReturn(tree,mv);

        for(int i = 0; i<methodSymbol.getParameterCount(); i++ )
        {
            DeclVarSymbol paramSymbol = methodSymbol.getDeclSymbols(i);
            paramSymbol.startLabel = startLabel;
            paramSymbol.endLabel = endLabel;
        }

        mv.visitLocalVariable("this", classSymbol.getSignature(true), null, startLabel, endLabel, 0);

        int varcount = methodSymbol.getVarCount();
        for(int i = 0; i<varcount; i++ )
        {
            DeclVarSymbol varSymbol = methodSymbol.getDeclSymbols(i);
            String sign = varSymbol.typeSymbol.getSignature(true);
            //Debuger.outln(varSymbol.name+" : "+varSymbol.adr);
            mv.visitLocalVariable(varSymbol.name, sign, null, varSymbol.startLabel, varSymbol.endLabel, varSymbol.getAdr());
        }

        EmitUtil.visitMaxs(mv);// EmitUtil.visitMaxs(mv,0,0);
        mv.visitEnd();
    }

    @Override
    public void visitBlock(JCBlock tree, EmitContext arg)
    {
        MethodVisitor mv = arg.mv;

        for(int i=0;i<tree.stats.size();i++)
        {
            JCStatement statement = tree.stats.get(i);
            emitStatement(statement,arg);
            if( tree.startLabel==null)
            {
                tree.startLabel = lineMaps.get(statement.line);
            }
        }
        Label endLabel = new Label();
        tree.endLabel = endLabel;
        EmitUtil.visitLabel(mv,endLabel);

        emitLineNumber(arg.mv,tree.endLine,endLabel);
        for(int i=0;i<tree.frame.localVars.size();i++)
        {
            DeclVarSymbol symbol = tree.frame.localVars.get(i);
            symbol.endLabel = endLabel;
        }
    }

    @Override
    public void visitExec(JCExpressionStatement tree, EmitContext arg) {
        //Debuger.outln("219 visitExec:"+tree);
        emitExpr(tree.expr,arg);
        if(tree.expr instanceof JCMethodInvocation) {
            if (!SymbolUtil.isVoid(tree.expr.getSymbol().getTypeSymbol())) {
                MethodVisitor mv = arg.mv;
                mv.visitInsn(POP);
            }
        }
    }

    public void visitIdent(JCIdent tree, EmitContext arg) {
        //Debuger.outln("218 visitIdent:"+tree);
        MethodVisitor mv = arg.mv;
        if (tree.name.equals(TokenKind.THIS.name)) {
            ASMEmitlus.visitVarInsn(mv,ALOAD,0);//mv.visitVarInsn(ALOAD, 0);
            return;
        }

        Symbol symbol = tree.getSymbol();
        if (symbol instanceof TypeSymbol) {
            return;
        }
        if (symbol instanceof MethodSymbol) {
            ASMEmitlus.visitVarInsn(mv,ALOAD,0);// mv.visitVarInsn(ALOAD, 0);
            return;
        }
        if (SymbolUtil.isDeclFiledSymbol(symbol)) {
            ASMEmitlus.visitVarInsn(mv,ALOAD,0);// mv.visitVarInsn(ALOAD, 0);
            String owner = symbol.getOwner().getSignature(false);
            String name = symbol.name;
            String returnSign = symbol.getTypeSymbol().getSignature(true);
            mv.visitFieldInsn(GETFIELD, owner, name, returnSign);
            return;
        }
        else if(symbol instanceof JavaVarSymbol)
        {
            String owner = symbol.getOwner().getSignature(false);
            String name = symbol.name;
            String returnSign = symbol.getTypeSymbol().getSignature(true);
            mv.visitFieldInsn(GETSTATIC, owner, name, returnSign);
            //mv.visitFieldInsn(GETSTATIC, "longa/domains/sql/models/JoinKind", "left", "Llonga/domains/sql/models/JoinKind;");
            return;
        }

        VarSymbol varSymbol = (VarSymbol) tree.getSymbol();
        TypeSymbol typeSymbol = varSymbol.getTypeSymbol();
        if (varSymbol instanceof DeclVarSymbol) {
            DeclVarSymbol declVarSymbol = (DeclVarSymbol) varSymbol;
            if (typeSymbol instanceof JavaClassSymbol) {
                JavaClassSymbol javaClassSymbol = (JavaClassSymbol) varSymbol.getTypeSymbol();
                int op = JVMOpCodeSelecter.load(javaClassSymbol.clazz);
                ASMEmitlus.visitVarInsn(mv,op,declVarSymbol.getAdr());// mv.visitVarInsn(op, declVarSymbol.getAdr());
            }
            else {
                ASMEmitlus.visitVarInsn(mv,ALOAD,declVarSymbol.getAdr());// mv.visitVarInsn(ALOAD, declVarSymbol.getAdr());
            }
        }
        else
            Assert.error();
    }

    @Override
    public void visitBreak(JCBreak tree, EmitContext arg) {
        MethodVisitor mv = arg.mv;
        mv.visitJumpInsn(GOTO, tree.loopTree.loopBodyEndLabel);
    }

    @Override
    public void visitContinue(JCContinue tree, EmitContext arg) {
        MethodVisitor mv = arg.mv;
        mv.visitJumpInsn(GOTO, tree.loopTree.loopBodyStartLabel);
    }

    @Override
    public void visitLiteral(JCLiteral tree, EmitContext arg) {
        MethodVisitor mv = arg.mv;

        Object value = tree.value;

        if(value instanceof String)
        {
            mv.visitLdcInsn(value);
        }
        else if(value instanceof Integer)
        {
            int ivalue =  ((Integer) value).intValue();
            ASMEmitlus.loadConstInteger(mv,ivalue);
        }
        else if(value instanceof Boolean)
        {
            boolean bvalue =  ((Boolean) value).booleanValue();
            int ivalue = bvalue?1:0;
            ASMEmitlus.loadConstInteger(mv,ivalue);
        }
        else if(value instanceof Float)
        {
            float floatValue =  ((Float) value).floatValue();
            ASMEmitlus.loadConstFloat(mv,floatValue);
        }
    }

    @Override
    public void visitAssign(JCAssign tree, EmitContext arg) {
        MethodVisitor mv = arg.mv;
        //JavaClassSymbol typeSymbol =  (JavaClassSymbol)tree.left.getSymbol().getTypeSymbol();
       /* if(tree.left instanceof JCArrayAccess)
        {
            JCArrayAccess jcArrayAccess = (JCArrayAccess)tree.left;
            emitExpr(jcArrayAccess.indexed,arg);
            emitExpr(jcArrayAccess.index,arg);
            tree.right.scan(this, arg);
            int arrayStormOp = JVMOpCodeSelecter.arrayStorm(typeSymbol.clazz);
            mv.visitInsn(arrayStormOp);
        }
        else*/ if(tree.left instanceof JCFieldAccess)
        {
            JCFieldAccess jcFieldAccess = (JCFieldAccess)tree.left;
            emitExpr(jcFieldAccess.selected,arg);
            emitExpr(tree.right,arg);
            ASMEmitlus.emitStoreField(mv,(VarSymbol) jcFieldAccess.getSymbol() );
        }
        else if(tree.left instanceof JCIdent)
        {
            if(SymbolUtil.isDeclFiledSymbol(tree.left.getSymbol()))
            {
                ASMEmitlus.visitVarInsn(mv,ALOAD,0);// mv.visitVarInsn(ALOAD, 0);
            }
            emitExpr(tree.right,arg);
            emitStoreVar(mv,(VarSymbol) tree.left.getSymbol() );
        }
        else
        {
            Assert.error();
        }
    }

    @Override
    public void visitMethodInvocation(JCMethodInvocation tree, EmitContext arg) {
        tree.meth.scan(this, arg);
        for (JCExpression jcExpression : tree.args) {
            emitExpr(jcExpression,arg);
        }

        MethodVisitor mv = arg.mv;
        MethodSymbol methodSymbol = (MethodSymbol) tree.meth.getSymbol();
        String owner = methodSymbol.owner.getSignature(false);
        String name = methodSymbol.name;
        String returnSign = methodSymbol.getSignature(true);
        boolean isInterface = methodSymbol.owner.getTypeSymbol().isInterface();
        if (methodSymbol.isStatic()) {
            mv.visitMethodInsn(INVOKESTATIC, owner, name, returnSign, isInterface);
        }
        else if(isInterface)
        {
            mv.visitMethodInsn(INVOKEINTERFACE, owner, name, returnSign, isInterface);
        }
        else {
            //Debuger.outln(new Object[]{owner,name,returnSign,isInterface});
           // try {
                mv.visitMethodInsn(INVOKEVIRTUAL, owner, name, returnSign, isInterface);
            //}catch (Exception ex){}
        }
    }

    @Override
    public void visitChain(JCChain tree, EmitContext arg) {
        Assert.error();
    }

    @Override
    public void visitBinary(JCBinary tree, EmitContext arg) {
        MethodVisitor mv = arg.mv;
        TokenKind opcode = tree.opcode;
        TypeSymbol resultType = tree.getSymbol().getTypeSymbol();
        if(SymbolUtil.isString(resultType))
        {
            StringConcatEmit stringContactEmit = new StringConcatEmit(arg.mv,tree);
            stringContactEmit.start();
            stringContactEmit.append(tree.left,this,arg);
            stringContactEmit.append(tree.right,this,arg);
            stringContactEmit.end();
            return;
        }
        else if(SymbolUtil.isBoolean(resultType))
        {
            LogicEmit logicEmit = new LogicEmit(this,tree,arg);
            if(opcode.equals(TokenKind.AND))
            {
                logicEmit.emitAND();
            }
            else if(opcode.equals(TokenKind.OR))
            {
                logicEmit.emitOR();
            }
            else if(opcode.equals(TokenKind.GT))
            {
                logicEmit.emitGT();
            }
            else if(opcode.equals(TokenKind.LT))
            {
                logicEmit.emitLT();
            }
            else if(opcode.equals(TokenKind.EQEQ))
            {
                logicEmit.emitEQ();
            }
            else if(opcode.equals(TokenKind.GTEQ))
            {
                logicEmit.emitGE();
            }
            else if(opcode.equals(TokenKind.LTEQ))
            {
                logicEmit.emitLE();
            }
            else if(opcode.equals(TokenKind.NOTEQ))
            {
                logicEmit.emitNE();
            }
            return;
        }

        emitExpr(tree.left,arg);
        emitExpr(tree.right,arg);

       // if(!(tree.getSym() instanceof VarSymbol ))
        //    Debuger.outln();
        //VarSymbol varSymbol = (VarSymbol) tree.getSym();

        JavaClassSymbol javaClassSymbol =(JavaClassSymbol)resultType; // (JavaClassSymbol)varSymbol.getTypeSymbol();
        //TokenKind opcode = tree.opcode;
        if(opcode.equals(TokenKind.ADD)) {
            int op = JVMOpCodeSelecter.add(javaClassSymbol.clazz);
            if (op == -1) {
                //Debuger.outln(op);
                op = JVMOpCodeSelecter.add(javaClassSymbol.clazz);
            }
            mv.visitInsn(op);
        }
        else if(opcode.equals(TokenKind.SUB))
        {
            int op = JVMOpCodeSelecter.sub(javaClassSymbol.clazz);
            mv.visitInsn(op);
        }
        else if(opcode.equals(TokenKind.MUL))
        {
            int op = JVMOpCodeSelecter.mul(javaClassSymbol.clazz);
            mv.visitInsn(op);
        }
        else if(opcode.equals(TokenKind.DIV))
        {
            int op = JVMOpCodeSelecter.div(javaClassSymbol.clazz);
            mv.visitInsn(op);
        }
        /*else if(opcode.equals(TokenKind.PERCENT))
        {
            int op = JVMOpCodeSelecter.rem(javaClassSymbol.clazz);
            mv.visitInsn(op);
        }*/
        /*else if(opcode.equals(TokenKind.AND))
        {
            ASMPlusEmit.and(mv);
        }
        else if(opcode.equals(TokenKind.OR))
        {
            ASMPlusEmit.or(mv);
        }*/
        /*else if(opcode.equals(TokenKind.GT))
        {
            ASMPlusEmit.gt(mv);
        }*/
        /*else if(opcode.equals(TokenKind.LT))
        {
            ASMPlusEmit.lt(mv);
        }*/
       /* else if(opcode.equals(TokenKind.EQEQ))
        {
            ASMPlusEmit.eq(mv);
        }*/
       /* else if(opcode.equals(TokenKind.GTEQ))
        {
            ASMPlusEmit.ge(mv);
        }
        else if(opcode.equals(TokenKind.LTEQ))
        {
            ASMPlusEmit.le(mv);
        }
        else if(opcode.equals(TokenKind.BANGEQ))
        {
            ASMPlusEmit.ne(mv);
        }*/
        else
        {
            Assert.error();
        }
    }
/*
    private void emitStatements(ArrayList<JCStatement> trees, EmitContext arg)
    {
        if(ArrayListUtil.nonEmpty(trees))
        {
            for(int i=0;i<trees.size();i++)
            {
                emitStatement(trees.get(i),arg);
            }
        }
    }*/

    @Override
    public void visitForLoop(JCForLoop tree, EmitContext arg) {
        MethodVisitor mv = arg.mv;

        Label startLabel = new Label();
        Label coditionLabel = new Label();
        Label endLabel = new Label();
        Label stepLabel = new Label();

        tree.loopBodyStartLabel = stepLabel;
        tree.loopBodyEndLabel = endLabel;

        /* 开始 init */
        EmitUtil.visitLabel(mv,startLabel);// mv.visitLabel(startLabel);
        if(tree.init!=null)
            tree.init.scan(this,arg);

        /* 循环条件 */
        EmitUtil.visitLabel(mv,coditionLabel);// mv.visitLabel(coditionLabel);
        if(tree.cond!=null) {
            emitExpr(tree.cond, arg);
            mv.visitJumpInsn(IFEQ, endLabel);
        }

        /* 循环体 */
        tree.body.scan(this,arg);
        /* 循环 step */
        EmitUtil.visitLabel(mv,stepLabel);// mv.visitLabel(stepLabel);
        if(tree.step!=null)
            tree.step.scan(this,arg);
        mv.visitJumpInsn(GOTO, coditionLabel);

        /* 循环结束 */
        EmitUtil.visitLabel(mv,endLabel);//mv.visitLabel(endLabel);

        //mv.visitLineNumber(18, l2);
        //mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        emitLineNumber(arg.mv,tree.line,endLabel);
        for(int i=0;i<tree.frame.localVars.size();i++)//for(String name:tree.frame.localVars.keySet())
        {
            DeclVarSymbol symbol = tree.frame.localVars.get(i);
            //symbol.startLabel = startLabel;
            symbol.endLabel = endLabel;
        }
    }

    @Override
    public void visitIf(JCIf tree, EmitContext arg) {
        MethodVisitor mv = arg.mv;
        if(tree.elsepart==null)
        {
            Label endlabel = new Label();
            tree.cond.scan(this,arg);
            mv.visitJumpInsn(IFEQ, endlabel);
           // arg.stackFrame = tree.thenStackFrame;
            tree.thenpart.scan(this,arg);
            EmitUtil.visitLabel(mv,endlabel);// mv.visitLabel(endlabel);
            //mv.visitInsn(NOP);
        }
        else
        {
            Label endlabel = new Label();
            Label elselabel = new Label();
            tree.cond.scan(this,arg);
            //ASMPlusEmit.booleanValue(mv);
            mv.visitJumpInsn(IFEQ, elselabel);

            tree.thenpart.scan(this,arg);
            mv.visitJumpInsn(GOTO, endlabel);
            EmitUtil.visitLabel(mv,elselabel);//  mv.visitLabel(elselabel);
           // arg.stackFrame = tree.thenStackFrame;
            tree.elsepart.scan(this,arg);
            EmitUtil.visitLabel(mv,endlabel);// mv.visitLabel(endlabel);
        }
       // arg.stackFrame = tree.elseStackFrame;
    }

    @Override
    public void visitWhileLoop(JCWhile tree, EmitContext arg) {
        MethodVisitor mv = arg.mv;
        Label startlabel = new Label();
        Label falselabel = new Label();
        Label bodyStartLabel = new Label();
        tree.loopBodyStartLabel = bodyStartLabel;
        tree.loopBodyEndLabel = falselabel;

        EmitUtil.visitLabel(mv,startlabel);//mv.visitLabel(startlabel);
        emitExpr(tree.cond,arg);//tree.cond.scan(this,arg);
        mv.visitJumpInsn(IFEQ, falselabel);
        EmitUtil.visitLabel(mv,bodyStartLabel);//mv.visitLabel(bodyStartLabel);
        tree.body.scan(this,arg);
        mv.visitJumpInsn(GOTO, startlabel);
        EmitUtil.visitLabel(mv,falselabel);// mv.visitLabel(falselabel);
    }

    @Override
    public void visitUnary(JCUnary tree, EmitContext arg) {
        emitExpr(tree.expr,arg);
        MethodVisitor mv = arg.mv;
        JavaClassSymbol javaClassSymbol =(JavaClassSymbol)tree.getSymbol().getTypeSymbol();
        TokenKind opcode = tree.opcode;
        if(opcode.equals(TokenKind.SUB))
        {
            int op = JVMOpCodeSelecter.neg(javaClassSymbol.clazz);
            mv.visitInsn(op);
        }
        else if(opcode.equals(TokenKind.NOT))
        {
            Label l1 = new Label();
            mv.visitJumpInsn(IFNE, l1);
            mv.visitInsn(ICONST_1);
            Label l2 = new Label();
            mv.visitJumpInsn(GOTO, l2);
            EmitUtil.visitLabel(mv,l1);// mv.visitLabel(l1);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(ICONST_0);
            EmitUtil.visitLabel(mv,l2);//mv.visitLabel(l2);
            // mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
        }
        else if(opcode.equals(TokenKind.ADD))
        {
            return;
        }
        else
        {
            Assert.error();
        }
    }

    @Override
    public void visitVarDef(JCVariableDecl tree, EmitContext arg)
    {
        DeclVarSymbol varSymbol = tree.getDeclVarSymbol();
        MethodVisitor mv = arg.mv;
        if(varSymbol.varSymbolKind == VarKind.field)
        {
            FieldVisitor fieldVisitor =  arg.classWriter.visitField(ACC_PUBLIC , varSymbol.name , varSymbol.getTypeSymbol().getSignature(true) , null, null);
            fieldVisitor.visitEnd();
            if (tree.init != null) {
                emitLineNumber(mv, tree.line);
                ASMEmitlus.visitVarInsn(mv,ALOAD,0);// mv.visitVarInsn(ALOAD, 0);
                emitExpr(tree.init,arg);
                ASMEmitlus.emitStoreField( mv , varSymbol );
            }
        }
        else {
            Label label = this.lineMaps.get(tree.line);
            varSymbol.startLabel = label;
            if (tree.init != null) {
                emitExpr(tree.init,arg);//tree.init.scan(this, arg);
                emitStoreVar(arg.mv,varSymbol);
            }
        }
    }

    private void emitStoreVar(MethodVisitor mv , VarSymbol varSymbol )
    {
        TypeSymbol typeSymbol = varSymbol.getTypeSymbol();
        int op;
        if (typeSymbol instanceof JavaClassSymbol) {
            JavaClassSymbol javaClassSymbol = (JavaClassSymbol) typeSymbol;
            op = JVMOpCodeSelecter.store(javaClassSymbol.clazz);
        }
        else if (typeSymbol instanceof JavaArrayTypeSymbol) {
            op = ASTORE;
        }
        else {
            op = ASTORE;
        }
        if(varSymbol instanceof DeclVarSymbol) {
            DeclVarSymbol declVarSymbol =(DeclVarSymbol)varSymbol;
            if(declVarSymbol.varSymbolKind == VarKind.field)
            {
                ASMEmitlus.emitStoreField(mv,declVarSymbol);
            }
            else {
                //Debuger.outln("711 declVarSymbol.adr:"+ declVarSymbol.name +" " +declVarSymbol.getAdr());
                ASMEmitlus.visitVarInsn(mv,op,declVarSymbol.getAdr());// mv.visitVarInsn(op, declVarSymbol.getAdr());
            }
        }
        else
            Assert.error();
    }

    @Override
    public void visitNewArray(JCNewList tree, EmitContext arg) {
        Label label = this.lineMaps.get(tree.line);
        tree.listVarSymbol.startLabel = label;

        int adr = tree.listVarSymbol.getAdr();
        Debuger.outln("740 visitNewArray:"+adr);
        MethodVisitor mv = arg.mv;
        mv.visitTypeInsn(NEW, "longa/lang/List");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "longa/lang/List", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, adr); // emitStoreVar(mv,tree.listVarSymbol);
        for(JCExpression item:tree.elems)
        {
            mv.visitVarInsn(ALOAD, adr);
            emitExpr(item,arg);// mv.visitLdcInsn("a");
            mv.visitMethodInsn(INVOKEVIRTUAL, "longa/lang/List", "add", "(Ljava/lang/Object;)V", false);
        }
        mv.visitVarInsn(ASTORE, adr);
    }

    @Override
    public void visitNewClass(JCNewClass tree, EmitContext arg) {
        MethodVisitor mv = arg.mv;
        MethodSymbol initSymbol =tree.constructorSymbol;
        String sign = initSymbol.returnTypeSymbol.getSignature(false);
        mv.visitTypeInsn(NEW,sign );
        mv.visitInsn(DUP);
        for (JCExpression jcExpression : tree.args) {
            emitExpr(jcExpression,arg);
        }
        String msign = initSymbol.getSignature(true);
        mv.visitMethodInsn(INVOKESPECIAL, initSymbol.owner.getSignature(false),"<init>",msign , false);
    }

    @Override
    public void visitParens(JCParens tree, EmitContext arg) {
        emitExpr(tree.expr,arg);
    }

    @Override
    public void visitReturn(JCReturn tree, EmitContext arg) {
        MethodVisitor mv = arg.mv;
        if(tree.expr!=null)
            emitExpr(tree.expr,arg);

        mv.visitJumpInsn(GOTO, tree.declMethodSymbol.endLabel);
    }

    @Override
    public void visitSelect(JCFieldAccess tree, EmitContext arg) {
        //Debuger.outln("816 emit visitSelect:"+tree+" "+ tree.treeIndexCurrent+ " , "+tree.getSymbol());
        MethodVisitor mv = arg.mv;
        emitExpr(tree.selected,arg);
        Symbol symbol2 = tree.getSymbol();
        String owner = symbol2.getOwner().getSignature(false);
        String name = symbol2.name;
        String returnSign = symbol2.getTypeSymbol().getSignature(true);
        if (symbol2 instanceof VarSymbol) {
            TypeSymbol varType = tree.selected.getTypeSymbol();
            if(varType instanceof JavaArrayTypeSymbol)
            {
                mv.visitInsn(ARRAYLENGTH); //调用数组类型的length属性
            }
            else
            {
                VarSymbol varSymbol =(VarSymbol) symbol2;
                if (varSymbol.isStatic()) {
                    mv.visitFieldInsn(GETSTATIC, owner, name, returnSign);
                }
                else {
                    mv.visitFieldInsn(GETFIELD, owner, name, returnSign);
                }
            }
        }
    }

    public void emitStatement(JCStatement statement, EmitContext arg)
    {
        MethodVisitor mv = arg.mv;
        emitLineNumber(mv,statement.line);
        statement.scan(this,arg);
    }
/*
    public void emitExpr2(JCExpression jcExpression, EmitContext arg) {
        if (jcExpression == null) return;
        jcExpression.scan(this, arg);
        MethodVisitor mv = arg.mv;
        if (jcExpression.requireConvertTo == null) return;

        TypeSymbol toType = jcExpression.requireConvertTo;
        TypeSymbol curType = jcExpression.getTypeSymbol();
        if(curType.equals(toType)) return;
        if(curType.equalType(toType)) return;
        if(!toType.isAssignableFrom(curType)) return;

        if ((SymbolUtil.isObject(toType) && (curType instanceof JavaArrayTypeSymbol)))
        {
             return;
        }

        if ((SymbolUtil.isObject(toType) && SymbolUtil.isPrimitive(curType))) //装箱
        {
            if (BoxEmit.box(mv, toType, curType))
                return;
        }

        if (SymbolUtil.isPrimitiveBox(toType) && SymbolUtil.isPrimitive(curType)) //拆箱
        {
            if (BoxEmit.box(mv, toType, curType))
                return;
        }

        if (SymbolUtil.isPrimitive(toType) && SymbolUtil.isPrimitiveBox(curType)) //拆箱
        {
            if (BoxEmit.unbox(mv, toType, curType))
                return;
        }

        Assert.error();
    }
*/

    /** 生成表达式 */
    public void emitExpr(JCExpression jcExpression, EmitContext arg) {
        if (jcExpression == null) return;
        jcExpression.scan(this, arg);
        if (jcExpression.requireConvertTo == null) return;
        if (!(jcExpression.requireConvertTo instanceof JavaClassSymbol)) return;
        emitBoxOrUnBox(jcExpression,arg);
    }

    private void emitBoxOrUnBox(JCExpression jcExpression, EmitContext arg)
    {
        MethodVisitor mv = arg.mv;
        TypeSymbol toType = jcExpression.requireConvertTo;
        TypeSymbol curType = jcExpression.getTypeSymbol();

        if(curType.equals(toType)) return;
        if(curType.equalType(toType)) return;
        if(toType.isAssignableFrom(curType))
            return;

        if ((SymbolUtil.isObject(toType) && (curType instanceof JavaArrayTypeSymbol)))
        {
            return;
        }

        if ((SymbolUtil.isObject(toType) && SymbolUtil.isPrimitive(curType))) //装箱
        {
            if (BoxEmit.box(mv, toType, curType))
                return;
        }

        if (SymbolUtil.isPrimitiveBox(toType) && SymbolUtil.isPrimitive(curType)) //装箱
        {
            if (BoxEmit.box(mv, toType, curType))
                return;
        }

        if (SymbolUtil.isPrimitive(toType) && SymbolUtil.isPrimitiveBox(curType)) //拆箱
        {
            if (BoxEmit.unbox(mv, toType, curType))
                return;
        }

        Assert.error();
    }

    HashMap<Integer,Label> lineMaps = new HashMap<>();

    private void emitLineNumber(MethodVisitor mv ,int line)
    {
        if(!lineMaps.containsKey(line)) {
            Label lineLabel = new Label();
            EmitUtil.visitLabel(mv,lineLabel);//  mv.visitLabel(lineLabel);
            emitLineNumber(mv, line, lineLabel);
            lineMaps.put(line,lineLabel);
            //Debuger.outln("869 emitLineNumber:"+line);
        }
    }

    private void emitLineNumber(MethodVisitor mv ,int line, Label lineLabel)
    {
        if (line == SourceLog.NOPOS)
            return;

        //if(line==23)
        //    Debuger.outln("emitLineNumber:"+line);
        mv.visitLineNumber(line, lineLabel);
       // Debuger.outln("711 emitLineNumber:"+line+" "+lineLabel +" : ");
    }

}
