package models.entity;

/**
 *
 * @author Gabriel Huertas
 */
public class BinaryNode {

    //---------------------Attributes--------------------------
    private String info;
    private BinaryNode left;
    private BinaryNode right;

    //---------------------Constructors------------------------
    public BinaryNode(String info) {
        this.info = info;
        left = null;
        right = null;
    }

    public BinaryNode() {
        info = "";
        left = null;
        right = null;
    }

    //----------------------Methods---------------------------
    
    /**
     * 
     * @return true if this node has no children otherwise false 
     */
    public boolean isLeaf() {
        return right == null && left == null;
    }

    //---------------------Getters and Setters-----------------
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public BinaryNode getLeft() {
        return left;
    }

    public void setLeft(BinaryNode left) {
        this.left = left;
    }

    public BinaryNode getRight() {
        return right;
    }

    public void setRight(BinaryNode right) {
        this.right = right;
    }

    //---------------------To String---------------------------
    @Override
    public String toString() {
        return String.valueOf(info);
    }
}
