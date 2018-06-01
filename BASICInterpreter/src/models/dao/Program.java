package models.dao;

import java.util.ArrayList;
import java.util.HashMap;
import models.entity.Line;

/**
 *
 * @author user
 */
public class Program {

    private HashMap<String, Object> variables;
    private ArrayList<Line> lines;

    public Program() {
        variables = new HashMap<>();
        lines = new ArrayList<>();
    }

}
