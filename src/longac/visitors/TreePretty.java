package longac.visitors;

import java.io.*;
import java.util.ArrayList;

import longac.trees.JCMethodDecl;
import longac.trees.JCTree;
import tools.jvmx.Convert;
import tools.jvmx.NamesTexts;
import longac.trees.*;

public class TreePretty extends TreeScanner<Object>  {

    public TreePretty(Writer out, boolean sourceOutput) {
        this.out = out;
        this.sourceOutput = sourceOutput;
    }

    private final boolean sourceOutput;

    /** The output stream on which trees are printed.
     */
    Writer out;

    /** Indentation width (can be reassigned from outside).
     */
    public int width = 4;

    /** The current left margin.
     */
    int lmargin = 0;

    /** The enclosing class name.
     */
    String enclClassName;

    /** A table mapping trees to their documentation comments
     *  (can be null)
     */
   // DocCommentTable docComments = null;

    /**
     * A string sequence to be used when Pretty output should be constrained
     * to fit into a given size
     */
    private final static String trimSequence = "[...]";

    /**
     * Max number of chars to be generated when output should fit into a single line
     */
    private final static int PREFERRED_LENGTH = 20;

    /** Align code to be indented to left margin.
     */
    void align() throws IOException {
        for (int i = 0; i < lmargin; i++) out.write(" ");
    }

    /** Increase left margin by indentation width.
     */
    void indent() {
        lmargin = lmargin + width;
    }

    /** Decrease left margin by indentation width.
     */
    void undent() {
        lmargin = lmargin - width;
    }

    /** Enter a new precedence level. Emit a `(' if new precedence level
     *  is less than precedence level so far.
     *  @param contextPrec    The precedence level in force so far.
     *  @param ownPrec        The new precedence level.
     */
    void open(int contextPrec, int ownPrec) throws IOException {
        if (ownPrec < contextPrec) out.write("(");
    }

    /** Leave precedence level. Emit a `(' if inner precedence level
     *  is less than precedence level we revert to.
     *  @param contextPrec    The precedence level we revert to.
     *  @param ownPrec        The inner precedence level.
     */
    void close(int contextPrec, int ownPrec) throws IOException {
        if (ownPrec < contextPrec) out.write(")");
    }

    /** Print string, replacing all non-ascii character with unicode escapes.
     */
    public void print(Object s) throws IOException {
        out.write(Convert.escapeUnicode(s.toString()));
    }

    /** Print new line.
     */
    public void println() throws IOException {
        out.write(lineSep);
    }

    public static String toSimpleString(JCTree tree) {
        return toSimpleString(tree, PREFERRED_LENGTH);
    }

    public static String toSimpleString(JCTree tree, int maxLength) {
        StringWriter s = new StringWriter();
        try {
            new TreePretty(s, false).printTree(tree);
        }
        catch (IOException e) {
            // should never happen, because StringWriter is defined
            // never to throw any IOExceptions
            throw new AssertionError(e);
        }
        //we need to (i) replace all line terminators with a space and (ii) remove
        //occurrences of 'missing' in the Pretty output (generated when types are missing)
        String res = s.toString().trim().replaceAll("\\s+", " ").replaceAll("/\\*missing\\*/", "");
        if (res.length() < maxLength) {
            return res;
        } else {
            int head = (maxLength - trimSequence.length()) * 2 / 3;
            int tail = maxLength - trimSequence.length() - head;
            return res.substring(0, head) + trimSequence + res.substring(res.length() - tail);
        }
    }

    String lineSep = System.getProperty("line.separator");

    /**************************************************************************
     * Traversal methods
     *************************************************************************/

    /** Exception to propogate IOException through visitXXX methods */
    public static class UncheckedIOException extends Error {
        UncheckedIOException(IOException e) {
            super(e.getMessage(), e);
        }
    }

    /** Visitor argument: the current precedence level.
     */
    int prec;

    /** Visitor method: print expression tree.
     *  @param prec  The current precedence level.
     */
    public void printTree(JCTree tree, int prec) throws IOException {
        int prevPrec = this.prec;
        try {
            this.prec = prec;
            if (tree == null) print("/*missing*/");
            else {
                tree.scan(this,null);
            }
        } catch (UncheckedIOException ex) {
            IOException e = new IOException(ex.getMessage());
            e.initCause(ex);
            throw e;
        } finally {
            this.prec = prevPrec;
        }
    }

    /** Derived visitor method: print expression tree at minimum precedence level
     *  for expression.
     */
    public void printTree(JCTree tree) throws IOException {
        printTree(tree, TreeUtil.noPrec);
    }

