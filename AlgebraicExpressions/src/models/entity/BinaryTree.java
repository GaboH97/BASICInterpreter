package models.entity;

/**
 *
 * @author Gabriel Huertas
 */
public class BinaryTree {

    //---------------------Attributes---------------------
    
    private BinaryNode root;
    private SimpleStack opStack;
    private SimpleList output;

    //--------------------Constructors--------------------
    
    public BinaryTree() {
        this.root = null;
    }

    //---------------------Methods-----------------------
    /**
     * 
     * @return true if root is null, otherwise, false 
     */
    public boolean isEmpty() {
        return root == null;
    }
    
    /**
     * 
     * @return SimpleList calling recursive method InOrder(BinaryNode node, SimpleList<Object> list) 
     */
    public SimpleList inOrder() {
        return (!isEmpty()) ? inOrder(root, new SimpleList<Object>()) : new SimpleList();
    }
    
    /**
     * 
     * @param node
     * @param list
     * @return SimpleList of a binary tree traversal
     */
    private SimpleList inOrder(BinaryNode node, SimpleList<Object> list) {
        if (node == null) {
            return list;
        } else {
            inOrder(node.getLeft(), list);
            list.addToTail(node.getInfo());
            inOrder(node.getRight(), list);
        }
        return list;
    }
    
    /**
     * 
     * @return 
     */
    public SimpleList preOrder() {
        return (!isEmpty()) ? inOrder(root, new SimpleList<Object>()) : new SimpleList();
    }
    
    /**
     * 
     * @param node
     * @param list
     * @return 
     */
    private SimpleList preOrder(BinaryNode node, SimpleList<Object> list) {
        if (node == null) {
            return list;
        } else {
            list.addToTail(node.getInfo());
            preOrder(node.getLeft(), list);
            preOrder(node.getRight(), list);
        }
        return list;
    }
    
    /**
     * 
     * @return 
     */
    public SimpleList postOrder() {
        return (!isEmpty()) ? postOrder(root, new SimpleList<Object>()) : new SimpleList();
    }
    
    /**
     * 
     * @param node
     * @param list
     * @return 
     */
    private SimpleList postOrder(BinaryNode node, SimpleList<Object> list) {
        if (node == null) {
            return list;
        } else {
            preOrder(node.getLeft(), list);
            preOrder(node.getRight(), list);
            list.addToTail(node.getInfo());
        }
        return list;
    }

    //-------------Getters and Setters--------------------
    
    public BinaryNode getRoot() {
        return root;
    }

    public void setRoot(BinaryNode root) {
        this.root = root;
    }

    //--------------------To String----------------------

    @Override
    public String toString() {
        return inOrder().toString();
    }
    
    
}
