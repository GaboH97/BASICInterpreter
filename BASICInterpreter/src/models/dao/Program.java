package models.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import models.entity.Line;
import models.entity.LineType;
import models.entity.Variable;
import models.entity.VariableType;

/**
 *
 * @author user
 */
public class Program {

    private HashMap<String, Variable> variables;
    private ArrayList<Line> codeLines;

    public Program() {
        variables = new HashMap<>();
        codeLines = new ArrayList<>();
    }

    public void loadCodeLines(ArrayList<String> textLines) throws Exception {
        for (String textLine : textLines) {
            loadLine(textLine);
        }
        loadProgramVariables();
    }

    public void loadProgramVariables() throws Exception {
        for (Line codeLine : codeLines) {
            //OBTIENE CONTENIDO DE LA LÍNEA
            String lineContent = codeLine.getText();
            if (codeLine.getLineType() != null) {
                switch (codeLine.getLineType()) {
                    case DIM:
                        processDimLine(codeLine);
                        break;
                    case PRINT:
                        processPrintLine(codeLine);
                        break;
                    case INPUT:
                        processInputLine(codeLine);
                        break;
                    default:
                        break;
                }
            } else { //------ ASIGNACIONES
                processAssignationLine(codeLine);
            }
        }
    }

    public void loadVariablesInProgram(int lineNumber, String[] varList, VariableType varType) throws Exception {
        for (int i = 0; i < varList.length; i++) {
            String variableName = varList[i];
            if (!variables.containsKey(variableName)) {

                Variable variable = new Variable(variableName,
                        varType,
                        (varType.equals(VariableType.STRING)) ? "Hola soy un string" : 2.0);

                variables.put(variableName, variable);
            } else {
                throw new Exception(
                        SyntaxUtils.buildOutputErrorMessage(
                                lineNumber, "Variable: "
                                + SyntaxUtils.QUOTES + variableName
                                + SyntaxUtils.QUOTES) + " ya ha sido declarada");
            }
        }
    }

    public void processDimLine(Line codeLine) throws Exception {
        String lineContent = codeLine.getText();
        //OBTIENE LOS TOKENS DE LA LÍNEA
        String[] lineTokens = SyntaxValidator.deleteSpaces(lineContent).split(" ");
        lineTokens = SyntaxValidator.refactorTokensForDimLine(lineTokens);
        //OBTIENE LA LISTA DE VARIABLES
        String[] varList = lineTokens[2].split(",");
        //OBTIENE TIPO DE VARIABLE
        String variableType = lineTokens[lineTokens.length - 1];

        VariableType varType = VariableType.valueOf(variableType);

        loadVariablesInProgram(codeLine.getLineNumber(), varList, varType);
    }

    public void processAssignationLine(Line codeLine) throws Exception {
        String lineContent = codeLine.getText();
        //OBTIENE LOS TOKENS DE LA LÍNEA
        String[] lineTokens = SyntaxValidator.deleteSpaces(lineContent).split(" ");
        lineTokens = SyntaxValidator.refactorTokensForAssignationLine(lineTokens);

        String varToAssignToken = lineTokens[1];
        if (variables.containsKey(varToAssignToken)) {
            Variable var1 = variables.get(varToAssignToken);
            if (var1.getVariableType().equals(VariableType.DOUBLE)) {
                //SI ES DE TIPO DOUBLE, SE ESPERA QUE EL NUEVO VALOR
                //SEA UNA EXPRESIÓN ALGEBRÁICA
                String aritmethicExpression = lineTokens[3];
                double newValue = solveArithmeticExpression(codeLine.getLineNumber(), aritmethicExpression);
                var1.setValue(newValue);
            } else { //ES UN STRING, POR ENDE, SOLO DEBE RECIBIR UN PARAMETRO
                //YA SEA UNA VARIABLE O UN LITERAL
                String newValToken = lineTokens[3];
                //ES UN LITERAL -> "TEXTO"
                if (newValToken.startsWith(String.valueOf(SyntaxUtils.QUOTES))
                        && newValToken.endsWith(String.valueOf(SyntaxUtils.QUOTES))) {
                    var1.setValue(newValToken);
                    //ES UNA VARIABLE
                } else if (SyntaxValidator.isValidVariableName(newValToken)) {
                    if (variables.containsKey(newValToken)) {
                        Variable var2 = variables.get(newValToken);
                        if (var2.getVariableType().equals(VariableType.STRING)) {

                        } else {//ES UNA VARIABLE DE TIPO DOUBLE
                            throw new Exception(SyntaxUtils.buildOutputErrorMessage(codeLine.getLineNumber(), "Variable "
                                    + SyntaxUtils.QUOTES
                                    + var2.getVariableName()
                                    + SyntaxUtils.QUOTES
                                    + " Incompatible"));
                        }
                    } else {//NO EXISTE LA VARIABLE
                        throw new Exception(SyntaxUtils.buildOutputErrorMessage(codeLine.getLineNumber(), "Variable "
                                + SyntaxUtils.QUOTES
                                + newValToken
                                + SyntaxUtils.QUOTES
                                + " no ha sido declarada"));
                    }
                } else { //NOMBRE DE VARIABLE INVÁLIDO
                    throw new Exception(SyntaxUtils.buildOutputErrorMessage(codeLine.getLineNumber(), SyntaxUtils.MSG_INVALID_VARIABLE_NAME));
                }
            }
        } else { //NO EXISTE LA VARIABLE
            throw new Exception(SyntaxUtils.buildOutputErrorMessage(codeLine.getLineNumber(), "Variable "
                    + SyntaxUtils.QUOTES + varToAssignToken + SyntaxUtils.QUOTES + "no ha sido declarada"));
        }
    }