    /** Derived visitor method: print statement tree.
     */
    public void printStat(JCTree tree) throws IOException {
        printTree(tree, TreeUtil.notExpression);
    }

    public <T extends JCTree> void printExprs(ArrayList<T> trees, String sep) throws IOException {
        if (trees.size()>0) {
            printTree(trees.get(0));
            for (int i=1;i<trees.size();i++) {
                print(sep);
                printTree(trees.get(i));
            }
        }
    }

    public <T extends JCTree> void printExprs(ArrayList<T> trees) throws IOException {
        printExprs(trees, ", ");
    }

    /** Derived visitor method: print pattern.
     */

    public void printPattern(JCTree tree) throws IOException {
        printTree(tree);
    }

    /** Derived visitor method: print list of statements, each on a separate line.
     */
    public void printStats(ArrayList<? extends JCTree> trees) throws IOException {
        for(int i=0;i<trees.size();i++)
        {
            align();
            printStat(trees.get(i));
            println();
        }
    }

    /** Print a block.
     */
    public void printBlock(ArrayList<? extends JCTree> stats) throws IOException {
        print("{");
        println();
        indent();
        printStats(stats);
        undent();
        align();
        print("}");
    }


    /** Print unit consisting of package clause and import statements in toplevel,
     *  followed by class definition. if class definition == null,
     *  print all definitions in toplevel.
     *  @param tree     The toplevel tree
     *  @param cdef     The class definition, which is assumed to be part of the
     *                  toplevel tree.
     */
    public void printUnit(JCFileTree tree, JCClassDecl cdef) throws IOException {
        boolean firstImport = true;
        if(tree.defs!=null && tree.defs.size()>0)
        for(int i=0;i<tree.defs.size();i++)
        {
            JCTree l= tree.defs.get(i);

            if (l instanceof JCImport )
            {
                JCImport imp = (JCImport)l;
                String name = TreeUtil.name(imp.qualid);
                if (name == NamesTexts.star ||
                        cdef == null // ||
                        //isUsed(TreeUtil.symbol(imp.qualid), cdef)
                )
                {
                    if (firstImport) {
                        firstImport = false;
                        println();
                    }
                    printStat(imp);
                }
            } else {
                printStat(l);
            }
        }
        if (cdef != null) {
            printStat(cdef);
            println();
        }
    }

    /**************************************************************************
     * Visitor methods
     *************************************************************************/

