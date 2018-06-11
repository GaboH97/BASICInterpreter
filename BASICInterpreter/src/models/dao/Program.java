package models.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
        buildProgram();
    }

    public void buildProgram() {
        for (Line codeLine : codeLines) {
            if (codeLine.getLineType() != null) {
                if (codeLine.getLineType().equals(LineType.DIM)) {
                    //OBTIENE CONTENIDO DE LA LÍNEA
                    String lineContent = codeLine.getText();
                    //OBTIENE LOS TOKENS DE LA LÍNEA
                    String[] lineTokens = lineContent.split(" ");
                    //OBTIENE LA LISTA DE VARIABLES
                    String[] varList = lineTokens[2].split(",");
                    //OBTIENE TIPO DE VARIABLE
                    String variableType = lineTokens[4];

                    VariableType varType = VariableType.valueOf(variableType);

                    loadVariablesInProgram(varList, varType);
                }
            }else{ //AQUI HACE MODIFICACIONES A LAS VARIABLES
                
            }
        }
    }

    public void loadVariablesInProgram(String[] varList, VariableType varType) {
        for (int i = 0; i < varList.length; i++) {
            String variableName = varList[i];
            if (!variables.containsKey(variableName)) {
                Variable variable = new Variable(variableName, varType);
                variables.put(variableName, variable);
            } else {
                System.out.println("ya existe "+variableName);
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

}