    public void processPrintLine(Line codeLine) throws Exception {
        String lineContent = codeLine.getText();
        //OBTIENE LOS TOKENS DE LA LÍNEA
        String[] lineTokens = SyntaxValidator.deleteSpaces(lineContent).split(" ");

        String tokenAux = SyntaxValidator.buildNewStringFromIndex(2, lineTokens);

        String[] printableTokens = tokenAux.split(";");
        StringBuilder builder = new StringBuilder();

        for (String printableToken : printableTokens) {
            //IMPRIME UN LITERAL
            if (printableToken.startsWith(String.valueOf(SyntaxUtils.QUOTES))
                    && printableToken.endsWith(String.valueOf(SyntaxUtils.QUOTES))) {
                builder.append(printableToken);
            } else {
                //IMPRIME UNA VARIABLE
                if (variables.containsKey(printableToken)) {
                    builder.append(variables.get(printableToken).getValue()).append(" ");
                } else {
                    throw new Exception(SyntaxUtils.buildOutputErrorMessage(codeLine.getLineNumber(), "Variable "
                            + SyntaxUtils.QUOTES
                            + printableToken
                            + SyntaxUtils.QUOTES
                            + " no ha sido declarada"));
                }
            }
        }
        System.out.println("Printing: " + builder.toString());
    }

    public void processInputLine(Line codeLine) throws Exception {
        //ABRE SCANNER PARA LA ENTRADA DE DATOS
        String varName = SyntaxValidator.deleteSpaces(codeLine.getText()).split(" ")[2];
        Variable var = variables.get(varName);
        if (var != null) {

            Scanner sc = new Scanner(System.in);
            System.out.println("perrito");
            String newValue = sc.nextLine();

            //ESPERA UN VALOR TIPO DOUBLE
            if (SyntaxValidator.isNumeric(newValue)) {
                if (var.getVariableType().equals(VariableType.DOUBLE)) {
                    var.setValue(Double.parseDouble(newValue));
                } else {
                    sc.close();
                    throw new Exception(SyntaxUtils.buildOutputErrorMessage(codeLine.getLineNumber(), "Variable "
                            + SyntaxUtils.QUOTES
                            + varName
                            + SyntaxUtils.QUOTES
                            + " es de tipo String y no recibe números"));

                }
            } else { //ESPERA UN VALOR TIPO STRING
                if (var.getVariableType().equals(VariableType.STRING)) {
                    var.setValue(newValue);

                } else {
                    sc.close();
                    throw new Exception(SyntaxUtils.buildOutputErrorMessage(codeLine.getLineNumber(), "Variable "
                            + SyntaxUtils.QUOTES
                            + varName
                            + SyntaxUtils.QUOTES
                            + " es de tipo Double y no recibe caracteres"));
                }
            }

        } else {
            throw new Exception(SyntaxUtils.buildOutputErrorMessage(codeLine.getLineNumber(), "Variable "
                    + SyntaxUtils.QUOTES
                    + varName
                    + SyntaxUtils.QUOTES
                    + " no ha sido declarada"));
        }

    }

    public void loadLine(String textLine) throws Exception {
        String lineTokens[] = SyntaxValidator.deleteSpaces(textLine).split(" ");
        
        System.out.println("IMPIRMO "+lineTokens[0]);
        int lineNumber = Integer.parseInt(lineTokens[0]);
        String lineTypeToken = lineTokens[1];

        //REVISA QUE EL STRING DEL TIPO DE LÍNEA ESTÉ EN EL ENUMERADO
        //DE TIPOS DE LÍNEA, SI NO, SE TOMA COMO UNA ASIGNACIÓN
        //DE VARIABLE
        boolean existsInLineType = Arrays.stream(LineType.values())
                .anyMatch(val -> val.name().equals(lineTypeToken.toUpperCase()));

        Line line = createLine(lineNumber, textLine,
                (existsInLineType) ? LineType.valueOf(lineTypeToken.toUpperCase())
                        : null);

        if (addLine(line)) {
            System.out.println("Line successfully added!");
        } else {
            throw new Exception(SyntaxUtils.buildOutputErrorMessage(line.getLineNumber(), "Compilation error: " + SyntaxUtils.MSG_LINE_ALREADY_EXISTS));
        }
    }

