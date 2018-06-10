package models.dao;

import java.util.ArrayList;
import java.util.HashMap;
import models.entity.Line;
import models.entity.LineType;

/**
 *
 * @author user
 */
public class Program {

    private HashMap<String, Object> variables;
    private ArrayList<Line> lines;
    private String text;

    public Program() {
        variables = new HashMap<>();
        lines = new ArrayList<>();
    }

    public static Line createLine(int lineNumber, String text, LineType lineType) throws Exception {
            return new Line(0, text, lineType);
        
    }
    
    public boolean addLine(Line line){
        if(!lineAlreadyExists(line.getLineNumber())){
            lines.add(line);
            return true;
        }else{
            return false;
        }
    }

    public boolean lineAlreadyExists(int lineNumber) {
        return lines.stream().anyMatch(line -> line.getLineNumber()==lineNumber);
    }
    
    public static void main(String[] args) {
        
    }
}
