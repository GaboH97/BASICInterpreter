package models.toolkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import models.dao.Validator;

/**
 *
 * @author user
 */
public class ProgramLoader {

    private static final String MSG_CANNOT_FIND_FILE = "No puede encontrar archivo con nombre: ";

    public ArrayList<String> readProgramLinesFromFile(String fileName) {
        List<String> lines = new ArrayList<>();
        File file = new File("src/resources/" + fileName);
        //read file into stream, try-with-resources
        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(Paths.get(file.getCanonicalPath()));
        } catch (IOException ex) {
            Logger.getLogger(ProgramLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        //br returns as stream and convert it into a List
        lines = br.lines().collect(Collectors.toList());
        return (ArrayList<String>) lines;
    }

    public static void main(String[] args) {

        ProgramLoader programLoader = new ProgramLoader();
        Validator validator = new Validator();
        String fileName = "Program_1.txt";
        ArrayList<String> lines = programLoader.readProgramLinesFromFile(fileName);
        if (!lines.isEmpty()) {
            System.out.println("PROGRAM LINES \n");
            lines.stream().forEach(line -> System.out.println("\t" + line));
            try {
                validator.validateCodeLines(lines);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
