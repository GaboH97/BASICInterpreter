
package models.entity;


/**
 *
 * @author Gabriel Huertas
 * @param <T>
 */
public class SimpleQueue<T> {
    
     //------------------Attributes--------------------
    private SimpleList list;
    private int capacity;
    public static final int MAX_CAPACITY = 10;

    //------------------Constructors------------------
    public SimpleQueue() {
        this.capacity = MAX_CAPACITY;
        list = new SimpleList();
    }

    public SimpleQueue(int capacity) {
        this.capacity = capacity;
        list = new SimpleList();
    }
    
    //------------------Methods----------------------
    /**
     * Add a T to the end of the Queue
     * @param info
     * @throws ExceededCapacityException 
     */
    public void add(T info){
            list.addToTail(info);
    }
    /**
     * 
     * @return last element added to the Queue
     * @throws Exception 
     */
    public T removeLast() throws Exception{
        if (!isEmpty()) {
            T node = (T) list.getHead();
            list.removeHead();
            return node;
        } else {
            throw new Exception();
        }
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
     public boolean isFull() {
        return list.getListSize() >= capacity;
    }
     
    //------------------Getters & Setters------------

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) throws Exception {
        if(capacity>list.getListSize()){
         this.capacity = capacity;   
        }else{
            throw new Exception(java.util.ResourceBundle.getBundle("properties/MessageBoundle_en_US").getString("CANNOT SET QUEUE CAPACITY"));
        }
    }
     
    //------------------To String--------------------

    @Override
    public String toString() {
       return list.toString();
    }
  
}
