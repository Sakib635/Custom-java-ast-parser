# Java AST Parser

This project is a Java AST (Abstract Syntax Tree) parser that uses the Eclipse JDT (Java Development Tools) to analyze Java source files. The parser extracts and prints information about method declarations, variable declarations, method invocations, and their relationships within a codebase.

## Features

1. **Step 1**: Parse method declarations and identify variables declared in the method body along with their line numbers.
2. **Step 2**: Determine the signature of each method call, including the method name, number of parameters, and types of parameters.
3. **Step 3**: For each method call, determine the receiver variable and collect those methods that are called on the same variable prior to the current method call.
4. **Step 4**: For each method call whose receiver is a variable, determine those methods called prior that use the receiver variable as an argument.

## Setup

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Eclipse IDE or any Java IDE
- Apache Commons IO library
- Eclipse JDT Core library

### Getting Started

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/java-ast-parser.git
   cd java-ast-parser

2. Open the project in your preferred Java IDE.

3. Add the required libraries to your project's build path:

  Apache Commons IO (commons-io-x.x.jar)
  Eclipse JDT Core (org.eclipse.jdt.core_x.x.x.jar)

4. Update the file path in the parserStep class to point to your Java source file:
String content = FileUtils.readFileToString(new File("/path/to/your/ContentReader.java"));

5. Run the parserStep4 class to see the output.


## Usage
The main class parserStep reads a Java source file, parses it into an AST, and prints the following information to the console:

Step 1:
Method declarations and their names.
Variable declarations within method bodies and their line numbers.

Step 2:
Method invocations and their signatures (method name, number of parameters, and parameter types).

Step 3:
Receiver variable of method calls.
Method calls on the same receiver variable within the same method body, prior to the current method call.

Step 4:
Method calls that use the receiver variable as an argument, within the same method body, prior to the current method call.
