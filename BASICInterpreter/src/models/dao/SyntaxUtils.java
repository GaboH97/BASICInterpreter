package models.dao;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author user
 */
public class SyntaxUtils {

    //------------------- SYNTAX ERROR MESSAGES -----------------------
    public static final String SPACE = " ";
    public static final char QUOTES = '"';
    public static final String ASSIGNATION = "=";
    public static final char[] ARITHMETIC_EXPRESSION_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '/', '(', ')', ' ', '.'};
    public static final List<String> LOGIC_EXPRESSION_COMPARATORS = Arrays.asList(
            ">=",
            "<=",
            "==",
            ">",
            "<"
    );
    public static final List<String> RESERVED_WORDS = Arrays.asList(
            "DIM",
            "AS",
            "STRING",
            "DOUBLE",
            "IF",
            "THEN",
            "ELSE",
            "ENDIF",
            "WHILE",
            "WEND",
            "PRINT",
            "GOTO",
            "END",
            "AND",
            "OR",
            "INPUT");

    public static final String MSG_INVALID_LINE_NUMBER = "Número de línea inválido";
    public static final String MSG_NOT_DIM_FOUND = "Esperaba palabra reservada DIM";
    public static final String MSG_NOT_PRINT_FOUND = "Esperaba palabra reservada PRINT";
    public static final String MSG_INVALID_VARIABLE_NAME = "Nombre de variable inválido";

    public static final String MSG_INCOMPLETE_STATEMENT = "Sentencia incompleta";
    public static final String MSG_INVALID_DATATYPE = "Tipo de dato inválido";
    public static final String MSG_INVALID_PRINTABLE_CODE = "Argumento a imprimir inválido";
    public static final String MSG_TOO_MUCH_TOKENS = "Estructura de línea inválida";
    public static final String MSG_NOT_INPUT_FOUND = "Esperaba palabra reservada INPUT";
    public static final String MSG_WHILE_NOT_CLOSED = "Sentencia WHILE sin cerrar";
    public static final String MSG_IF_NOT_CLOSED = "Sentencia IF sin cerrar";
    public static final String MSG_PROGRAM_HAS_NOT_ENDED = "No se ha encontrado sentencia END que finalice el programa";
    public static final String MSG_INVALID_ASSIGNATION = "Asignación inválida";
    public static final String MSG_INVALID_ARITHMETIC_EXPRESSION = "Expresión algebráica inválida";
    public static final String MSG_INVALID_LOGIC_EXPRESSION = "Expresión lógica inválida";
    public static final String MSG_STATEMENTS_AFTER_END = "Sentencia inválida ya que había terminado programa";
    public static final String MSG_IF_NOT_OPENED = "ENDIF sin IF";
    public static final String MSG_WHILE_NOT_OPENED = "WEND sin WHILE";
    public static final String MSG_NOT_THEN_FOUND = "Esperaba palabra reservada THEN";
    public static final String MSG_LINE_ALREADY_EXISTS = "Número de línea duplicado";
    public static final String MSG_CANNOT_DECLARE_VARIABLE = "No se puede declarar variables aquí";
    public static final String MSG_EMPTY_FILE = "Archivo vacío";

    public static String buildOutputErrorMessage(int lineIndex, String message) {
        return "at line " + lineIndex + ": " + message;
    }

}
