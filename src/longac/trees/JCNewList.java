package longac.trees;

import longac.lgac.makeModels.LongaListAddModel;
import longac.lgac.makeModels.LongaListNewMakeModel;
import longac.symbols.DeclVarSymbol;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

import java.util.ArrayList;

/** 创建数组实例表达式 */
public class JCNewList extends JCExpression
{
    public ArrayList<JCExpression> elems;

    public JCNewList(ArrayList<JCExpression> elems)
    {
        this.elems = elems;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitNewArray(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateNewArray(this, d);
    }

    public DeclVarSymbol listVarSymbol;

    public LongaListNewMakeModel listNewModel;

    public ArrayList<LongaListAddModel> listAddModels = new ArrayList<>();
}
