package models.dao;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static models.dao.SyntaxUtils.buildOutputErrorMessage;
import models.entity.LineType;

/**
 *
 * @author user
 */
public class Validator {

    private boolean isWhileOpen;
    private boolean isIfOpen;
    private boolean hasEnded;

    public Validator() {
        isWhileOpen = false;
        isIfOpen = false;
        hasEnded = false;
    }

    public void validateCodeLines(ArrayList<String> lines) throws Exception {
        for (int i = 0; i < lines.size(); i++) {
            validateLine(i, lines.get(i));
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
        System.out.println("Succesfully validated!");
    }

    public static String deleteSpaces(String str) {    //custom method to remove multiple space
        StringBuilder sb = new StringBuilder();
        for (String s : str.split(" ")) {
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
                    try {
                        switch (LineType.valueOf(identifierToken)) {
                            case DIM:
                                System.out.println("Línea " + lineIndex + " es DIM");
                                validateDIMLine(lineIndex, line);
                                break;
                            case PRINT:
                                System.out.println("Línea " + lineIndex + " es PRINT");
                                validatePrintLine(lineIndex, line);
                                break;
                            case INPUT:
                                System.out.println("Línea " + lineIndex + " es INPUT");
                                validateInputLine(lineIndex, line);
                                break;
                            case IF:
                                System.out.println("Línea " + lineIndex + " es IF");
                                validateIfLine(lineIndex, line);
                                isIfOpen = true;
                                break;
                            case ENDIF:
                                if (isIfOpen && !isWhileOpen) {
                                    System.out.println("Línea " + lineIndex + " es ENDIF");
                                    validateElseEndEndIfWendLine(lineIndex, line);
                                    isIfOpen = false;
                                } else {
                                    System.out.println("entro aqui");
                                    throw new Exception(buildOutputErrorMessage(lineIndex, "perra vida"));
                                }
                                break;
                            case WHILE:
                                System.out.println("Línea " + lineIndex + " es WHILE");
                                validateWhileLine(lineIndex, line);
                                isWhileOpen = true;
                                break;
                            case WEND:
                                if (isWhileOpen) {
                                    System.out.println("Línea " + lineIndex + " es WEND");
                                    validateElseEndEndIfWendLine(lineIndex, line);
                                    isWhileOpen = false;
                                } else {
                                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_WHILE_NOT_OPENED));

                                }
                                break;
                            case GOTO:
                                System.out.println("Línea " + lineIndex + " es GOTO");
                                validateGotoLine(lineIndex, line);
                                break;
                            case END:
                                System.out.println("Línea " + lineIndex + " es END");
                                validateElseEndEndIfWendLine(lineIndex, line);
                                hasEnded = true;
                                break;
                            case ELSE:
                                if (isIfOpen && !isWhileOpen) {
                                    System.out.println("Línea " + lineIndex + " es ELSE");
                                    validateElseEndEndIfWendLine(lineIndex, line);
                                    hasEnded = true;
                                    break;
                                } else {
                                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_IF_NOT_OPENED));
                                }
                        }
                    } catch (Exception e) {
                        //ESTE CASO ES PARA ASIGNACIÓN DE VARIABLE
                        e.printStackTrace();
                        System.out.println("Línea " + lineIndex + " es de asignación ");
                        validateAssignationLine(lineIndex, line);
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

                System.out.println("\t Line " + lineIndex + ": INPUT line is OK");

            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_VARIABLE_NAME));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
        }

    }

    public static void validatePrintLine(int lineIndex, String line) throws Exception {

        String[] lineTokens = line.split(" ");
        //Build a new token out of the original lineToken array starting
        //at an specific index

        String tokenAux = buildNewStringFromIndex(2, lineTokens);

        String[] printableTokens = tokenAux.split(";");

        long countQuotes = tokenAux.chars().filter(num -> num == SyntaxUtils.QUOTES).count();
        long countdotAndComma = tokenAux.chars().filter(num -> num == ';').count();

        //Check if number of quotes is odd and every dot and comma is
        //enclosed between two printable tokens
        if (((countQuotes & 1) == 0) && (printableTokens.length - 1 == countdotAndComma)) {

            if (areValidPrintableTokens(printableTokens)) {
                System.out.println("\t Line " + lineIndex + ": PRINT line is OK");

            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_VARIABLE_NAME));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_PRINTABLE_CODE));
        }

    }

    public static void validateDIMLine(int lineIndex, String line) throws Exception {
        //Trim string to only one space between words
        String trimmedLine = deleteSpaces(line);
        //Split line into spaces
        String[] lineTokens = trimmedLine.split(" ");

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

                            System.out.println("\t Line " + lineIndex + ": DIM line is OK");

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

    public void validateElseEndEndIfWendLine(int lineIndex, String line) throws Exception {

        String trimmedLine = deleteSpaces(line);

        //Split line into spaces
        String[] lineTokens = trimmedLine.split(" ");

        if (lineTokens.length < 3) {
            System.out.println("\t Line " + lineIndex + ": END/ENDIF/WEND line is OK");
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        }

    }

    public static void validateAssignationLine(int lineIndex, String line) throws Exception {
        String[] lineTokens = line.split(" ");
        if (lineTokens.length < 5) {
            if (isValidVariableName(lineTokens[1])) {

                if (lineTokens[2] != null && lineTokens[3] != null) {

                    if (lineTokens[2].equals(SyntaxUtils.ASSIGNATION)) {

                        if (isValidArithmeticExpression(lineTokens[3])) {
                            System.out.println("\t Line " + lineIndex + ": ASSIGNATION line is OK");

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

    public static void validateIfLine(int lineIndex, String line) throws Exception {

        String[] lineTokens = line.split(" ");

        //ACÁ SE CAMBIA SI LA EXPRESIÓN LÓGICA TIENE ESPACIOS TIENE ESPACIOS    
        if (lineTokens.length < 5) {
            //ACÁ SE CAMBIA SI LA EXPRESIÓN LÓGICA TIENE ESPACIOS TIENE ESPACIOS 
            if (lineTokens.length == 4) {
                //Extract logExpr and THEN reserved word
                String logExp = lineTokens[2];
                String thenToken = lineTokens[3];

                System.out.println("Log Expr is: " + logExp);
                if (isValidLogicExpression(logExp)) {

                    if (thenToken.length() == 4 && thenToken.toUpperCase().equals("THEN")) {

                        System.out.println("\t Line " + lineIndex + ": IF line is OK");

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


        /*
        if (lineTokens.length < 5) {

            if (lineTokens[2] != null && lineTokens[3] != null) {

                if (isValidLogicExpression(lineTokens[2])) {

                    if (lineTokens[3].toUpperCase().equals("THEN")) {

                        System.out.println("\t Line " + lineIndex + ": IF line is OK");

                    } else {
                        throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
                    }
                } else {
                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_LOGIC_EXPRESSION));
                }
            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        }*/
    }

    private static void validateWhileLine(int lineIndex, String line) throws Exception {
        String[] lineTokens = line.split(" ");

        //AQUI TOCA MIRAR SI LA EXPRESIÓN LÓGICA DEL WHILE VA CON O SIN ESPACIOS
        //ES DECIR, SI VA POR EJEMPLO
        //  100 WHILE A > B
        //  100 WHILE A>B
        if (lineTokens.length < 100) {
            String logExpToken = buildNewStringFromIndex(2, lineTokens);

            if (isValidLogicExpression(logExpToken)) {
                System.out.println("\t Line " + lineIndex + ": WHILE line is OK");
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

                    System.out.println("\t Line " + lineIndex + ": GOTO line is OK");

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
        Matcher m = Pattern.compile("AND|OR").matcher(logicExpression);
        int andOrOcurrences = 0;
        while (m.find()) {
            andOrOcurrences++;
        }

        System.out.println("Occurences: " + andOrOcurrences);
        String[] subLogicExpressions = logicExpression.split("AND|OR");
        if (andOrOcurrences == subLogicExpressions.length - 1) {
            for (String subLogicExpression : subLogicExpressions) {
                System.out.println("sublogic expression is: " + subLogicExpression);
                if (!isValidSubLogicExpression(subLogicExpression)) {

                    return false;
                }
            }
        } else {
            System.out.println("aqui");
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
        return ((int) c) >= 48 && ((int) c) <= 57;
    }

    /**
     *
     * @param expression
     * @return true If param expression is lexicographically correct, which
     * means it doesn't contain characters different to numbers, basic
     * aritmethic operators and parentheses
     */
    public static boolean isValidArithmeticExpression(String expression) {
        System.out.println("Aritmetic expression is " + expression);
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

    public static String buildNewStringFromIndex(int fromIndex, String[] tokens) {
        StringBuilder builder = new StringBuilder();
        for (int i = fromIndex; i < tokens.length; i++) {
            builder.append(tokens[i]);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        Validator val = new Validator();

        ArrayList<String> lines = new ArrayList<>();
        String dimLine = "001 DIM VAR AS DOUBLE ";
        String printLine = "101 PRINT "
                + SyntaxUtils.QUOTES + "Hello" + SyntaxUtils.QUOTES + ";"
                + SyntaxUtils.QUOTES + "Its me" + SyntaxUtils.QUOTES + ";"
                + "VAR2";
        String inputLine = "102 INPUT VAR1";
        String inputLine2 = "103 INPUT VAR1";
        String inputLine3 = "104 INPUT VAR1";
        String inputLine4 = "105 INPUT VAR1";
        String validationLine = "111 VAR1 = 0+0";
        String assignationLine = "102 X = 1+0";
        String ifLine = "110 IF ((1>6)AND(5==7)) THEN";
        String ifLine2 = "110 IF 1>0AND2>0OR2==100*(2*((12+6)/5))/14 THEN";
        String endIfLine = "115 ENDIF";
        String whileLine = "110 WHILE r AND x OR s";
        String wendLine = "110 WEND";
        String endLine = "110 END";

        // lines.add(dimLine);
        //lines.add(printLine);
        // lines.add(inputLine);
        // lines.add(inputLine2);
        // lines.add(inputLine3);
        // lines.add(inputLine4);
        // lines.add(validationLine);
        // lines.add(assignationLine);
        lines.add(ifLine2);
        lines.add(endIfLine);
        //lines.add(whileLine);
        //lines.add(wendLine);
        lines.add(endLine);

        try {
            val.validateCodeLines(lines);
            //val.validateDIMLine(1, dimLine);
            //val.validatePrintLine(2, printLine);
            //val.validateInputLine(3, inputLine);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