    public static Line createLine(int lineNumber, String text, LineType lineType) {
        return new Line(lineNumber, text, lineType);
    }

    public boolean addLine(Line line) {
        if (!lineAlreadyExists(line.getLineNumber())) {
            codeLines.add(line);
            return true;
        } else {
            return false;
        }
    }

    public boolean lineAlreadyExists(int lineNumber) {
        return codeLines.stream().anyMatch(line -> line.getLineNumber() == lineNumber);
    }

    public ArrayList<Line> getCodeLines() {
        return codeLines;
    }

    public HashMap<String, Variable> getVariables() {
        return variables;
    }

    public String printVariables() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Variable> entry : variables.entrySet()) {
            String varName = entry.getKey();
            Variable var = entry.getValue();
            builder.append(varName).append(" -> ").append(var.toString()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Program \n");
        codeLines.forEach((codeLine) -> {
            builder.append(codeLine.toString());
        });
        return builder.toString();
    }

    //--------------------- TO REMOVE FROM THIS CLASS ----------------------
    public double solveArithmeticExpression(int lineNumber, String expression) throws Exception {

        String trimmedExpression = expression.replaceAll(" ", "");

        char[] tokens = trimmedExpression.toCharArray();

        // Stack for numbers: 'values'
        Stack<Double> values = new Stack<Double>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<Character>();

        for (int i = 0; i < tokens.length; i++) {

            //HASTA EL MOMENTO SOLO FUNCIONA CON NÚMEROS ENTEROS
            // Current token is a number, push it to stack for numbers
            if (Character.isLetterOrDigit(tokens[i])) {
                StringBuffer sbuf = new StringBuffer();

                // There may be more than one digits in number
                while (i < tokens.length && Character.isLetterOrDigit(tokens[i])) {
                    sbuf.append(tokens[i++]);
                }
                i--;

                //OBTIENE EL OPERANDO MIRA LUEGO SI ES NUMÉRICO
                String operand = sbuf.toString();
                if (SyntaxValidator.isNumeric(operand)) {
                    //SI ES ASÍ, MANDA A LA PILA DIRECTAMENTE EL VALOR CASTEADO
                    values.push(Double.parseDouble(operand));
                } else {
                    Variable var = variables.get(operand);
                    if (var != null) {
                        if (var.getVariableType().equals(VariableType.DOUBLE)) {
                            values.push((Double) var.getValue());
                        } else {//SI LA VARIABLE ES DE TIPO STRING
                            throw new Exception(SyntaxUtils.buildOutputErrorMessage(lineNumber, "Variable incompatible: "
                                    + SyntaxUtils.QUOTES
                                    + operand
                                    + SyntaxUtils.QUOTES
                                    + " es de tipo STRING"));
                        }
                    } else { //SI LA VARIABLE NO EXISTE
                        throw new Exception(SyntaxUtils.buildOutputErrorMessage(lineNumber, "Variable "
                                + SyntaxUtils.QUOTES
                                + operand
                                + SyntaxUtils.QUOTES
                                + " no ha sido declarada"));
                    }

                }

                //values.push(Double.parseDouble(sbuf.toString()));
            } // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(') {
                ops.push(tokens[i]);
            } // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(doOperation(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            } // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-'
                    || tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '^') {
                // While top of 'ops' has same or greater precedence to current
                // token, which is an operator. Apply operator on top of 'ops'
                // to top two elements in values stack
                while (!ops.empty() && operatorHasPrecedence(tokens[i], ops.peek())) {
                    values.push(doOperation(ops.pop(), values.pop(), values.pop()));
                }
                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }
        }

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty()) {
            values.push(doOperation(ops.pop(), values.pop(), values.pop()));
        }

        // Top of 'values' contains result, return it
        return values.pop();
    }

    // Returns true if 'op2' has higher or same precedence as 'op1',
    // otherwise returns false.
    // Precedence goes like this () -> ^ -> */ -> +-
    public static boolean operatorHasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '^') && ((op2 == '*' || op2 == '/'))
                || (op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')
                || (op1 == '^') && (op2 == '+' || op2 == '-')) {
            return false;
        } else {
            return true;
        }
    }

    // A utility method to apply an operator 'op' on operands 'a' 
    // and 'b'. Return the result.
    public static double doOperation(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '^':
                return Math.pow(a, b);
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("No se puede dividir por cero");
                }
                return a / b;
        }
        return 0;
    }
}
