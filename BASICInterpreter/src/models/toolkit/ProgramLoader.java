package models.toolkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import models.dao.Program;
import models.dao.SyntaxUtils;
import models.dao.SyntaxValidator;

/**
 *
 * @author user
 */
public class ProgramLoader {

    private static final String MSG_CANNOT_FIND_FILE = "No puede encontrar archivo con nombre: ";

    public ArrayList<String> readProgramLinesFromFile(String fileName) {
        List<String> lines = new ArrayList<>();
        File file = new File("./resources/" + fileName);
        if (file.length() != 0) {
            //read file into stream, try-with-resources
            BufferedReader br = null;
            try {
                br = Files.newBufferedReader(Paths.get(file.getCanonicalPath()));
            } catch (IOException ex) {
                System.out.println(MSG_CANNOT_FIND_FILE + fileName);
            }
            //br returns as stream and convert it into a List
            lines = br.lines().collect(Collectors.toList());
        }
        return (ArrayList<String>) lines;
    }

    public static void main(String[] args) {

        Program program = new Program();
        ProgramLoader programLoader = new ProgramLoader();
        SyntaxValidator validator = new SyntaxValidator();

        Scanner sc = new Scanner(System.in);
        
    
        System.out.println("Ingrese el nombre del archivo ubicado en carpeta resources: \n");
        String fileName = sc.nextLine();
        ArrayList<String> lines = programLoader.readProgramLinesFromFile(fileName);

        if (!lines.isEmpty()) {
            System.out.println("TEXT LINES \n");
            lines.stream().forEach(line -> System.out.println("\t" + line));
            System.out.println("\n");
            try {
                validator.validateCodeLines(lines);
                //program.loadCodeLines(lines);
                //System.out.println(program.toString());
                //System.out.println(program.printVariables());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }

        } else {
            System.out.println(SyntaxUtils.MSG_EMPTY_FILE);
        }
    }
}
