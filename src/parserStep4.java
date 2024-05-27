import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;

public class parserStep4 {

    public void run() throws IOException {
        // First, I need to read the content of my Java file into a string.
        String content = FileUtils.readFileToString(new File("/home/sakib51/eclipse-workspace/TestParser/src/ContentReader.java"));

        // Now, I'll create an AST parser and configure it to parse a Java source file.
        ASTParser parser = ASTParser.newParser(AST.JLS8);

        // I'll set the source code to be parsed by the AST parser.
        parser.setSource(content.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        // I need to enable binding resolution to get type information.
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);

        // I'll set the classpath and sourcepath to null to use the default settings.
        parser.setEnvironment(null, null, null, true);
        parser.setUnitName("/home/sakib51/eclipse-workspace/TestParser/src/ContentReader.java");

        // Now, I'll parse the source code to create an abstract syntax tree (AST).
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        // Time to traverse the AST to gather the information I need.
        cu.accept(new ASTVisitor() {

            @Override
            public boolean visit(MethodDeclaration node) {
                // Whenever I find a method declaration, I'll collect its name.
                String methodName = node.getName().getFullyQualifiedName();
                System.out.println("Method Declaration: " + methodName);

                // Now, I need to find variables declared inside the method body along with their line numbers.
                node.getBody().accept(new ASTVisitor() {
                    @Override
                    public boolean visit(VariableDeclarationFragment var) {
                        // I'll get the line number where the variable is declared.
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
                List<?> arguments = node.arguments();
                int numberOfParameters = arguments.size();
                List<String> parameterTypes = new ArrayList<>();

                // I'll collect the types of each parameter.
                for (Object arg : arguments) {
                    if (arg instanceof Expression) {
                        ITypeBinding typeBinding = ((Expression) arg).resolveTypeBinding();
                        if (typeBinding != null) {
                            parameterTypes.add(typeBinding.getQualifiedName());
                        } else {
                            parameterTypes.add("Unknown");
                        }
                    }
                }

                // Now, I'll print the method call information including name, number of parameters, and their types.
                System.out.println("Method call: " + methodName + " with " + numberOfParameters + " parameters: " + parameterTypes);

                // Let's determine the receiver variable and collect method calls on the same variable.
                Expression receiver = node.getExpression();
                if (receiver != null && receiver instanceof SimpleName) {
                    String receiverName = ((SimpleName) receiver).getIdentifier();
                    System.out.println("Receiver variable: " + receiverName);

                    // I need to navigate up to find the enclosing method body.
                    ASTNode methodBody = node.getParent();
                    while (!(methodBody instanceof MethodDeclaration) && methodBody != null) {
                        methodBody = methodBody.getParent();
                    }
                    if (methodBody != null) {
                        methodBody.accept(new ASTVisitor() {
                            @Override
                            public boolean visit(MethodInvocation priorNode) {
                                if (priorNode == node) {
                                    return false;
                                }

                                // I'll check if the prior method call has the same receiver variable.
                                Expression priorReceiver = priorNode.getExpression();
                                if (priorReceiver != null && priorReceiver instanceof SimpleName) {
                                    String priorReceiverName = ((SimpleName) priorReceiver).getIdentifier();
                                    if (receiverName.equals(priorReceiverName)) {
                                        System.out.println("Prior method call on same variable: " + priorNode.getName().getIdentifier());
                                    }
                                }

                                // I'll also check if the prior method call uses the receiver variable as an argument.
                                for (Object arg : priorNode.arguments()) {
                                    if (arg instanceof SimpleName) {
                                        String argName = ((SimpleName) arg).getIdentifier();
                                        if (receiverName.equals(argName)) {
                                            System.out.println("Prior method call using receiver as argument: " + priorNode.getName().getIdentifier());
                                        }
                                    }
                                }
                                //System.out.println();
                                return super.visit(priorNode);
                            }
                        });
                    }
                }
                System.out.println();
                return super.visit(node);
            }
        });
    }

    public static void main(String[] args) {
        // I'll create an instance of the parser and run it.
        parserStep4 driver = new parserStep4();
        try {
            driver.run();
        } catch (IOException e) {
            // I'll handle any IO exceptions that might occur.
            e.printStackTrace();
        }
    }
}
