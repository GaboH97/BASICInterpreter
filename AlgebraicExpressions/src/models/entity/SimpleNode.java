
package models.entity;

/**
 *
 * @author Gabriel Huertas, JairoBotero (Arquitecto)
 * @param <T>
 */
public class SimpleNode<T> {

    //---------------------------------Atributtes-------------------
    /**
     * Node info
     */
    private T info;
    /**
     * Pointer
     */
    private SimpleNode<T> next;

    //-----------------------------Constructor---------------------
    /**
     * Creates a simple node with a T info
     * @param info
     */
    public SimpleNode(T info) {
        this.info = info;
        this.next = null;
    }

    public SimpleNode(T info, SimpleNode next) {
        this.info = info;
        this.next = next;
    }

    //--------------------- Getter & Stters------------------------
    public T getInfo() {
        return info;
    }

    public SimpleNode<T> getNext() {
        return next;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public void setNext(SimpleNode<T> next) {
        this.next = next;
    }

    //-----------------------------To String----------------------------
    @Override
    public String toString() {
        return info + ", ";
    }

}
