
package algebraicexpressions;

import models.dao.AlgebraicExpressionsAnalyzer;

/**
 *
 * @author Gabriel Huertas
 */
public class AlgebraicExpressions {
    
    public static void main(String[] args) {
        
      /*  System.out.println("---------ALGEBRAIC EXPRESSIONS TREE TEST----------");
        
        
        String algebraicExpression = "((((1+2)*(3-4))*(20+(30/10)))*10)";
       
        AlgebraicExpressionsAnalyzer analyzer = new AlgebraicExpressionsAnalyzer();
        
        String cosito = algebraicExpression.replaceAll("\\s", "");
        if(AlgebraicExpressionsAnalyzer.isAValidExpression(cosito)){
            System.out.println("It's a valid valid expression");
            analyzer.toInfixForm(algebraicExpression);
            System.out.println(analyzer.getInfix().toString());
            try {
                analyzer.toPostfixForm(analyzer.getInfix());
                analyzer.buildAlgebraicExpressionTree();
                System.out.println(analyzer.getAlgebraicExpressionTree().inOrder().toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println(analyzer.getPostfix().toString());
        }*/
      new controllers.Controller();
    }
    
}
