import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;

public class parserStep1 {

    public void run() throws IOException {
        // Read the content of the file
        String content = FileUtils.readFileToString(new File("/home/sakib51/eclipse-workspace/TestParser/src/ContentReader.java"));

        // Create an AST parser
        ASTParser parser = ASTParser.newParser(AST.JLS8);

        // Set the source code to be parsed
        parser.setSource(content.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        // Visitor to gather information
        cu.accept(new ASTVisitor() {

            @Override
            public boolean visit(MethodDeclaration node) {
                // Collecting method declarations
                String methodName = node.getName().getFullyQualifiedName();
                System.out.println("Method Declaration: " + methodName);

                // Identifying variables declared in the method body with line numbers
                node.getBody().accept(new ASTVisitor() {
                    @Override
                    public boolean visit(VariableDeclarationFragment var) {
                        int lineNumber = cu.getLineNumber(var.getStartPosition());
                        System.out.println("Variable declaration: " + var.getName() + " at line " + lineNumber);
                        return super.visit(var);
                    }
                });

                return super.visit(node);
            }
        });
    }

    public static void main(String[] args) {
    	parserStep1 driver = new parserStep1();
        try {
            driver.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
