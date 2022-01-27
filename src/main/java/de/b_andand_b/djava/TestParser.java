package de.b_andand_b.djava;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * Some code that uses JavaParser.
 */
public class TestParser {
    public static void main(String[] args) {
        /*

        Getting started with javaparser:
        https://tomassetti.me/getting-started-with-javaparser-analyzing-java-code-programmatically/#
        Official website:
        https://javaparser.org/

         */
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());  // JavaParser has a minimal logging class that normally logs nothing. Let's ask it to write to standard out
        Log.info("------------------------------");
        CompilationUnit cu = null;
        try {
            // ways to get src/main/resources/file.ext:
            // (relative to package) - getClass().getClassLoader().getResourceAsStream("file.ext");
            // (as above; for static methods) - TheClass.class.getClassLoader().getResourceAsStream("file.ext");
            // ("/" for starting at root resource folder) - getClass().getResourceAsStream("/file.ext");
            InputStream is = TestParser.class.getClassLoader().getResourceAsStream("x.djava");


            // hopes and dreams shattered
            :(
            // parser will keywords im original



            cu = StaticJavaParser.parse(is);//CodeGenerationUtils.mavenModuleRoot(TestParser.class).resolve("src/main/resources").resolve("test_code2.java"));
                    //"D:\\Users\\Arthur\\Dokumente\\test_javaparser1.java"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(cu);
        test3(cu);
    }

    static void test3(CompilationUnit cu) {
        NodeIterator ni = new NodeIterator(node -> {
            String s = node.getClass().getName();
            if (node.getChildNodes().isEmpty())
                System.out.printf("[%-23s] %s%n", s.substring(s.indexOf(".ast.")+5), node);
            if (node.toString().equals("String") && node.getParentNode().isPresent()) {
                Node n = node.getParentNode().get();
                System.out.println("     " + n.getClass().getName());
            }
            return true;
        });
        ni.explore(cu);
    }

    static void test2(CompilationUnit cu) {
        class IntegerLiteralModifier extends ModifierVisitor<Void> {
            private final Pattern LOOK_AHEAD_THREE = Pattern.compile("(\\d)(?=(\\d{3})+$)");
            String format(String s) {
                return LOOK_AHEAD_THREE.matcher(s.replaceAll("_", "")).replaceAll("$1_");
            }
            @Override
            public Visitable visit(FieldDeclaration n, Void arg) {
                super.visit(n, arg);
                System.out.println(n.toString());
                n.getVariables().forEach(v ->
                        v.getInitializer().ifPresent(i ->
                                i.ifIntegerLiteralExpr(il ->
                                        v.setInitializer(format(il.getValue()))
                                )
                        )
                );
                return n;
            }
        }
        ModifierVisitor<Void> v = new IntegerLiteralModifier();
        v.visit(cu, null);
        // print altered source code
        //System.out.println(cu.toString());
    }

    static void test1(CompilationUnit cu){
        class MethodNamePrinter extends VoidVisitorAdapter<Void> {
            @Override
            public void visit(MethodDeclaration n, Void arg) {
                super.visit(n, arg);
                System.out.println("Method name: " + n.getName());
            }
        }
        VoidVisitor<Void> vv = new MethodNamePrinter();
        vv.visit(cu, null);
    }

    static void example() {
        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.
//        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(TestParser.class).resolve("src/main/resources"));

        // Our sample is in the root of this directory, so no package name.
//        CompilationUnit cu = sourceRoot.parse("", "test_code2.java");

//        cu.accept(new ModifierVisitor<Void>() {
//            /**
//             * For every if-statement, see if it has a comparison using "!=".
//             * Change it to "==" and switch the "then" and "else" statements around.
//             */
//            @Override
//            public Visitable visit(IfStmt n, Void arg) {
//                // Figure out what to get and what to cast simply by looking at the AST in a debugger!
//                n.getCondition().ifBinaryExpr(binaryExpr -> {
//                    if (binaryExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS && n.getElseStmt().isPresent()) {
//                        /* It's a good idea to clone nodes that you move around.
//                            JavaParser (or you) might get confused about who their parent is!
//                        */
//                        Statement thenStmt = n.getThenStmt().clone();
//                        Statement elseStmt = n.getElseStmt().get().clone();
//                        n.setThenStmt(elseStmt);
//                        n.setElseStmt(thenStmt);
//                        binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
//                    }
//                });
//                return super.visit(n, arg);
//            }
//        }, null);

        // This saves all the files we just read to an output directory.
//        sourceRoot.saveAll(
//                // The path of the Maven module/project which contains the de.b_andand_b.djava.LogicPositivizer class.
//                CodeGenerationUtils.mavenModuleRoot(Main.class)
//                        // appended with a path to "output"
//                        .resolve(Paths.get("output")));
    }
}