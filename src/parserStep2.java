import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;

public class parserStep2 {

    public void run() throws IOException {
        // First, I need to read the content of the Java file into a string.
        String content = FileUtils.readFileToString(new File("/home/sakib51/eclipse-workspace/TestParser/src/ContentReader.java"));

        // Now, I create an AST parser and configure it to parse a Java source file (compilation unit).
        ASTParser parser = ASTParser.newParser(AST.JLS8);

        // Setting the source code to be parsed.
        parser.setSource(content.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        // Enable binding resolution
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);

        // Setting the classpath and sourcepath
        parser.setEnvironment(null, null, null, true);
        parser.setUnitName("/path/to/your/ContentReader.java");

        // Parsing the source code to create an abstract syntax tree (AST).
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        // Now, I need to traverse the AST to gather information.
        cu.accept(new ASTVisitor() {

            @Override
            public boolean visit(MethodDeclaration node) {
                // When I find a method declaration, I'll collect its name.
                String methodName = node.getName().getFullyQualifiedName();
                System.out.println("Method Declaration: " + methodName);

                // Then, I need to find variables declared inside the method body along with their line numbers.
                node.getBody().accept(new ASTVisitor() {
                    @Override
                    public boolean visit(VariableDeclarationFragment var) {
                        // Getting the line number where the variable is declared.
                        int lineNumber = cu.getLineNumber(var.getStartPosition());
                        System.out.println("Variable declaration: " + var.getName() + " at line " + lineNumber);
                        return super.visit(var);
                    }
                });
                System.out.println();
                return super.visit(node);
            }
            
            @Override
            public boolean visit(MethodInvocation node) {
                // For each method call, I need to determine the method signature.
                String methodName = node.getName().getIdentifier();
                // This retrieves the arguments of the method call. It's a list of unknown type, hence List<?>.
                List<?> arguments = node.arguments();
                int numberOfParameters = arguments.size();
                List<String> parameterTypes = new ArrayList<>();
                
                // Collecting the types of each parameter.
                for (Object arg : arguments) {
                    // Checking if the argument is an instance of Expression.
                    if (arg instanceof Expression) {
                        // Resolving the type of the expression argument.
                        ITypeBinding typeBinding = ((Expression) arg).resolveTypeBinding();
                        if (typeBinding != null) {
                            // Adding the fully qualified name of the type to the parameterTypes list.
                            parameterTypes.add(typeBinding.getQualifiedName());
                        } else {
                            // If type binding is null, adding "Unknown" to the parameterTypes list.
                            parameterTypes.add("Unknown");
                        }
                    }
                }

                // Printing the method call information including name, number of parameters, and their types.
                System.out.println("Method call: " + methodName + " with " + numberOfParameters + " parameters: " + parameterTypes);
                return super.visit(node);
            }
        });
    }

    public static void main(String[] args) {
        // Creating an instance of the parser and running it.
    	parserStep2 driver = new parserStep2();
        try {
            driver.run();
        } catch (IOException e) {
            // Handling any IO exceptions that might occur.
            e.printStackTrace();
        }
    }
}

