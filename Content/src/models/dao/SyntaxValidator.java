package models.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static models.dao.SyntaxUtils.buildOutputErrorMessage;
import models.entity.LineType;

/**
 *
 * @author user
 */
public class SyntaxValidator {

    private boolean isWhileOpen;
    private boolean isIfOpen;
    private boolean hasEnded;
    private boolean hasDeclaredVariables;
    private int ifsOpened;
    private int whilesOpened;

    public SyntaxValidator() {
        isWhileOpen = false;
        isIfOpen = false;
        hasEnded = false;
        hasDeclaredVariables = false;
        ifsOpened = 0;
        whilesOpened = 0;
    }

    public void validateCodeLines(ArrayList<String> lines) throws Exception {
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).isEmpty()) {
                validateLine(i, lines.get(i));
            }
        }

        if (ifsOpened > 0) {
            throw new Exception(buildOutputErrorMessage(lines.size(), SyntaxUtils.MSG_IF_NOT_CLOSED));
        } else if (ifsOpened < 0) {
            throw new Exception(buildOutputErrorMessage(lines.size(), SyntaxUtils.MSG_IF_NOT_OPENED));
        }

        if (whilesOpened > 0) {
            throw new Exception(buildOutputErrorMessage(lines.size(), SyntaxUtils.MSG_WHILE_NOT_CLOSED));
        } else if (whilesOpened < 0) {
            throw new Exception(buildOutputErrorMessage(lines.size(), SyntaxUtils.MSG_WHILE_NOT_OPENED));
        }

        if (isWhileOpen) {
            throw new Exception(buildOutputErrorMessage(lines.size(), SyntaxUtils.MSG_WHILE_NOT_CLOSED));
        }

        if (isIfOpen) {
            throw new Exception(buildOutputErrorMessage(lines.size(), SyntaxUtils.MSG_IF_NOT_CLOSED));
        }

        if (!hasEnded) {
            throw new Exception(buildOutputErrorMessage(lines.size(), SyntaxUtils.MSG_PROGRAM_HAS_NOT_ENDED));
        }

        System.out.println("\n \t Succesfully validated!");
    }

    public static String deleteSpaces(String str) {    //custom method to remove multiple space
        StringBuilder sb = new StringBuilder();
        for (String s : str.split(" |\t")) {
            if (!s.equals("")) { // ignore space
                sb.append(s).append(" ");       // add word with 1 space
            }
        }
        return sb.toString();
    }

    public void validateLine(int lineIndex, String line) throws Exception {
        String trimmedLine = deleteSpaces(line);

        //Split line into spaces
        String[] lineTokens = trimmedLine.split(" ");

        if (!hasEnded) {
            //Check is a valid lineNumber
            if (isValidLineNumber(lineTokens[0])) {
                if (lineTokens[1] != null) {
                    String identifierToken = lineTokens[1].toUpperCase();

                    //REVISA QUE EL STRING DEL TIPO DE LÍNEA ESTÉ EN EL ENUMERADO
                    //DE TIPOS DE LÍNEA, SI NO, SE TOMA COMO UNA ASIGNACIÓN
                    //DE VARIABLE
                    boolean existsInLineType = Arrays.stream(LineType.values())
                            .anyMatch(val -> val.name().equals(identifierToken.toUpperCase()));

                    if (existsInLineType) {

                        switch (LineType.valueOf(identifierToken)) {

                            case DIM:
                                if (!hasDeclaredVariables) {
                                    //System.out.println("Línea " + lineIndex + " es DIM");
                                    validateDIMLine(lineIndex, line);
                                } else {
                                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_CANNOT_DECLARE_VARIABLE));
                                }
                                break;
                            case PRINT:
                                //System.out.println("Línea " + lineIndex + " es PRINT");
                                validatePrintLine(lineIndex, line);
                                hasDeclaredVariables = true;
                                break;
                            case INPUT:
                                // System.out.println("Línea " + lineIndex + " es INPUT");
                                validateInputLine(lineIndex, line);
                                hasDeclaredVariables = true;
                                break;
                            case IF:
//                                System.out.println("Línea " + lineIndex + " es IF");
                                validateIfLine(lineIndex, line);
                                isIfOpen = true;
                                ifsOpened++;
                                hasDeclaredVariables = true;
                                break;
                            case ENDIF:
//                                System.out.println("Línea " + lineIndex + " es ENDIF");
                                validateElseEndEndIfWendLine(lineIndex, line);
                                isIfOpen = false;
                                hasDeclaredVariables = true;
                                ifsOpened--;
                                break;
                            case WHILE:
//                                System.out.println("Línea " + lineIndex + " es WHILE");
                                validateWhileLine(lineIndex, line);
                                isWhileOpen = true;
                                hasDeclaredVariables = true;
                                whilesOpened++;
                                break;
                            case WEND:
//                                System.out.println("Línea " + lineIndex + " es WEND");
                                validateElseEndEndIfWendLine(lineIndex, line);
                                isWhileOpen = false;
                                hasDeclaredVariables = true;
                                whilesOpened--;
                                break;
                            case GOTO:
//                                System.out.println("Línea " + lineIndex + " es GOTO");
                                validateGotoLine(lineIndex, line);
                                hasDeclaredVariables = true;
                                break;
                            case END:
//                                System.out.println("Línea " + lineIndex + " es END");
                                validateElseEndEndIfWendLine(lineIndex, line);
                                hasEnded = true;
                                hasDeclaredVariables = true;
                                break;
                            case ELSE:
                                if (isIfOpen) {
//                                    System.out.println("Línea " + lineIndex + " es ELSE");
                                    validateElseEndEndIfWendLine(lineIndex, line);
                                    hasDeclaredVariables = true;

                                } else {
                                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_IF_NOT_OPENED));
                                }
                                break;
                        }
                    } else {
                        //ESTE CASO ES PARA ASIGNACIÓN DE VARIABLE
//                        System.out.println("Línea " + lineIndex + " es de asignación ");
                        validateAssignationLine(lineIndex, line);
                        hasDeclaredVariables = true;
                    }

                } else {
                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
                }
            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_LINE_NUMBER));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_STATEMENTS_AFTER_END));
        }
    }
    //********************** VALIDATIONS *************************

    public static void validateInputLine(int lineIndex, String line) throws Exception {
        //Trim string to only one space between words
        String trimmedLine = deleteSpaces(line);

        //Split line into spaces
        String[] lineTokens = trimmedLine.split(" ");
        //Check is a valid lineNumber

        if (lineTokens[2] != null) {

            if (isValidVariableName(lineTokens[2])) {

//                System.out.println("\t Line " + lineIndex + ": INPUT line is OK");
            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_VARIABLE_NAME));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
        }

    }

    public static void validatePrintLine(int lineIndex, String line) throws Exception {

        String[] lineTokens = deleteSpaces(line).split(" ");
        //Build a new token out of the original lineToken array starting
        //at an specific index

        String tokenAux = buildNewStringFromIndex(2, lineTokens);

        String[] printableTokens = tokenAux.split(";");
        

        long countQuotes = tokenAux.chars().filter(num -> num == SyntaxUtils.QUOTES).count();
        long countdotAndComma = tokenAux.chars().filter(num -> num == ';').count();

        //Check if number of quotes is odd and every dot and comma is
        //enclosed between two printable tokens
       
        if (((countQuotes & 1) == 0) && (printableTokens.length - 1 == countdotAndComma) && (((countQuotes / 2) - 1) <= countdotAndComma) && auxValidatorPrint(tokenAux)) {

            if (areValidPrintableTokens(printableTokens)) {
//                System.out.println("\t Line " + lineIndex + ": PRINT line is OK");

            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_VARIABLE_NAME));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_PRINTABLE_CODE));
        }

    }

    private static boolean auxValidatorPrint(String line) {
        boolean quotesOpen = false;
        boolean dotAndComaOpen = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                quotesOpen = !quotesOpen;
            }
            if (c == ';') {
                if (quotesOpen) {
                    return false;
                }
            }
        }
        int counter = 0;
        for (int i = 0; i < line.length() - 1; i++) {
            if (line.charAt(i) == '"') {
                counter++;
                if (counter == 2) {
                    if ((line.charAt(i + 1) == ';') || line.charAt(i + 1) == ',') {
                        counter = 0;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void validateDIMLine(int lineIndex, String line) throws Exception {

        //Trim string to only one space between words && Split line into spaces
        String[] lineTokens = deleteSpaces(line).split(" ");

        lineTokens = refactorTokensForDimLine(lineTokens);
        //---------------------------------------------------------------------
        if (lineTokens.length < 6) {
            //split variables by commas for third token, check variablesamount - 1
            //equal to countCommas
            String[] variablesList = lineTokens[2].split(",");
            long countCommas = lineTokens[2].chars().filter(num -> num == ',').count();
            if (variablesList.length - 1 == countCommas) {
                //Check that all variables in variable list are
                if (areValidVariablesNames(variablesList)) {

                    if (lineTokens[3] != null || lineTokens[4] != null) {

                        if (lineTokens[3].toUpperCase().equals("AS")
                                && (lineTokens[4].toUpperCase().equals("DOUBLE")
                                || lineTokens[4].toUpperCase().equals("STRING"))) {

//                            System.out.println("\t Line " + lineIndex + ": DIM line is OK");
                        } else {
                            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_DATATYPE));
                        }
                    } else {
                        throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
                    }
                } else {
                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_VARIABLE_NAME));
                }
            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, "Más comas que variables"));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        }
    }

    public static String[] refactorTokensForDimLine(String[] lineTokens) {
        ArrayList<String> lineTokensAux = new ArrayList<>();
        String aux = "";
        int counter = 0;
        lineTokensAux.add(lineTokens[0]);
        lineTokensAux.add(lineTokens[1]);
        for (int i = 2; i < lineTokens.length; i++) {
            if (!lineTokens[i].equals("AS")) {
                aux += lineTokens[i];
            } else {
                counter = i;
                lineTokensAux.add(aux);
                break;
            }
        }
        for (int i = counter; i < lineTokens.length; i++) {
            lineTokensAux.add(lineTokens[i]);
        }
        return lineTokensAux.toArray(new String[lineTokensAux.size()]);
    }

    public void validateElseEndEndIfWendLine(int lineIndex, String line) throws Exception {

        String trimmedLine = deleteSpaces(line);

        //Split line into spaces
        String[] lineTokens = trimmedLine.split(" ");

        if (lineTokens.length < 3) {
//            System.out.println("\t Line " + lineIndex + ": END/ENDIF/WEND line is OK");
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        }

    }

    public static void validateAssignationLine(int lineIndex, String line) throws Exception {

        //Trim string to only one space between words && Split line into spaces
        String[] lineTokens = deleteSpaces(line).split(" ");

        lineTokens = refactorTokensForAssignationLine(lineTokens);
        //FALLA SI HAY ESPACIOS EN LA ASIGNACIÓN
        if (lineTokens.length < 5) {

            if (isValidVariableName(lineTokens[1])) {

                if (lineTokens[2] != null && lineTokens[3] != null) {

                    if (lineTokens[2].equals(SyntaxUtils.ASSIGNATION)) {

                        String newValToken = lineTokens[3];
                        //RECIBE UN LITERAL O UNA EXPRESIÓN ALGEBRÁICA
                        if (isValidArithmeticExpression(newValToken)
                                || (newValToken.startsWith(String.valueOf(SyntaxUtils.QUOTES))
                                && newValToken.startsWith(String.valueOf(SyntaxUtils.QUOTES)))) {
//                            System.out.println("\t Line " + lineIndex + ": ASSIGNATION line is OK");

                        } else {
                            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_ARITHMETIC_EXPRESSION));
                        }

                    } else {
                        throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_ASSIGNATION));
                    }
                } else {
                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
                }
            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_VARIABLE_NAME));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        }
    }

    public static String[] refactorTokensForAssignationLine(String[] lineTokens) {
        ArrayList<String> lineTokensAux = new ArrayList<>();
        String aux = "";
        for (int i = 3; i < lineTokens.length; i++) {
            aux += lineTokens[i];
        }
        lineTokensAux.add(lineTokens[0]);
        lineTokensAux.add(lineTokens[1]);
        lineTokensAux.add(lineTokens[2]);
        lineTokensAux.add(aux);

        return lineTokensAux.toArray(new String[lineTokensAux.size()]);
    }

    public static void validateIfLine(int lineIndex, String line) throws Exception {

        String[] lineTokens = deleteSpaces(line).split(" ");
        //
        lineTokens = refactorTokensForIfLine(lineTokens);

        //ACÁ SE CAMBIA SI LA EXPRESIÓN LÓGICA TIENE ESPACIOS TIENE ESPACIOS    
        if (lineTokens.length < 5) {
            //ACÁ SE CAMBIA SI LA EXPRESIÓN LÓGICA TIENE ESPACIOS TIENE ESPACIOS 
            if (lineTokens.length == 4) {
                //Extract logExpr and THEN reserved word
                String logExp = lineTokens[2];
                String thenToken = lineTokens[3];

//                System.out.println("Log Expr is: " + logExp);
                if (isValidLogicExpression(logExp)) {

                    if (thenToken.length() == 4 && thenToken.toUpperCase().equals("THEN")) {

//                        System.out.println("\t Line " + lineIndex + ": IF line is OK");
                    } else {
                        throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_NOT_THEN_FOUND));
                    }
                } else {
                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_LOGIC_EXPRESSION));
                }
            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        }

    }

    public static String[] refactorTokensForWhileLine(String[] lineTokens) {
        ArrayList<String> lineTokensAux = new ArrayList<>();
        String aux = "";
        int counter = 0;
        lineTokensAux.add(lineTokens[0]);
        lineTokensAux.add(lineTokens[1]);
        for (int i = 2; i < lineTokens.length; i++) {
            aux += lineTokens[i];
        }

        lineTokensAux.add(aux);

        return lineTokensAux.toArray(new String[lineTokensAux.size()]);
    }

    public static String[] refactorTokensForIfLine(String[] lineTokens) {
        ArrayList<String> lineTokensAux = new ArrayList<>();
        String aux = "";
        int counter = 0;
        lineTokensAux.add(lineTokens[0]);
        lineTokensAux.add(lineTokens[1]);
        for (int i = 2; i < lineTokens.length; i++) {
            if (!lineTokens[i].equals("THEN")) {
                aux += lineTokens[i];
            } else {
                counter = i;
                lineTokensAux.add(aux);
                break;
            }
        }
        for (int i = counter; i < lineTokens.length; i++) {
            lineTokensAux.add(lineTokens[i]);
        }
        return lineTokensAux.toArray(new String[lineTokensAux.size()]);
    }

    private static void validateWhileLine(int lineIndex, String line) throws Exception {
        String[] lineTokens = deleteSpaces(line).split(" ");

        //AQUI TOCA MIRAR SI LA EXPRESIÓN LÓGICA DEL WHILE VA CON O SIN ESPACIOS
        //ES DECIR, SI VA POR EJEMPLO
        //  100 WHILE A > B
        //  100 WHILE A>B
        if (lineTokens.length < 100) {
            String logExpToken = buildNewStringFromIndex(2, lineTokens);
//            System.out.println("\n logic exp in while is " + logExpToken + "\n");
            if (isValidLogicExpression(logExpToken)) {
//                System.out.println("\t Line " + lineIndex + ": WHILE line is OK");
            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_LOGIC_EXPRESSION));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        }
        // } else {
        //   throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        //}
    }

    private static void validateGotoLine(int lineIndex, String line) throws Exception {
        String[] lineTokens = line.split(" ");

        if (lineTokens.length < 4) {

            if (lineTokens[2] != null) {

                if (isValidLineNumber(lineTokens[2])) {

//                    System.out.println("\t Line " + lineIndex + ": GOTO line is OK");
                } else {
                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_LINE_NUMBER));
                }
            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        }
    }

    public static boolean isValidLogicExpression(String logicExpression) {
        //ESTE CASO FALLA SI 100 IF RAND > 0

        //HACER SPLIT POR LOS OPERADORES LÓGICOS AND,OR YA QUE ELLOS SOLO CUMPLEN LA
        //FUNCIÓN DE CONCATENAR SUBEXPRESIONES LÓGICAS QUE INVOLUCRAN COMPARACIÓN
        // (>, <, >=, >= ==)
//        
        Matcher m = Pattern.compile("AND|OR").matcher(logicExpression);
        int andOrOcurrences = 0;
        while (m.find()) {
            andOrOcurrences++;
        }

        String[] subLogicExpressions = logicExpression.split("AND|OR");

        if (andOrOcurrences == subLogicExpressions.length - 1) {
            for (String subLogicExpression : subLogicExpressions) {
//                System.out.println("sub exp before method is " + subLogicExpression);
                subLogicExpression = subLogicExpression.substring(1, subLogicExpression.length() - 1);
//                System.out.println("sub exp after method is " + subLogicExpression);
                if (!isValidSubLogicExpression(subLogicExpression)) {
//                    System.out.println("se daño en is valid logic expresion");
                    return false;
                }
            }
        } else {
            return false;
        }

        //Para el caso más básico que es (ARITEXP LOGOPER ARITEXPR)
        /* String[] logExpTokens = logicExpression.split(" ");
        return isValidArithmeticExpression(logExpTokens[0])
                && (logExpTokens[1].equals("AND") || logExpTokens[1].equals("OR")
                && isValidArithmeticExpression(logExpTokens[2]));*/
        return true;
    }

    public static boolean isValidSubLogicExpression(String sublogicExpression) {
        //LA SUBEXPRESIÓN LÓGICA TIENE LA SIGUIENTE ESTRUCTURA
        //EXPRESIÓN ARITMÉTICA - OPERADOR LÓGICO DE COMPARACIÓN - EXPRESIÓN ARITMÉTICA

        //ENCUENTRA QUÉ OPERADOR LÓGICO DE COMPARACIÓN UTILIZA LA EXPRESIÓN
        String operator = findOperatorInSublogicExpression(sublogicExpression);
//        System.out.println("operator is " + operator);

        if (!operator.equals("")) {

            int indexOfOperator = sublogicExpression.indexOf(operator);
            int lengthOfOperator = operator.length();

            //CREA NUEVO STRING CON LA PRIMERA EXPRESIÓN ARITMÉTICA DESDE INDEX 0
            //HASTA EL INDEX DEL OPERADOR LÓGICO DE COMPARACIÓN
            String firstAritExpr = sublogicExpression.substring(0, indexOfOperator);

            //CREA NUEVO STRING CON LA SEGUNDA EXPRESIÓN ARITMÉTICA DESDE INDEX DONDE
            //EMPIEZA EL OPERADOR + LA LONGITUD DEL OPERADOR HASTA EL FINAL DE LA
            //SUBEXPRESIÓN LÓGICA
            String secondAritExpr = sublogicExpression.substring(indexOfOperator + lengthOfOperator, sublogicExpression.length());

//            System.out.println(firstAritExpr + " & " + secondAritExpr);
            if (isValidArithmeticExpression(firstAritExpr)
                    && isValidArithmeticExpression(secondAritExpr)) {

                return true;
            } else {
                //SI ALGUNA DE LAS DOS EXPRESIONES ARITMÉTICAS NO ES VÁLIDA
                return false;
            }
        } else {
            //SI NO HAY ALGÚN OPERADOR DENTRO DE LA SUBEXPRESIÓN LÓGICA
            return false;
        }
    }

    public static String findOperatorInSublogicExpression(String sublogicExpression) {
        for (String operator : SyntaxUtils.LOGIC_EXPRESSION_COMPARATORS) {
            if (sublogicExpression.contains(operator)) {
                return operator;
            }
        }
        return "";
    }

    private static boolean areValidVariablesNames(String[] variablesList) {
        for (int i = 0; i < variablesList.length; i++) {
            String variableName = variablesList[i].toUpperCase();
            if (!isValidVariableName(variableName)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAnOperator(char c) {
        switch (c) {
            case '*':
            case '/':
            case '+':
            case '-':
            case '%':
                return true;
            default:
                return false;
        }
    }

    public static boolean isANumberOrValidVariableChar(char c) {
        return (((int) c) >= 48 && ((int) c) <= 57) || (((int) c) >= 65 && ((int) c) <= 90) || (((int) c) >= 97 && ((int) c) <= 122) || c == '.';
    }

    /**
     *
     * @param expression
     * @return true If param expression is lexicographically correct, which
     * means it doesn't contain characters different to numbers, basic
     * aritmethic operators and parentheses
     */
    public static boolean isValidArithmeticExpression(String expression) {
        // TEST 1
        if (isAnOperator(expression.charAt(0)) || isAnOperator(expression.charAt(expression.length() - 1))) {
            return false;
        }

        int openParenthCount = 0;
        boolean lastWasOp = false;
        boolean lastWasOpen = false;

        for (char c : expression.toCharArray()) {
            if (c == ' ') {
                continue;
            }
            if (c == '(') {
                openParenthCount++;
                lastWasOpen = true;
                continue;
            } else if (c == ')') {
                if (openParenthCount <= 0 || lastWasOp) {
                    return false;
                }
                openParenthCount--;
            } else if (isAnOperator(c)) {
                if (lastWasOp || lastWasOpen) {
                    return false;
                }
                lastWasOp = true;
                continue;
            } else if (!isANumberOrValidVariableChar(c)) {
                return false;
            }
            lastWasOp = false;
            lastWasOpen = false;
        }
        if (openParenthCount != 0) {
            return false;
        }
        if (lastWasOp || lastWasOpen) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param character
     * @return true if character equals to one of the ALLOWED_CHARS chars
     */
    private static boolean isValidCharacter(char character) {
        for (int i = 0; i < SyntaxUtils.ARITHMETIC_EXPRESSION_CHARS.length; i++) {
            if (character == SyntaxUtils.ARITHMETIC_EXPRESSION_CHARS[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidVariableName(String variableName) {
        return variableName.matches("[a-zA-Z0-9]+") && !SyntaxUtils.RESERVED_WORDS.stream().anyMatch(variableName::equals);
    }

    private static boolean areValidPrintableTokens(String[] printableTokens) {
        for (String printableToken : printableTokens) {
            //Only check printable tokens that are not messages
            if (!printableToken.contains(String.valueOf(SyntaxUtils.QUOTES))) {
                if (!isValidVariableName(printableToken)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isValidLineNumber(String lineNumber) {
        return lineNumber.length() < 4 && lineNumber.chars().allMatch(Character::isDigit);
    }

    public static boolean isNumeric(String numberAsString) {
        return numberAsString.matches("[+-]?\\d*(\\.\\d+)?");
    }

    public static String buildNewStringFromIndex(int fromIndex, String[] tokens) {
        StringBuilder builder = new StringBuilder();
        for (int i = fromIndex; i < tokens.length; i++) {
            builder.append(tokens[i]);
        }
        return builder.toString();
    }
    
    public static String buildNewStringFromIndex2(int fromIndex, String[] tokens) {
        StringBuilder builder = new StringBuilder();
        for (int i = fromIndex; i < tokens.length; i++) {
            builder.append(tokens[i]).append(" ");
        }
        return builder.toString();
    }

//    public static void main(String[] args) {
//        String coso = "190                      WHILE  Cont < Pow ";
//        System.out.println(coso + coso.contains("\t"));
//        coso = deleteSpaces(coso);
//        System.out.println(coso + coso.contains("\t"));
//    }
}
