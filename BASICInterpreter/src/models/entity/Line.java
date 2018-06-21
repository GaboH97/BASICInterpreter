/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.entity;

/**
 *
 * @author user
 */
public class Line {

    private int lineNumber;
    private String text;
    private LineType lineType;
    //-------- PARA ANIDACIÓN ------
    private int nestingLevel;

    public Line(int lineNumber, String text, LineType lineType, int nestingLevel) {
        this.lineNumber = lineNumber;
        this.text = text;
        this.lineType = lineType;
        this.nestingLevel = nestingLevel;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LineType getLineType() {
        return lineType;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    public int getNestingLevel() {
        return nestingLevel;
    }

    public void setNestingLevel(int nestingLevel) {
        this.nestingLevel = nestingLevel;
    }

    @Override
    public String toString() {
        return String.format("%-100s -> %s %d \n", getText(),
                (getLineType() != null) ? getLineType().name() : "ASIGN", getNestingLevel());
    }

}
