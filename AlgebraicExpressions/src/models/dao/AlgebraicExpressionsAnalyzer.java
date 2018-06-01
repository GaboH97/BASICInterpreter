package models.dao;

import java.util.Iterator;
import models.entity.BinaryNode;
import models.entity.BinaryTree;
import models.entity.SimpleList;
import models.entity.SimpleStack;

/**
 *
 * @author Gabriel Huertas
 */
public class AlgebraicExpressionsAnalyzer {

    //---------------------Attributes------------------------
    private BinaryTree algebraicExpressionTree;
    private SimpleStack<String> postFixStack;
    private SimpleList postFix;
    private SimpleList infix;
    public static final char[] ALLOWED_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '/', '(', ')', ' ', '.'};
    private double result;

    //---------------------Constructors----------------------
    public AlgebraicExpressionsAnalyzer() {
        postFixStack = new SimpleStack();
        postFix = new SimpleList();
        infix = new SimpleList();
        algebraicExpressionTree = new BinaryTree();
        result = 0;
    }

    //---------------------Methods---------------------------
    /**
     *
     * @param expression
     * @return true If param expression is lexicographically correct, which
     * means it doesn't contain characters different to numbers, basic
     * aritmethic operators and parentheses
     */
    public static boolean isAValidExpression(String expression) {
        int parentheses = 0;
        for (int i = 0; i < expression.length(); i++) {
            char character = expression.charAt(i);
            if (!isValidCharacter(character)) {
                return false;
            } else if (character == '(') {
                parentheses++;
            } else if (character == ')') {
                parentheses--;
            }
        }
        return parentheses == 0;
    }

    /**
     * Logically resets Game
     */
    public void reset() {
        algebraicExpressionTree.setRoot(null);
        infix = new SimpleList();
        postFix = new SimpleList();
        postFixStack = new SimpleStack<>();
    }

    /**
     *
     * @param character
     * @return true if character equals to one of the ALLOWED_CHARS chars
     */
    private static boolean isValidCharacter(char character) {
        for (int i = 0; i < ALLOWED_CHARS.length; i++) {
            if (character == ALLOWED_CHARS[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Passes entire expression to infix Form
     *
     * @param expression
     */
    public void toInfixForm(String expression) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char character = expression.charAt(i);
            if (Character.isDigit(character) || character == '.') {
                builder.append(character);
            } else {
                if (!builder.toString().isEmpty()) {
                    infix.addToTail(builder.toString());
                    builder.setLength(0);
                }
                infix.addToTail(String.valueOf(character));
            }
        }
    }

    /**
     *
     * @param postfixFormList
     */
    public void toPostfixForm() throws Exception {
        for (Iterator iterator = infix.iterator(); iterator.hasNext();) {
            String subExp = (String) iterator.next();
            if (isFloatNumber(subExp)/*subExp.chars().allMatch(Character::isDigit)*/) { //is a numeric string
                postFix.addToTail(subExp);
            } else if (postFixStack.isEmpty()) {
                postFixStack.pop(subExp);
            } else {
                String nonNumericChar = (String) postFixStack.top();
                if (!nonNumericChar.equals("+") && !nonNumericChar.equals("-")
                        && !nonNumericChar.equals("*") && !nonNumericChar.equals("/")) {
                    postFixStack.pop(subExp);
                } else if (subExp.equals(")")) {
                    String auxNonNumericChar = nonNumericChar;
                    while (!postFixStack.isEmpty()) {
                        if (auxNonNumericChar.equals("(")) {
                            postFixStack.push();
                            break;
                        } else {
                            postFix.addToTail(postFixStack.push());
                            auxNonNumericChar = (String) postFixStack.top();
                        }
                    }
                } else {
                    postFixStack.pop(subExp);
                }
            }
        }
        if (!postFixStack.isEmpty()) {
            String lastOnes = (String) postFixStack.top();
            while (!postFixStack.isEmpty()) {
                postFix.addToTail(lastOnes);
                lastOnes = (String) postFixStack.push();
            }
        }
    }

    /**
     * Builds Binary tree according to algebraic expression's postFix
     * representation
     */
    public void buildAlgebraicExpressionTree() throws Exception {

        SimpleStack valuesStack = new SimpleStack();
        SimpleStack nodesStack = new SimpleStack();

        for (int i = 0; i < postFix.getListSize(); i++) {
            String subExp = (String) postFix.get(i);
            if (isFloatNumber(subExp)/*subExp.chars().allMatch(Character::isDigit)*/) { //Is a numeric value
                valuesStack.pop(subExp);
                nodesStack.pop(new BinaryNode(subExp));
            } else if (valuesStack.isEmpty()) {
                System.out.println("Is empty"); //aquí (se supone...) nunca entra 
            } else {
                String secondValueStr = (String) valuesStack.push();
                String firstValueStr = (String) valuesStack.push();
                if (secondValueStr == null || firstValueStr == null) {
                    throw new Exception("Invalid algebrabic expression");
                } else {

                    double firstValue = Double.parseDouble(firstValueStr);
                    BinaryNode leftChild = (BinaryNode) nodesStack.push();
                    double secondValue = Double.parseDouble(secondValueStr);
                    BinaryNode rightChild = (BinaryNode) nodesStack.push();

                    BinaryNode parent = new BinaryNode(subExp);
                    parent.setRight(rightChild);
                    parent.setLeft(leftChild);

                    result = doOperation(firstValue, secondValue, subExp);
                    algebraicExpressionTree.setRoot(parent);
                    nodesStack.pop(algebraicExpressionTree.getRoot());
                    valuesStack.pop(String.valueOf(result));
                }
            }
        }
    }

    /**
     *
     * @param firstOperand
     * @param secondOperand
     * @param operator
     * @return A third value equal to the operation, designed by the operator
     * param between operands
     * @throws Exception
     */
    public double doOperation(double firstOperand, double secondOperand, String operator) throws Exception {
        double result = 0;
        switch (operator) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "*":
                result = firstOperand * secondOperand;
                break;
            case "/":
                if (secondOperand != 0) {
                    result = firstOperand / secondOperand;
                } else {
                    throw new ArithmeticException("Division by zero not allowed");
                }
                break;
        }
        return result;
    }

    /**
     *
     * @param operand
     * @return true if a String is a representation of a real number -> Allowed
     * decimal part symbol is '.' -> String must contain up to one '.' character
     */
    public boolean isFloatNumber(String operand) {
        boolean numbersOnly = false;
        int pointCounter = 0;
        for (int i = 0; i < operand.length(); i++) {
            char character = operand.charAt(i);
            if (Character.isDigit(character)) {
                numbersOnly = true;
            } else if (character == '.') {
                pointCounter++;
            } else {
                return false;
            }
        }
        return (pointCounter == 0 || pointCounter == 1) && numbersOnly;
    }

    //--------------------Getters and Setters----------------
    public SimpleStack getOperators() {
        return postFixStack;
    }

    public void setOperators(SimpleStack operators) {
        this.postFixStack = operators;
    }

    public SimpleList getPostfix() {
        return postFix;
    }

    public void setPostfix(SimpleList postfix) {
        this.postFix = postfix;
    }

    public SimpleList getInfix() {
        return infix;
    }

    public void setInfix(SimpleList infix) {
        this.infix = infix;
    }

    public BinaryTree getAlgebraicExpressionTree() {
        return algebraicExpressionTree;
    }

    public void setAlgebraicExpressionTree(BinaryTree algebraicExpressionTree) {
        this.algebraicExpressionTree = algebraicExpressionTree;
    }

    public double getResult() {
        return result;
    }

    //--------------------To String---------------------
}
