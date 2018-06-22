package models.entity;

/**
 *
 * @author user
 */
public class Variable {

    private String variableName;
    private VariableType variableType;
    private Object value;

    public Variable(String variableName, VariableType variableType, Object value) {
        this.variableName = variableName;
        this.variableType = variableType;
        this.value = (value instanceof String) ? value : Double.parseDouble(value.toString());
    }

    public Variable(String variableName, VariableType variableType) {
        this.variableName = variableName;
        this.variableType = variableType;
        this.value = null;
    }

    public String getVariableName() {
        return variableName;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getValue()+" - "+getVariableType().name();
                
    }

//    public static void main(String[] args) {
//        Variable variable = new Variable("PI", VariableType.DOUBLE, "3.1416");
//        System.out.println((variable.getValue() instanceof Double) ? "Si" : "No");//revisar que le entra a esto
//    }
}
