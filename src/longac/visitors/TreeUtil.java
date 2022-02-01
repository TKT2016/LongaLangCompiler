package longac.visitors;
import longac.trees.*;
public class TreeUtil {

    public static final int
            notExpression = -1,   // not an expression
            noPrec = 0,           // no enclosing expression
            assignPrec = 1,
    postfixPrec = 15;
    /*
    public static boolean isExpressionStatement(JCExpression tree) {
        return (tree instanceof JCAssign)
                 || (tree instanceof JCMethodInvocation)
                || (tree instanceof JCNewClass)
                //|| (tree instanceof JCErroneous)
                ;
    }*/

    public static String fullName(JCTree tree) {
        tree = skipParens(tree);
        if (tree instanceof JCIdent)
            return ((JCIdent) tree).name;
        else if (tree instanceof JCFieldAccess) {
            String sname = fullName(((JCFieldAccess) tree).selected);
            return sname == null ? null : name(tree) + "." + sname;// sname.append('.', name(tree))//sname.append('.', name(tree))
        }
        return null;
    }

    public static JCTree skipParens(JCTree tree) {
        if (tree instanceof JCParens)// .hasTag(PARENS))
            return skipParens((JCParens)tree);
        else
            return tree;
    }

    public static String name(JCTree tree) {
        if(tree instanceof JCIdent)
            return ((JCIdent) tree).name;
        else if(tree instanceof JCFieldAccess)
            return ((JCFieldAccess) tree).name;
        return null;
    }

    public static JCTree innermostType(JCTree type /*, boolean skipAnnos*/) {
      //  JCTree lastAnnotatedType = null;
        JCTree cur = type;
        loop: while (true) {
           /*if(cur instanceof JCArrayTypeTree)
            {
               // lastAnnotatedType = null;
                cur = ((JCArrayTypeTree)cur).elemtype;
                break;
            }
            else*/
                break loop;
           /* switch (cur.getTag()) {
                case TYPEARRAY:
                    lastAnnotatedType = null;
                    cur = ((JCArrayTypeTree)cur).elemtype;
                    break;
                default:
                    break loop;
            }*/
        }
        return cur;
        /*
        if (!skipAnnos && lastAnnotatedType!=null) {
            return lastAnnotatedType;
        } else {
            return cur;
        }*/
    }
}
