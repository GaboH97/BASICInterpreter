package models.entity;

import java.util.Stack;

/**
 *
 * @author user
 */
public class ArithmeticExpParser {

    public static double solveArithmeticExpression(String expression) {

        String trimmedExpression = expression.replaceAll(" ", "");

        char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
        Stack<Double> values = new Stack<Double>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<Character>();

        for (int i = 0; i < tokens.length; i++) {
            
            //HASTA EL MOMENTO SOLO FUNCIONA CON NÚMEROS ENTEROS
            
            // Current token is a number, push it to stack for numbers
            if (Character.isLetterOrDigit(tokens[i])) {
                StringBuffer sbuf = new StringBuffer();

                // There may be more than one digits in number
                while (i < tokens.length && Character.isLetterOrDigit(tokens[i])) {
                    sbuf.append(tokens[i++]);
                }
                values.push(Double.parseDouble(sbuf.toString()));
            } // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(') {
                ops.push(tokens[i]);
            } // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(doOperation(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            } // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-'
                    || tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '^') {
                // While top of 'ops' has same or greater precedence to current
                // token, which is an operator. Apply operator on top of 'ops'
                // to top two elements in values stack
                while (!ops.empty() && operatorHasPrecedence(tokens[i], ops.peek())) {
                    values.push(doOperation(ops.pop(), values.pop(), values.pop()));
                }

                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }
        }

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty()) {
            values.push(doOperation(ops.pop(), values.pop(), values.pop()));
        }

        // Top of 'values' contains result, return it
        return values.pop();
    }

    // Returns true if 'op2' has higher or same precedence as 'op1',
    // otherwise returns false.
    // Precedence goes like this () -> ^ -> */ -> +-
    public static boolean operatorHasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '^') && ((op2 == '*' || op2 == '/'))
                || (op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')
                || (op1 == '^') && (op2 == '+' || op2 == '-')) {
            return false;
        } else {
            return true;
        }
    }

    // A utility method to apply an operator 'op' on operands 'a' 
    // and 'b'. Return the result.
    public static double doOperation(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '^':
                return Math.pow(a, b);
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("No se puede dividir por cero");
                }
                return a / b;
        }
        return 0;
    }
    
    public static boolean isAnOperator(char c) {
        switch (c) {
            case '*':
            case '/':
            case '+':
            case '-':
            case '%':
                return true;
            default:
                return false;
        }
    }

    public static boolean isANumberOrValidVariableChar(char c) {
        return ((int) c) >= 48 && ((int) c) <= 57;
    }
    
    /**
     *
     * @param expression
     * @return true If param expression is lexicographically correct, which
     * means it doesn't contain characters different to numbers, basic
     * aritmethic operators and parentheses
     */
    public static boolean isValidArithmeticExpression(String expression) {
        // TEST 1
        if (isAnOperator(expression.charAt(0)) || isAnOperator(expression.charAt(expression.length() - 1))) {
            return false;
        }

        int openParenthCount = 0;
        boolean lastWasOp = false;
        boolean lastWasOpen = false;

        for (char c : expression.toCharArray()) {
            if (c == ' ') {
                continue;
            }
            if (c == '(') {
                openParenthCount++;
                lastWasOpen = true;
                continue;
            } else if (c == ')') {
                if (openParenthCount <= 0 || lastWasOp) {
                    return false;
                }
                openParenthCount--;
            } else if (isAnOperator(c)) {
                if (lastWasOp || lastWasOpen) {
                    return false;
                }
                lastWasOp = true;
                continue;
            } else if (!isANumberOrValidVariableChar(c)) {
                return false;
            }
            lastWasOp = false;
            lastWasOpen = false;
        }
        if (openParenthCount != 0) {
            return false;
        }
        if (lastWasOp || lastWasOpen) {
            return false;
        }
        return true;
    }

    // Driver method to test above methods
    public static void main(String[] args) {
        System.out.println(ArithmeticExpParser.solveArithmeticExpression("2 ^ ( 2 * 3 )"));
        System.out.println(ArithmeticExpParser.solveArithmeticExpression("3 + 3 + 3"));
        System.out.println(ArithmeticExpParser.solveArithmeticExpression("100 * 2 + 12"));
        System.out.println(ArithmeticExpParser.solveArithmeticExpression("100 * ( 1 + 12 )"));
        System.out.println(ArithmeticExpParser.solveArithmeticExpression("100 * ( 2 * ( ( 12 + 6 ) / 5 ) ) / 14"));
    }
}
