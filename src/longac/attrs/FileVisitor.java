package longac.attrs;

import longac.diagnostics.SimpleLog;
import longac.symbols.SourceFileSymbol;
import longac.symbols.PackageSymbol;
import longac.symbols.ProjectSymbol;
import longac.trees.JCFileTree;
import longac.trees.JCPackage;
import longac.utils.CompileContext;
import longac.visitors.TreeUtil;

import java.util.ArrayList;

public class FileVisitor {
    SimpleLog log;
    CompileContext context;

    public FileVisitor(CompileContext context) {
        this.context = context;
        log = context.log;
    }

    public void visit(ArrayList<JCFileTree> trees)
    {
        for (JCFileTree tree:trees)
        {
            visit(tree);
        }
    }

    public void visit(JCFileTree tree)
    {
        SourceFileSymbol compilationUnitSymbol = new SourceFileSymbol(tree.log.sourceFile, context.projectSymbol);
        tree.setSymbol( compilationUnitSymbol);
        JCPackage jcPackageDecl = tree.getPackage();
        if (jcPackageDecl != null)
        {
            visitPackageDef(jcPackageDecl,compilationUnitSymbol);
        }
        else
        {
            createPackageSymbol(compilationUnitSymbol,"");
        }
    }

    private PackageSymbol createPackageSymbol(SourceFileSymbol compilationUnitSymbol, String packageName)
    {
        ProjectSymbol projectSymbol = compilationUnitSymbol.getProjectSymbol();
        PackageSymbol packageSymbol = new PackageSymbol(packageName,projectSymbol);
        packageSymbol.owner = projectSymbol;
        compilationUnitSymbol.owner = packageSymbol;
        compilationUnitSymbol.packageSymbol = packageSymbol;
        projectSymbol.packageSymbolMap.put(packageName,packageSymbol);
        return packageSymbol;
    }

    public void visitPackageDef(JCPackage jcPackageDecl, SourceFileSymbol compilationUnitSymbol)
    {
        if(jcPackageDecl.getSymbol()==null)
        {
            String packageName = TreeUtil.fullName(jcPackageDecl.packageName);
            PackageSymbol  packageSymbol = createPackageSymbol(compilationUnitSymbol,packageName);
            jcPackageDecl.packge = packageSymbol;
        }

       /* CompilationUnitSymbol compilationUnitSymbol =( CompilationUnitSymbol)arg;
        String packageName = TreeInfo.fullName(jcPackageDecl.pid);
        PackageSymbol packageSymbol =compilationUnitSymbol.packageSymbol; //((ProjectSymbol)compilationUnitSymbol.owner).packageSymbolMap.get(packageName);
        if(packageSymbol==null)
        {
            packageSymbol = createPackageSymbol(compilationUnitSymbol,packageName);
        }
        jcPackageDecl.packge = packageSymbol;*/
    }
}
