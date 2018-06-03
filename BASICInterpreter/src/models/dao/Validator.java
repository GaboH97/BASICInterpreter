package models.dao;

import java.util.ArrayList;
import static models.dao.SyntaxUtils.ASSIGNATION;
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

        /*if (!hasEnded) {
            throw new Exception(buildOutputErrorMessage(lines.size(), SyntaxUtils.MSG_PROGRAM_HAS_NOT_ENDED));
        }*/
        System.out.println("Succesfully validated!");
    }

    public static String deleteSpaces(String str) {    //custom method to remove multiple space
        StringBuilder sb = new StringBuilder();
        for (String s : str.split(" ")) {
            if (!s.equals("")) // ignore space
            {
                sb.append(s).append(" ");       // add word with 1 space
            }
        }
        return sb.toString();
    }

    public static void validateLine(int lineIndex, String line) throws Exception {
        String trimmedLine = deleteSpaces(line);

        //Split line into spaces
        String[] lineTokens = trimmedLine.split(" ");

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
                            System.out.println("Línea " + lineIndex + " es DIM");
                            validateIfLine(lineIndex, line);
                            break;
                        case ENDIF:
                            System.out.println("Línea " + lineIndex + " es ENDIF");
                            validateEndEndIfWendLine(lineIndex, line);
                            break;
                        case WHILE:
                            System.out.println("Línea " + lineIndex + " es WHILE");
                            validateWhileLine(lineIndex, line);
                            break;
                        case WEND:
                            System.out.println("Línea " + lineIndex + " es WEND");
                            validateEndEndIfWendLine(lineIndex, line);
                            break;
                        case GOTO:
                            System.out.println("Línea " + lineIndex + " es GOTO");
                            validateGotoLine(lineIndex, line);
                            break;
                        case END:
                            System.out.println("Línea " + lineIndex + " es END");
                            validateEndEndIfWendLine(lineIndex, line);
                            break;
                    }
                } catch (Exception e) {
                    //ESTE CASO ES PARA ASIGNACIÓN DE VARIABLE
                    e.printStackTrace();
                    //System.out.println("Línea " + lineIndex + " es de asignación ");
                    validateAssignationLine(lineIndex, line);

                }

            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_LINE_NUMBER));
        }
    }

    //********************** VALIDATIONS *************************
    public static void validateInputLine(int lineIndex, String line) throws Exception {
        //Trim string to only one space between words
        String trimmedLine = deleteSpaces(line);

        //Split line into spaces
        String[] lineTokens = trimmedLine.split(" ");
        //Check is a valid lineNumber
        if (isValidLineNumber(lineTokens[0])) {
            String inputToken = lineTokens[1];
            //Check if second token is reserved word INPUT

            if (inputToken.toUpperCase().equals(LineType.INPUT.name())) {

                if (lineTokens[2] != null) {

                    if (isValidVariableName(lineTokens[2])) {

                        System.out.println("\t Line " + lineIndex + ": INPUT line is OK");

                    } else {
                        throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_VARIABLE_NAME));
                    }
                } else {
                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
                }
            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_NOT_INPUT_FOUND));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_LINE_NUMBER));
        }
    }

    public static void validatePrintLine(int lineIndex, String line) throws Exception {

        //Trim string to only one space between words
        String trimmedLine = deleteSpaces(line);
        //Split line into spaces

        String[] lineTokens = trimmedLine.split(" ");
        //Check is a valid lineNumber

        if (isValidLineNumber(lineTokens[0])) {
            String printToken = lineTokens[1];
            //Check if second token is reserved word DIM 

            if (printToken.toUpperCase().equals(LineType.PRINT.name())) {

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
            } else {
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_NOT_PRINT_FOUND));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_LINE_NUMBER));
        }

    }

    public static void validateDIMLine(int lineIndex, String line) throws Exception {
        //Trim string to only one space between words
        String trimmedLine = deleteSpaces(line);
        //Split line into spaces
        String[] lineTokens = trimmedLine.split(" ");
        //Check is a valid lineNumber
        if (isValidLineNumber(lineTokens[0])) {
            String dimToken = lineTokens[1];
            //Check if second token is reserved word PRINT
            if (dimToken.toUpperCase().equals(LineType.DIM.name())) {
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
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_NOT_DIM_FOUND));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_LINE_NUMBER));
        }
    }

    private static void validateEndEndIfWendLine(int lineIndex, String line) throws Exception {
        String trimmedLine = deleteSpaces(line);

        //Split line into spaces
        String[] lineTokens = trimmedLine.split(" ");

        if (lineTokens.length < 3) {
            System.out.println("\t Line " + lineIndex + ": END/ENDIF/WEND line is OK");
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        }
    }

    private static void validateAssignationLine(int lineIndex, String line) throws Exception {
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

    private static void validateIfLine(int lineIndex, String line) throws Exception {
        String[] lineTokens = line.split(" ");

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
        }
    }

    private static void validateWhileLine(int lineIndex, String line) throws Exception {
        String[] lineTokens = line.split(" ");
        if (lineTokens.length < 4) {

            if (lineTokens.length > 2) {

                if (lineTokens[2] != null) {

                    if (isValidLogicExpression(lineTokens[2])) {

                        System.out.println("\t Line " + lineIndex + ": WHILE line is OK");

                    } else {
                        throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INVALID_LOGIC_EXPRESSION));
                    }
                } else {
                    System.out.println("entro aqui");
                    throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
                }
            } else {
                System.out.println("ento aqui");
                throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_INCOMPLETE_STATEMENT));
            }
        } else {
            throw new Exception(buildOutputErrorMessage(lineIndex, SyntaxUtils.MSG_TOO_MUCH_TOKENS));
        }
    }

    private static void validateGotoLine(int lineIndex, String line) throws Exception {
        String[] lineTokens = line.split(" ");

        if (lineTokens.length < 4) {

            if (lineTokens[2] != null) {

                if (isValidLineNumber(lineTokens[2])) {

                    System.out.println("\t Line " + lineIndex + ": WHILE line is OK");

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

    public static String buildOutputErrorMessage(int lineIndex, String message) {
        return "at line " + lineIndex + ": " + message;
    }

    private static boolean isValidLogicExpression(String logicExpression) {
        return true;
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

    /**
     *
     * @param expression
     * @return true If param expression is lexicographically correct, which
     * means it doesn't contain characters different to numbers, basic
     * aritmethic operators and parentheses
     */
    private static boolean isValidArithmeticExpression(String expression) {
        int parentheses = 0;
        for (int i = 0; i < expression.length(); i++) {
            char character = expression.charAt(i);
            if (!isValidCharacter(character)) {
                return false;
            } else if (character == '(') {
                parentheses++;
            } else if (character == ')') {
                parentheses--;
            }
        }
        return parentheses == 0;
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
        String dimLine = "101 DIM VAR AS DOUBLE";
        String printLine = "101 PRINT "
                + SyntaxUtils.QUOTES + "Hello" + SyntaxUtils.QUOTES + ";"
                + SyntaxUtils.QUOTES + "Its me" + SyntaxUtils.QUOTES + ";"
                + "VAR2";
        String inputLine = "102 INPUT VAR1";
        String inputLine2 = "103 INPUT VAR1";
        String inputLine3 = "104 INPUT VAR1";
        String inputLine4 = "105 INPUT VAR1";
        //String validationLine = "111 VAR1 = 0+0";
        String endLine = "110 WHILE EXPRESSION";

        lines.add(dimLine);
        lines.add(printLine);
        lines.add(inputLine);
        lines.add(inputLine2);
        lines.add(inputLine3);
        lines.add(inputLine4);
        //  lines.add(validationLine);
        lines.add(endLine);

        try {
            val.validateCodeLines(lines);
            //val.validateDIMLine(1, dimLine);
            //val.validatePrintLine(2, printLine);
            //val.validateInputLine(3, inputLine);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
