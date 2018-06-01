package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import models.dao.AlgebraicExpressionsAnalyzer;
import views.MainWindow;

/**
 *
 * @author Gabriel Huertas
 */
public class Controller implements ActionListener {

    //-----------------Attributes-------------------
    
    public AlgebraicExpressionsAnalyzer analyzer;
    public MainWindow mainWindow;

    //-----------------Constructors------------------
    public Controller() {
        analyzer = new AlgebraicExpressionsAnalyzer();
        mainWindow = new MainWindow(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (Actions.valueOf(e.getActionCommand())) {
            case SOLVE:
                solve();
                break;
            case EXIT:
                exit();
                break;
        }
    }
    
    /**
     * Solves Algebraic expression and shows results in Window
     */
    private void solve() {
        try {
            String text = mainWindow.getText();
            if (AlgebraicExpressionsAnalyzer.isAValidExpression(text)) {
                analyzer.reset();
                analyzer.toInfixForm(text);
                analyzer.toPostfixForm();
                analyzer.buildAlgebraicExpressionTree();
                mainWindow.showResults(analyzer.getResult(), analyzer.getAlgebraicExpressionTree());
            } else {
                JOptionPane.showMessageDialog(null, "Algebraic expression is not valid", Utilities.GAME_TITLE, JOptionPane.ERROR_MESSAGE, new ImageIcon(getClass().getResource(Utilities.SAD_FACE_IMAGE_PATH)));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), Utilities.GAME_TITLE, JOptionPane.ERROR_MESSAGE, new ImageIcon(getClass().getResource(Utilities.SAD_FACE_IMAGE_PATH)));
        }
    }
    
    /**
     * Terminates execution if required
     */
    private void exit() {
        int opt = JOptionPane.showConfirmDialog(null, "Do you want to quit now?", Utilities.GAME_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(getClass().getResource(Utilities.SAD_FACE_IMAGE_PATH)));
        if (opt == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

}
