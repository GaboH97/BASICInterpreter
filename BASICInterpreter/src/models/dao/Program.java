package models.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import static models.dao.SyntaxValidator.findOperatorInSublogicExpression;
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
    private int nestingLevel;

    public Program() {
        variables = new HashMap<>();
        codeLines = new ArrayList<>();
        nestingLevel = 0;
    }

    public void loadCodeLines(ArrayList<String> textLines) throws Exception {
        for (String textLine : textLines) {
            loadLine(textLine);
        }
        executeProgram();
    }

    public void executeProgram() throws Exception {
        boolean programHasEnded = false;
        int executingLineIndex = 0;
        while (!programHasEnded) {
            //OBTIENE CONTENIDO DE LA LÍNEA
            Line codeLine = codeLines.get(executingLineIndex);
            String lineContent = codeLine.getText();
            if (codeLine.getLineType() != null) {
                switch (codeLine.getLineType()) {
                    case DIM:
                        processDimLine(codeLine);
                        executingLineIndex++;
                        break;
                    case PRINT:
                        processPrintLine2(codeLine);
                        executingLineIndex++;
                        break;
                    case INPUT:
                        processInputLine(codeLine);
                        executingLineIndex++;
                        break;
                    case WHILE:
                        executingLineIndex = processWhileLine(codeLine, executingLineIndex);
                        break;
                    case WEND:
                        //TODO
                        executingLineIndex = processWendLine(codeLine, executingLineIndex);
                        break;
                    case IF:
                        //TODO
                        executingLineIndex = processIfLine(codeLine, executingLineIndex);
                        break;
                    case ELSE:
                        //TODO
                        executingLineIndex++;
                        break;
                    case ENDIF:
                        executingLineIndex++;
                        break;
                    case GOTO:
                        //OBTIENE LA LÍNEA A LA QUE TIENE QUE IR
                        executingLineIndex = processGotoLine(codeLine, executingLineIndex);
                        if (executingLineIndex == -1) {
                            throw new Exception(
                                    SyntaxUtils.buildOutputErrorMessage(
                                            codeLine.getLineNumber(), "Línea inalcanzable"));
                        }
                        break;
                    case END:
                        programHasEnded = true;
                        break;
                }
            } else { //------ ASIGNACIONES
                processAssignationLine(codeLine);
                executingLineIndex++;
            }
        }
    }

    public void loadVariablesInProgram(int lineNumber, String[] varList, VariableType varType) throws Exception {
        for (int i = 0; i < varList.length; i++) {
            String variableName = varList[i];
            if (!variables.containsKey(variableName)) {

                Variable variable = new Variable(variableName,
                        varType,
                        (varType.equals(VariableType.STRING)) ? "" : 0);

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

    private void processDimLine(Line codeLine) throws Exception {
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

    private void processAssignationLine(Line codeLine) throws Exception {
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

    private void processPrintLine(Line codeLine) throws Exception {
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
                builder.append(printableToken.substring(1, printableToken.length() - 1)).append(" ");
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
        System.out.println(builder.toString());
    }

    private void processPrintLine2(Line codeLine) throws Exception {
        String lineContent = codeLine.getText();
        //OBTIENE LOS TOKENS DE LA LÍNEA
        String[] lineTokens = SyntaxValidator.deleteSpaces(lineContent).split(" ");

        String tokenAux = SyntaxValidator.buildNewStringFromIndex2(2, lineTokens);

        String[] printableTokens = tokenAux.split(";");
//        for (String printableToken : printableTokens) {
//            printableToken = SyntaxValidator.deleteSpaces(printableToken);
//            System.out.println("" + printableToken);
//            System.out.println(printableToken.startsWith(String.valueOf(SyntaxUtils.QUOTES)) 
//                    + " *** " 
//                    + (printableToken.endsWith(String.valueOf(SyntaxUtils.QUOTES))
//                        ||printableToken.endsWith(String.valueOf(" "))));
//        }
        StringBuilder builder = new StringBuilder();

        for (String printableToken : printableTokens) {
            printableToken = SyntaxValidator.deleteSpaces(printableToken);
            //IMPRIME UN LITERAL
            if (printableToken.startsWith(String.valueOf(SyntaxUtils.QUOTES))
                    && (printableToken.endsWith(String.valueOf(SyntaxUtils.QUOTES))
                    || printableToken.endsWith(String.valueOf(" ")))) {
                printableToken = printableToken.replaceAll(String.valueOf(SyntaxUtils.QUOTES), "");
                builder.append(printableToken).append(" ");
            } else {
                printableToken = printableToken.replaceAll(" ", "");
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
        System.out.println(builder.toString());
    }

    private void processInputLine(Line codeLine) throws Exception {
        //ABRE SCANNER PARA LA ENTRADA DE DATOS
        String varName = SyntaxValidator.deleteSpaces(codeLine.getText()).split(" ")[2];
        Variable var = variables.get(varName);
        if (var != null) {

            Scanner sc = new Scanner(System.in);
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

    private int processIfLine(Line codeLine, int executingLineIndex) throws Exception {

        if (solveLogicExpression(codeLine)) {
            return executingLineIndex + 1;
        } else {
            int elseIndex = getLineIndex(executingLineIndex, codeLine.getNestingLevel(), LineType.ELSE);
            if (elseIndex == -1) {//NO HAY UN ELSE EN ESTE IF
                return executingLineIndex = getLineIndex(executingLineIndex, codeLine.getNestingLevel(), LineType.ENDIF);
            } else {//HAY UN ELSE EN ESTE IF
                return elseIndex;
            }
        }
    }

    public boolean solveLogicExpression(Line codeLine) throws Exception {
        String text[] = SyntaxValidator.deleteSpaces(codeLine.getText()).split(" ");

        String lineTokens[] = null;
        if (codeLine.getLineType().equals(LineType.IF)) {
            lineTokens = SyntaxValidator.refactorTokensForIfLine(text);
        } else {
            lineTokens = SyntaxValidator.refactorTokensForWhileLine(text);
        }

        String logicExpr = lineTokens[2];

        String[] subLogicExpressions = logicExpr.split("AND|OR");

        if (subLogicExpressions.length == 1) { //CUANDO NO HAY AND|OR
            return solveSublogicExpression(subLogicExpressions[0]);
        } else {//CUANDO HAY AND|OR
            return solveLogicExpression(subLogicExpressions[0],
                    subLogicExpressions[1], (logicExpr.contains("AND")) ? "AND" : "OR");
        }
        // return solveSublogicExpression(subLogicExpressions[0]);

    }

    public boolean solveSublogicExpression(String sublogicExpression) throws Exception {
        //LA SUBEXPRESIÓN LÓGICA TIENE LA SIGUIENTE ESTRUCTURA
        //EXPRESIÓN ARITMÉTICA - OPERADOR LÓGICO DE COMPARACIÓN - EXPRESIÓN ARITMÉTICA

        //ENCUENTRA QUÉ OPERADOR LÓGICO DE COMPARACIÓN UTILIZA LA EXPRESIÓN
        String operator = findOperatorInSublogicExpression(sublogicExpression);

        int indexOfOperator = sublogicExpression.indexOf(operator);
        int lengthOfOperator = operator.length();

        //CREA NUEVO STRING CON LA PRIMERA EXPRESIÓN ARITMÉTICA DESDE INDEX 0
        //HASTA EL INDEX DEL OPERADOR LÓGICO DE COMPARACIÓN
        String firstAritExpr = sublogicExpression.substring(0, indexOfOperator);

        //CREA NUEVO STRING CON LA SEGUNDA EXPRESIÓN ARITMÉTICA DESDE INDEX DONDE
        //EMPIEZA EL OPERADOR + LA LONGITUD DEL OPERADOR HASTA EL FINAL DE LA
        //SUBEXPRESIÓN LÓGICA
        String secondAritExpr = sublogicExpression.substring(indexOfOperator + lengthOfOperator, sublogicExpression.length());

        //if (hasToRemove) {
//        System.out.println(" FIRST " + firstAritExpr);
//        System.out.println(" SECOND " + secondAritExpr);
        firstAritExpr = firstAritExpr.substring(1, firstAritExpr.length());
        secondAritExpr = secondAritExpr.substring(0, secondAritExpr.length() - 1);
        // }

//        System.out.println(" FIRST CHUNK " + firstAritExpr);
//        System.out.println(" SECOND CHUNK " + secondAritExpr);
//        System.out.println(" OPERATOR CHUNK \n " + operator);
        double valueLeft = solveArithmeticExpression(0, firstAritExpr);
        double valueRight = solveArithmeticExpression(0, secondAritExpr);

        return solveSublogicExpression(valueLeft, valueRight, operator);
    }

    public boolean solveLogicExpression(String leftSublogicExpr, String rightSublogicExpr, String operator) throws Exception {
        switch (operator) {
            case "AND":
                return solveSublogicExpression(leftSublogicExpr) && solveSublogicExpression(rightSublogicExpr);
            case "OR":
                return solveSublogicExpression(leftSublogicExpr) || solveSublogicExpression(rightSublogicExpr);
        }
        return false;
    }

    public static boolean solveSublogicExpression(double valueLeft, double valueRight, String operator) {
        switch (operator) {
            case "<":
                return valueLeft < valueRight;
            case ">":
                return valueLeft > valueRight;
            case "<=":
                return valueLeft <= valueRight;
            case ">=":
                return valueLeft >= valueRight;
            case "==":
                return valueLeft == valueRight;
        }
        return false;
    }

    private int processWhileLine(Line codeLine, int executingLineIndex) throws Exception {
        //ENCUENTRA EL WEND ANTES DE PROCESAR
        if (solveLogicExpression(codeLine)) {
            return executingLineIndex + 1;
        } else {
            return executingLineIndex = getLineIndex(executingLineIndex, codeLine.getNestingLevel(), LineType.WEND) + 1;
        }
    }

    private int processWendLine(Line codeLine, int executingLineIndex) {
        return executingLineIndex = getLineIndex(executingLineIndex, codeLine.getNestingLevel(), LineType.WHILE);
    }

    private int getLineIndex(int executingLineIndex, int nestingLevel, LineType lineType) {
        //OBTIENE LA LÍNEA QUE TIENE EL MISMO NESTING LEVEL, PARA SABER
        // A QUÉ LÍNEA SALTAR EN CASO DE QUE NO SE CUMPLA UNA CONDICIÓN
        // EN UNA INSTRUCCIÓN DE CONTROL (IF/WHILE)
        if (lineType.equals(LineType.WHILE)) {
            for (int i = executingLineIndex; i > 0; i--) {
                Line line = codeLines.get(i);
                if (line.getNestingLevel() == nestingLevel && line.getLineType().equals(lineType)) {
                    return i;
                }
            }
        } else {
            for (int i = executingLineIndex; i < codeLines.size(); i++) {
                Line line = codeLines.get(i);
                if (line.getNestingLevel() == nestingLevel && line.getLineType().equals(lineType)) {
                    return i;
                }
            }
        }

        //NO ENCUENTRA
        return -1;
    }

    private int processGotoLine(Line codeLine, int executingLineIndex) {
        int lineNumber = Integer.parseInt(SyntaxValidator.deleteSpaces(codeLine.getText()).split(" ")[2]);
        return getIndexInArrayOfLineNumber(lineNumber);
    }

    private int getIndexInArrayOfLineNumber(int lineNumber) {
        for (int i = 0; i < codeLines.size(); i++) {
            if (codeLines.get(i).getLineNumber() == lineNumber) {
                return i;
            }
        }
        // NO EXISTE DICHA LÍNEA
        return -1;
    }

    public void loadLine(String textLine) throws Exception {
        String lineTokens[] = SyntaxValidator.deleteSpaces(textLine).split(" ");

        int lineNumber = Integer.parseInt(lineTokens[0]);
        String lineTypeToken = lineTokens[1];

        //REVISA QUE EL STRING DEL TIPO DE LÍNEA ESTÉ EN EL ENUMERADO
        //DE TIPOS DE LÍNEA, SI NO, SE TOMA COMO UNA ASIGNACIÓN
        //DE VARIABLE
        boolean existsInLineType = Arrays.stream(LineType.values())
                .anyMatch(val -> val.name().equals(lineTypeToken.toUpperCase()));

        Line line = createLine(lineNumber, textLine,
                ((existsInLineType) ? LineType.valueOf(lineTypeToken.toUpperCase())
                        : null), 0);
        if (line.getLineType() != null) {

            switch (line.getLineType()) {
                //CADA VEZ QUE ABRE UN IF O UN WHILE, INCREMENTA EL NIVEL DE ANIDACIÓN EMPEZANDO
                //QUE COMIENZA DESDE 0
                case IF:
                case WHILE:
                    nestingLevel++;
                    line.setNestingLevel(nestingLevel);

                    break;

                //CADA VEZ QUE CIERRA UN IF O UN WHILE, INCREMENTA EL NIVEL DE ANIDACIÓN EMPEZANDO
                //QUE COMIENZA DESDE 0
                case ENDIF:
                case WEND:
                    line.setNestingLevel(nestingLevel);
                    nestingLevel--;
                    break;
                //CADA VEZ QUE HAY UN ELSE, LO DEJA CON EL MISMO NIVEL DE ANIDACIÓN
                //DEL ÚLTIMO IF ABIERTO
                case ELSE:
                    line.setNestingLevel(nestingLevel);
                    break;
                default:
                    break;
            }
        }

        if (!addLine(line)) {
            throw new Exception(SyntaxUtils.buildOutputErrorMessage(line.getLineNumber(), "Compilation error: " + SyntaxUtils.MSG_LINE_ALREADY_EXISTS));
        }
    }

    public static Line createLine(int lineNumber, String text, LineType lineType, int nestingLevel) {
        return new Line(lineNumber, text, lineType, nestingLevel);
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
        StringBuilder builder = new StringBuilder("Variables");
        for (Map.Entry<String, Variable> entry : variables.entrySet()) {
            String varName = entry.getKey();
            Variable var = entry.getValue();
            builder.append("\t").append(varName).append(" -> ").append(var.toString()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n Program \n");
        codeLines.forEach((codeLine) -> {
            builder.append(codeLine.toString());
        });
        return builder.toString();
    }

    //--------------------- TO REMOVE FROM THIS CLASS ----------------------}
    public double solveArithmeticExpression(int lineNumber, String expression) throws Exception {

        String trimmedExpression = expression.replaceAll(" ", "");

        char[] tokens = trimmedExpression.toCharArray();

        Stack<Double> values = new Stack<Double>();

        Stack<Character> operators = new Stack<Character>();

        for (int i = 0; i < tokens.length; i++) {

            //HASTA EL MOMENTO SOLO FUNCIONA CON NÚMEROS ENTEROS
            // OBTIENE CARACTER NUMÉRICO
            if (Character.isLetterOrDigit(tokens[i])) {
                StringBuffer sbuf = new StringBuffer();

                //OBTIENE LOS SIGUIENTES CARACTERES QUE NO SEAN OPERADORES
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

            } // CUANDO ABRE PARÉNTESIS SE MANDA A LA PILA DE OPERADORES
            else if (tokens[i] == '(') {
                operators.push(tokens[i]);
            } //CUANDO CIERRA PARÉNTESIS, SOLUCIONA LAS OPERACIONES
            else if (tokens[i] == ')') {
                while (operators.peek() != '(') {
                    values.push(doOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } // SI EL CARACTER ES UN OPERADOR
            else if (tokens[i] == '+' || tokens[i] == '-'
                    || tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '^') {
//                MIENTRAS QUE LA PARTE SUPERIOR DE 'OPERADORES' TIENE PRECEDENCIA IGUAL 
//                O MAYOR QUE EL TOKEN ACTUAL, QUE ES UN OPERADOR. APLICAR 
//                OPERADOR EN LA PARTE SUPERIOR DE 'OPERADORES' A LOS DOS ELEMENTOS
//                                        SUPERIORES EN LA PILA DE VALORES
                while (!operators.empty() && operatorHasPrecedence(tokens[i], operators.peek())) {
                    values.push(doOperation(operators.pop(), values.pop(), values.pop()));
                }
                // Push current token to 'ops'.
                operators.push(tokens[i]);
            }
        }

        //EMPIEZA A SOLUCIONAR LAS EXPRESIONES ALGEBRÁICAS HASTA QUE NO HAYA
        //MÁS OPERADORES EN LA PILA CORRESPONDIENTE
        while (!operators.empty()) {
            values.push(doOperation(operators.pop(), values.pop(), values.pop()));
        }

        // AL FINAL, LA PILA DE VALORES CONTIENE EL RESULTADO, EL CUAL SE RETORNA
        return values.pop();
    }

    // Retorna true si 'op2'tiene mayor precedencia que 'op1',
    // De lo contrario, retorna false
    // Precedencia va de esta manera: () -> ^ -> */ -> +-
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

    /**
     *
     * @param operator
     * @param b
     * @param a
     * @return El resultado operación realizada
     */
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

//    public static void main(String[] args) {
//        Program program = new Program();
//        Line line = new Line(200, "150 IF ((5/5)==0) THEN", LineType.IF, 0);
//        // String sublogicExpression = "(2+1)<(1+1)";
//        try {
//            System.out.println(" es valida ?: " + ((program.solveLogicExpression(line)) ? "SI" : "NO"));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            //Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
