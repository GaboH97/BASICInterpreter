package models.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public void loadCodeLines(ArrayList<String> textLines) {
        textLines.forEach((textLine) -> {
            loadLine(textLine);
        });
        try {
            buildProgram();
        } catch (Exception ex) {
            Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buildProgram() throws Exception {

        //AQUI SE CONVIERTE EL CÓDIGO A LENGUAJE JAVA
        for (Line codeLine : codeLines) {
            if (codeLine.getLineType() != null) {
                switch (codeLine.getLineType()) {
                    case DIM:
                        processDimLine(codeLine);
                        break;
                    case PRINT:
                        //TODO
                        processPrintLine(codeLine);
                        break;
                    case INPUT:
                        //TODO
                        processInputLine(codeLine);
                        break;
                    case WHILE:
                        processWhileLine(codeLine);
                        break;
                    case IF:
                        processIfLine(codeLine);
                        break;
                    case END:
                        processEndLine(codeLine);
                        break;
                    case GOTO:
                        processGotoLine(codeLine);
                        break;
                    case ENDIF:
                        processEndifLine(codeLine);
                        break;
                    case WEND:
                        processWendLine(codeLine);
                        break;
                    case ELSE:
                        processElseLine(codeLine);
                        break;
                }
            } else { //AQUI HAY UNA ASIGNACIÓN
                processAssignationLine(codeLine);
            }
        }
    }

    //===================== PROCESAMIENTO DE LÍNEAS DE CÓDIGO ==================
    private void processDimLine(Line codeLine) throws Exception {
        //OBTIENE CONTENIDO DE LA LÍNEA
        String lineContent = codeLine.getText();
        //OBTIENE LOS TOKENS DE LA LÍNEA
        String[] lineTokens = lineContent.split(" ");
        //OBTIENE LA LISTA DE VARIABLES
        String[] varList = lineTokens[2].split(",");
        //OBTIENE TIPO DE VARIABLE
        String variableType = lineTokens[4];

        VariableType varType = VariableType.valueOf(variableType);

        loadVariablesInProgram(varList, varType, codeLine.getLineNumber());
    }

    private void processPrintLine(Line codeLine) {
        //OBTIENE CONTENIDO DE LA LÍNEA
        String lineContent = codeLine.getText();
        //OBTIENE LOS TOKENS DE LA LÍNEA
        String[] lineTokens = lineContent.split(" ");
        //OBTIENE LA LISTA DE PARÁMETROS
        String[] paramList = lineTokens[2].split(";");

        try {
            String outputPrintLine = buildOutputPrintLine(codeLine.getLineNumber(), paramList);
            System.out.println(outputPrintLine);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String buildOutputPrintLine(int lineNumber, String[] paramList) throws Exception {
        StringBuilder builder = new StringBuilder();
        for (String param : paramList) {
            //ES UN LITERAL ENTRE COMILLAS
            if (param.startsWith(String.valueOf(SyntaxUtils.QUOTES))
                    && param.endsWith(String.valueOf(SyntaxUtils.QUOTES))) {
                builder.append(param);
            } else {
                //ES UNA VARIABLE DE LA CUAL SE EXTRAE SU VALOR
                Variable var = this.variables.get(param);
                if (var != null) {
                    //SI LA VARIABLE HA SIDO INICIALIZADA, RETORNA SU VALOR
                    //SI NO, RETORNA EL LITERAL "NULL"
                    builder.append((var.getValue() != null) ? var.getValue() : "NULL");
                } else {
                    throw new Exception(SyntaxUtils.buildOutputErrorMessage(lineNumber,
                            "Variable "
                            + SyntaxUtils.QUOTES
                            + param + SyntaxUtils.QUOTES
                            + " no ha sido declarada"));
                }
            }
        }
        return builder.toString();
    }

    private void processInputLine(Line codeLine) {
    }

    private void processWhileLine(Line codeLine) {
    }

    private void processIfLine(Line codeLine) {
    }

    private void processEndLine(Line codeLine) {
    }

    private void processGotoLine(Line codeLine) {
    }

    private void processEndifLine(Line codeLine) {
    }

    private void processWendLine(Line codeLine) {
    }

    private void processElseLine(Line codeLine) {
    }

    private void processAssignationLine(Line codeLine) throws Exception {
        //OBTIENE CONTENIDO DE LA LÍNEA
        String lineContent = codeLine.getText();
        //OBTIENE LOS TOKENS DE LA LÍNEA
        String[] lineTokens = lineContent.split(" ");
        //OBTIENE LA VARIABLE QUE SE DESEA MODIFICAR

        String varName = lineTokens[1];
        Variable var = this.variables.get(varName);

        if (var != null) {

            String newVarValue = lineTokens[3];

            if (var.getVariableType().equals(VariableType.DOUBLE)) {
                var.setValue(solveAritmethicExpression(newVarValue, codeLine.getLineNumber()));
            } else {
                var.setValue(getStringValue(newVarValue, codeLine.getLineNumber()));
            }

        } else {
            throw new Exception(SyntaxUtils.buildOutputErrorMessage(codeLine.getLineNumber(),
                    "Variable "
                    + SyntaxUtils.QUOTES
                    + varName + SyntaxUtils.QUOTES
                    + " no ha sido declarada"));
        }

    }

    private Double solveAritmethicExpression(String arithExpr, int lineNumber) {
        return 0.0;
    }

    private String getStringValue(String stringValue, int lineNumber) throws Exception {
        //ES UN LITERAL ENTRE COMILLAS
        if (stringValue.startsWith(String.valueOf(SyntaxUtils.QUOTES))
                && stringValue.endsWith(String.valueOf(SyntaxUtils.QUOTES))) {
            return stringValue;
        } else {
            //HAY UNA ASIGNACIÓN tipo VAR1 = VAR2; DE LA CUAL VAR1 TOMA EL CONTENIDO
            // DE LA VARIABLE VAR2
            Variable var2 = this.variables.get(stringValue);

            if (var2 != null) {
                if (var2.getVariableType().equals(VariableType.STRING)) {
                    return (String) var2.getValue();
                } else {
                    //LOS TIPOS DE LAS VARIABLES SON INCOMPATIBLES
                    throw new Exception(SyntaxUtils.buildOutputErrorMessage(lineNumber,
                            "Variables incompatibles"));
                }
            } else {
                //LA VARIABLE MENCIONADA NO EXISTE
                throw new Exception(SyntaxUtils.buildOutputErrorMessage(lineNumber,
                        "Variable "
                        + SyntaxUtils.QUOTES
                        + stringValue + SyntaxUtils.QUOTES
                        + " no ha sido declarada"));
            }
        }
    }

    public void loadVariablesInProgram(String[] varList, VariableType varType, int lineNumber) throws Exception {
        for (int i = 0; i < varList.length; i++) {
            String variableName = varList[i];
            if (!variables.containsKey(variableName)) {
                Variable variable = new Variable(variableName, varType);
                variables.put(variableName, variable);
            } else {
                throw new Exception(SyntaxUtils.buildOutputErrorMessage(lineNumber,
                        SyntaxUtils.MSG_VARIABLE_ALREADY_DECLARED + ": " + variableName));
            }
        }
    }

    public void loadLine(String textLine) {
        String lineTokens[] = textLine.split(" ");
        int lineNumber = Integer.parseInt(lineTokens[0]);
        String lineTypeToken = lineTokens[1];

        LineType lineType = null;
        try {
            lineType = LineType.valueOf(lineTypeToken.toUpperCase());
        } catch (Exception e) { //CUANDO ES DE ASIGNACIÓN

        }
        Line line = createLine(lineNumber, textLine, lineType);
        if (addLine(line)) {
            System.out.println("Line successfully added!");
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

    /**
     * ESTE MÉTODO TOMA LA EXPRESIÓN ALGEBRÁICA, REVISA LAS VARIABLES (EN
     * LETRAS) QUE CONTIENE Y REEMPLAZA DICHO TEXTO EN LA EXPRESIÓN POR SU VALOR
     * NUMÉRICO PARA LOS DEMÁS SÍMBOLOS, LOS DEJA INTACTOS
     *
     * @param aritmethicExpression
     * @return
     */
    public static String normalizeArithmeticExpression(String aritmethicExpression) {

        StringBuilder builder = new StringBuilder();
        char[] chars = aritmethicExpression.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            //OPERADORES SE AGREGAN NORMALMENTE
            if (c == '(' || c == ')' || c == '+'
                    || c == '-' || c == '*' || c == '/' || c == '^') {
                builder.append(c);
            } else if (Character.isLetterOrDigit(c) || c == '.') {
                StringBuilder buf = new StringBuilder();
                //CONSTRUYE EL LITERAL O VARIABLE HASTA QUE VUELVA A ENCONTRAR
                //OTRO OPERADOR
                while (i < chars.length && (Character.isLetterOrDigit(chars[i]) || c == '.')) {
                    buf.append(chars[i++]);
                }
            }
        }
        return builder.toString();
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

    public static void main(String[] args) {

    }

}
