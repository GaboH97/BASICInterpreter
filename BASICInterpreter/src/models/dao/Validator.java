package models.dao;

import com.sun.xml.internal.ws.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.entity.LineType;

/**
 *
 * @author user
 */
public class Validator {

    private boolean isWhileOpen;
    private boolean isIfOpen;

    public Validator() {
        isWhileOpen = false;
        isIfOpen = false;
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

    public static void validatePrintLine(int lineIndex, String line) throws Exception {

        //Trim string to only one space between words
        String trimmedLine = deleteSpaces(line);
        System.out.println("with deleted spaces " + trimmedLine);
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
                        System.out.println("Print line is OK");
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
            //Check if second token is reserved word DIM 
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

    public static String buildOutputErrorMessage(int lineIndex, String message) {
        return "at line " + lineIndex + ": " + message;
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
            System.out.println("concatenando " + tokens[i]);
            builder.append(tokens[i]);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        Validator val = new Validator();
        String dimLine = "101 DIM VAR AS DOUBLE";
        String printLine = "101 PRINT "
                + SyntaxUtils.QUOTES + "Hello" + SyntaxUtils.QUOTES + ";"
                + SyntaxUtils.QUOTES + "Its me" + SyntaxUtils.QUOTES + ";"
                + "VAR2";
        try {
            val.validateDIMLine(1, dimLine);
            val.validatePrintLine(2, printLine);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