    public void visitCompilationUnit(JCFileTree tree, Object arg) {
        try {
            printUnit(tree, null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitPackageDef(JCPackage tree, Object arg) {
        try {
            //printDocComment(tree);
           // printAnnotations(tree.annotations);
            if (tree.packageName != null) {
                print("package ");
                printTree(tree.packageName);
                print(";");
                println();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitImport(JCImport tree, Object arg) {
        try {
            print("import ");
            printTree(tree.qualid);
            if(tree.isPackageStar)
            {
                print(".");
                print("*");
            }
            print(";");
            println();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitClassDef(JCClassDecl tree, Object arg) {
        try {
            println();
            align();
            String enclClassNamePrev = enclClassName;
            enclClassName = tree.name;
          /*  if ((tree.mods.flags & INTERFACE) != 0) {
                print("interface " + tree.name);*/
               // printTypeParameters(tree.typarams);
               /* if (tree.implementing!=null && tree.implementing.size()>0)//.nonEmpty())
                {
                    print(" extends ");
                    printExprs(tree.implementing);
                }*/
            /*} else*/ {
               // if ((tree.mods.flags & ENUM) != 0)
               //     print("enum " + tree.name);
               // else
                    print("class " + tree.name);
               // printTypeParameters(tree.typarams);
                /*if (tree.extending != null) {
                    print(" extends ");
                    printExpr(tree.extending);
                }*/
              /*  if (tree.implementing!=null && tree.implementing.size()>0)//if (tree.implementing.nonEmpty())
                {
                    print(" implements ");
                    printExprs(tree.implementing);
                }*/
            }
            print(" ");
           /* if ((tree.mods.flags & ENUM) != 0) {
                printEnumBody(tree.defs);
            } else */
            {
                printBlock(tree.defs);
            }
            enclClassName = enclClassNamePrev;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitMethodDef(JCMethodDecl tree, Object arg) {
        try {
            // when producing source output, omit anonymous constructors
            if (tree.name == NamesTexts.init &&
                    enclClassName == null &&
                    sourceOutput) return;
            println(); align();
            visitMethodDefHead(tree);
            /*
            //printDocComment(tree);
          //  printExpr(tree.mods);
            //printTypeParameters(tree.typarams);
            if (tree.name == NamesTexts.init) {
                print(enclClassName != null ? enclClassName : tree.name);
            } else {
                printTree(tree.retTypeExpr);
                print(" " + tree.name);
            }
            print("(");
           /* if (tree.recvparam!=null) {
                printExpr(tree.recvparam);
                if (tree.params.size() > 0) {
                    print(", ");
                }
            }*//*
            printExprs(tree.params);
            print(")");*/
            /*if (tree.thrown.nonEmpty()) {
                print(" throws ");
                printExprs(tree.thrown);
            }*/
           /* if (tree.defaultValue != null) {
                print(" default ");
                printExpr(tree.defaultValue);
            }*/
            if (tree.body != null) {
                print(" ");
                printStat(tree.body);
            } else {
                print(";");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitMethodDefHead(JCMethodDecl tree) {
        try {
            // when producing source output, omit anonymous constructors
            if (tree.name == NamesTexts.init &&
                    enclClassName == null &&
                    sourceOutput) return;
            println(); align();
            if (tree.name == NamesTexts.init) {
                print(enclClassName != null ? enclClassName : tree.name);
            } else {
                printTree(tree.retTypeExpr);
                print(" " + tree.name);
            }
            print("(");
            printExprs(tree.params);
            print(")");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitVarDef(JCVariableDecl tree, Object arg) {
        try {
            printTree(tree.vartype);
            print(" " + tree.name);
            if (tree.init != null) {
                print(" = ");
                printTree(tree.init);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitBlock(JCBlock tree, Object arg) {
        try {
           // printFlags(tree.flags);
            printBlock(tree.stats);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitWhileLoop(JCWhile tree, Object arg) {
        try {
            print("while ");
          //  if (tree.cond.hasTag(PARENS)) {
          //      printExpr(tree.cond);
         //   } else {
                print("(");
                printTree(tree.cond);
                print(")");
         //   }
            print(" ");
            printStat(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public void visitChain(JCChain tree, Object arg)
    {
        try {
            //printTree(tree.master);
            print(":");
            print("{");
            printExprs( tree.nodes);
            print("}");
            print(" ");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitForLoop(JCForLoop tree, Object arg) {
        try {
            print("for (");
            if(tree.init!=null)
                tree.init.scan(this,arg);

            if (tree.cond != null) printTree(tree.cond);
            print("; ");
            if(tree.step!=null)
                tree.step.scan(this,arg);
            //printExprs(tree.step);
            print(") ");
            printStat(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
/*
    public void visitForeachLoop(JCEnhancedForLoop tree, Object arg) {
        try {
            print("for (");
            printExpr(tree.var);
            print(" : ");
            printExpr(tree.expr);
            print(") ");
            printStat(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/

    /*
    public void visitBindingPattern(JCBindingPattern patt, Object arg) {
        try {
            printExpr(patt.vartype);
            print(" ");
            print(patt.name);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/

/*
    public void visitTry(JCTry tree, Object arg) {
        try {
            print("try ");
            if (tree.resources.nonEmpty()) {
                print("(");
                boolean first = true;
                for (JCTree var : tree.resources) {
                    if (!first) {
                        println();
                        indent();
                    }
                    printStat(var);
                    first = false;
                }
                print(") ");
            }
            printStat(tree.body);
            for (List<JCCatch> l = tree.catchers; l.nonEmpty(); l = l.tail) {
                printStat(l.head);
            }
            if (tree.finalizer != null) {
                print(" finally ");
                printStat(tree.finalizer);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/

/*
    public void visitCatch(JCCatch tree, Object arg) {
        try {
            print(" catch (");
            printExpr(tree.param);
            print(") ");
            printStat(tree.body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/

    /*public void visitConditional(JCConditional tree, Object arg) {
        try {
            open(prec, TreeUtil.condPrec);
            printExpr(tree.cond, TreeUtil.condPrec + 1);
            print(" ? ");
            printExpr(tree.truepart);
            print(" : ");
            printExpr(tree.falsepart, TreeUtil.condPrec);
            close(prec, TreeUtil.condPrec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/

    public void visitIf(JCIf tree, Object arg) {
        try {
            print("if ");
            //if (tree.cond.hasTag(PARENS)) {
            //    printExpr(tree.cond);
           // } else {
                print("(");
                printTree(tree.cond);
                print(")");
          //  }
            print(" ");
            printStat(tree.thenpart);
            if (tree.elsepart != null) {
                print(" else ");
                printStat(tree.elsepart);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitExec(JCExpressionStatement tree, Object arg) {
        try {
            printTree(tree.expr);
            if (prec == TreeUtil.notExpression) print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitBreak(JCBreak tree, Object arg) {
        try {
            print("break;");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitContinue(JCContinue tree, Object arg) {
        try {
            print("continue");
           // if (tree.label != null) print(" " + tree.label);
            print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitReturn(JCReturn tree, Object arg) {
        try {
            print("return");
            if (tree.expr != null) {
                print(" ");
                printTree(tree.expr);
            }
            print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
/*
    public void visitThrow(JCThrow tree, Object arg) {
        try {
            print("throw ");
            printExpr(tree.expr);
            print(";");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/

    public void visitMethodInvocation(JCMethodInvocation tree, Object arg) {
        try {
           /* if (!tree.typeargs.isEmpty()) {
                if (tree.meth.hasTag(SELECT)) {
                    JCFieldAccess left = (JCFieldAccess)tree.meth;
                    printExpr(left.selected);
                    print(".<");
                    printExprs(tree.typeargs);
                    print(">" + left.name);
                } else {
                    print("<");
                    printExprs(tree.typeargs);
                    print(">");
                    printExpr(tree.meth);
                }
            }
            else*/ {
                printTree(tree.meth);
            }
            print("(");
            printExprs(tree.args);
            print(")");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitNewClass(JCNewClass tree, Object arg) {
        try {
           /* if (tree.encl != null) {
                printExpr(tree.encl);
                print(".");
            }*/
            print("new ");
           /* if (!tree.typeargs.isEmpty()) {
                print("<");
                printExprs(tree.typeargs);
                print(">");
            }*/
           /* if (tree.def != null && tree.def.mods.getAnnotations().nonEmpty()) {
                printTypeAnnotations(tree.def.mods.getAnnotations());
            }*/
            printTree(tree.clazz);
            print("(");
            printExprs(tree.args);
            print(")");
            /*if (tree.def != null) {
                String enclClassNamePrev = enclClassName;
                enclClassName = tree.def.name;
                        tree.def.name != null ? tree.def.name :
                            tree.type != null && tree.type.tsym.name != tree.type.tsym.name.table.names.empty
                                ? tree.type.tsym.name : null;
                if ((tree.def.mods.flags & Flags.ENUM) != 0) print("enum");
                printBlock(tree.def.defs);
                enclClassName = enclClassNamePrev;
            }*/
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitNewArray(JCNewList tree, Object arg) {
        try {

            if (tree.elems != null) {
                print("{");
                printExprs(tree.elems);
                print("}");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitParens(JCParens tree, Object arg) {
        try {
            print("(");
            printTree(tree.expr);
            print(")");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitAssign(JCAssign tree, Object arg) {
        try {
            open(prec, TreeUtil.assignPrec);
            printTree(tree.left, TreeUtil.assignPrec + 1);
            print(" = ");
            printTree(tree.right, TreeUtil.assignPrec);
            close(prec, TreeUtil.assignPrec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
/*
    public String operatorName(JCTreeTag tag) {
        switch(tag) {
            case POS:     return "+";
            case NEG:     return "-";
            case NOT:     return "!";
           // case COMPL:   return "~";
            case PREINC:  return "++";
            case PREDEC:  return "--";
            case POSTINC: return "++";
            case POSTDEC: return "--";
            case NULLCHK: return "<*nullchk*>";
            case OR:      return "||";
            case AND:     return "&&";
            case EQ:      return "==";
            case NE:      return "!=";
            case LT:      return "<";
            case GT:      return ">";
            case LE:      return "<=";
            case GE:      return ">=";
           // case BITOR:   return "|";
           // case BITXOR:  return "^";
           // case BITAND:  return "&";
           // case SL:      return "<<";
          //  case SR:      return ">>";
            //case USR:     return ">>>";
            case PLUS:    return "+";
            case MINUS:   return "-";
            case MUL:     return "*";
            case DIV:     return "/";
            case MOD:     return "%";
            default: throw new Error();
        }
    }*/

    public void visitUnary(JCUnary tree, Object arg) {
        try {
            print(tree.opcode.name);
            printTree(tree.expr);

           /* int ownprec = TreeUtil.opPrec(tree.getTag());
            String opname = operatorName(tree.getTag());
            open(prec, ownprec);
            if (!tree.getTag().isPostUnaryOp()) {
                print(opname);
                printExpr(tree.arg, ownprec);
            } else {
                printExpr(tree.arg, ownprec);
                print(opname);
            }
            close(prec, ownprec);*/
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitBinary(JCBinary tree, Object arg) {
        try {
            printTree(tree.left);
            print(tree.opcode.name);
            printTree(tree.right);
            //int ownprec = TreeUtil.opPrec(tree.getTag());
           // String opname = operatorName(tree.getTag());
           // open(prec, ownprec);
           /* printExpr(tree.lhs, ownprec);
            print(" " + opname + " ");
            printExpr(tree.rhs, ownprec + 1);
            close(prec, ownprec);*/
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
/*
    public void visitTypeCast(JCTypeCast tree, Object arg) {
        try {
            open(prec, TreeUtil.prefixPrec);
            print("(");
            printExpr(tree.clazz);
            print(")");
            printExpr(tree.expr, TreeUtil.prefixPrec);
            close(prec, TreeUtil.prefixPrec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/

/*
    public void visitTypeTest(JCInstanceOf tree, Object arg) {
        try {
            open(prec, TreeUtil.ordPrec);
            printExpr(tree.expr, TreeUtil.ordPrec);
            print(" instanceof ");
            if (tree.pattern instanceof JCPattern) {
                printPattern(tree.pattern);
            } else {
                printExpr(tree.getType(), TreeUtil.ordPrec + 1);
            }
            close(prec, TreeUtil.ordPrec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/
/*
    public void visitIndexed(JCArrayAccess tree, Object arg) {
        try {
            printTree(tree.indexed, TreeUtil.postfixPrec);
            print("[");
            printTree(tree.index);
            print("]");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/

    public void visitSelect(JCFieldAccess tree, Object arg) {
        try {
            printTree(tree.selected, TreeUtil.postfixPrec);
            print("." + tree.name);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
/*
    public void visitReference(JCMemberReference tree, Object arg) {
        try {
            printExpr(tree.expr);
            print("::");
            if (tree.typeargs != null) {
                print("<");
                printExprs(tree.typeargs);
                print(">");
            }
            print(tree.getMode() == ReferenceMode.INVOKE ? tree.name : "new");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/

    public void visitIdent(JCIdent tree, Object arg) {
        try {
            print(tree.name);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitLiteral(JCLiteral tree , Object arg) {

        try {
            /*if(tree.literalKind== TokenKind.NULL) {
                print("null");
                return;
            }
            else*/ if(tree.value!=null)
            {
                print(tree.value);
            }

           // Class<?> type = tree.type;
            /*
            if(boolean.class.equals(type)) {
                print(((Number)tree.value).intValue() == 1 ? "true" : "false");
            }
            else if(char.class.equals(type)) {
                print("\'" +
                        Convert.quote(
                                String.valueOf((char)((Number)tree.value).intValue())) +
                        "\'");
            }
            else if(int.class.equals(type)) {
                print(tree.value.toString());
            }
            else if(long.class.equals(type)) {
                print(tree.value + "L");
            }
            else if(float.class.equals(type)) {
                print(tree.value + "F");
            }
            else if(double.class.equals(type)) {
                print(tree.value.toString());
            }
            else if(String.class.equals(type)) {
                print("\"" + Convert.quote(tree.value.toString()) + "\"");
            }
            else
            {
                Assert.error();
            }*/
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeIdent(JCPrimitiveTypeTree tree, Object arg) {
        try {
            switch(tree.kind)
            {
                case INT:
                    print("int");
                    break;
                case BOOLEAN:
                    print("boolean");
                    break;
                case VOID:
                    print("void");
                    break;
                default:
                    print("<error>");
                    break;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
/*
    public void visitTypeArray(JCArrayTypeTree tree, Object arg) {
        try {
            printBaseElementType(tree);
            printBrackets(tree, arg);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }*/

    // Prints the inner element type of a nested array
    private void printBaseElementType(JCTree tree) throws IOException {
        printTree(TreeUtil.innermostType(tree));//, false));
    }

    // prints the brackets of a nested array in reverse order
    // tree is either JCArrayTypeTree or JCAnnotatedTypeTree
    private void printBrackets(JCTree tree, Object arg) throws IOException {
        JCTree elem = tree;
        while (true) {
            /*if (elem.hasTag(ANNOTATED_TYPE)) {
                JCAnnotatedType atype = (JCAnnotatedType) elem;
                elem = atype.underlyingType;
                if (elem.hasTag(TYPEARRAY)) {
                   // print(' ');
                   // printTypeAnnotations(atype.annotations);
                }
            }*/
           // if (elem.hasTag(TYPEARRAY)) {
                print("[]");
             //   elem = ((JCArrayTypeTree)elem).elemtype;
            break;
            //} else {
           //     break;
          //  }
        }
    }


    @Override
    public void visitErroneous(JCErroneous tree, Object arg) {
        try {
            print("(ERROR)");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitTree(JCTree tree, Object arg) {
        try {
            print("(UNKNOWN: " + tree.getClass().getSimpleName() + ")");
            println();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
