/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.dao;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author user
 */
public class SyntaxUtils {

    //------------------- SYNTAX ERROR MESSAGES -----------------------
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
    public static final String MSG_INVALID_VARIABLE_NAME = "Nombre de variable inválido";

    public static final String MSG_INCOMPLETE_STATEMENT = "Sentencia incompleta";
    public static final String MSG_INVALID_DATATYPE = "Tipo de dato inválido";
}
