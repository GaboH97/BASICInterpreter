package models.toolkit;

/**
 *
 * @author Gabriel Huertas
 * @param <T>
 */
public class SimpleStack<T> {

    //------------------Attributes--------------------
    private SimpleList list;
    private int capacity;
    public static final int MAX_CAPACITY = 10;

    //------------------Constructors------------------
    public SimpleStack() {
        this.capacity = MAX_CAPACITY;
        list = new SimpleList();
    }

    public SimpleStack(int capacity) {
        this.capacity = capacity;
        list = new SimpleList();
    }

    //------------------Methods----------------------- 
    /**
     * Adds some info to the top of the stack
     *
     * @param info
     */
    public void pop(T info) {
        list.addToHead(info);
    }

    /**
     *
     * @return first element of stack
     * @throws java.lang.Exception
     */
    public T push() {
        return (T) list.removeHead();
    }

    /**
     *
     * @return the first node of the stack
     * @throws java.lang.Exception
     */
    public T top() {
        if (!isEmpty()) {
            return (T) list.getHead().getInfo();
        } else {
            return null;
        }
    }

    /**
     * Metodo que averigua si el nodo esta vacio.
     *
     * @return true or false
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean isFull() {
        return list.getListSize() >= capacity;
    }
    
    /**
     * 
     * @return SimpleNode representing the top element of the
     *         stack
     */
    public SimpleNode getHead() {
        return list.getHead();
    }
    
    public int getSize(){
        return list.getListSize();
    }

    //--------------------To String-----------------
    @Override
    public String toString() {
        return list.toString();
    }

    /* public static void main(String[] args) {
        SimpleStack stack = new SimpleStack();
        try {
            for (int i = 0; i < 10; i++) {
                stack.pop("cosito " + i);
                System.out.println(stack.toString());
            }

            System.out.println(stack.isFull());
            stack.push();
            System.out.println(stack.toString());

        } catch (ExceededCapacityException ex) {
            Logger.getLogger(SimpleStack.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SimpleStack.class.getName()).log(Level.SEVERE, null, ex);
        }

    }*/
}
