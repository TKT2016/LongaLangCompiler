package longac._core;

import longac.utils.CompileContext;
import longac.trees.JCFileTree;
import longac.trees.JCTree;
import longac.optimizers.*;

public class Optimizer {
    CompileContext context;

    public Optimizer( CompileContext context )
    {
        this.context = context;
    }

    public JCTree translate(JCFileTree tree) {
        //return tree;

        JCFileTree treetemp = tree;
        ReturnAfterOptimizer returnAfterOptimizer = new ReturnAfterOptimizer();
        treetemp = returnAfterOptimizer.translate(treetemp);

        ConstValueOptimizer constOptimizer = new ConstValueOptimizer(context);
        treetemp = constOptimizer.translate(treetemp);

        //AssignOptimizer assignOptimizer = new AssignOptimizer();
       // treetemp = assignOptimizer.translate(treetemp);

        DeadCodeOptimizer deadCodeOptimizer = new DeadCodeOptimizer();
        treetemp = deadCodeOptimizer.translate(treetemp);

        return treetemp;
    }

}
