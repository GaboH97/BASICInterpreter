package models.toolkit;

import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @author Gabriel Huertas.
 * @param <T>
 */
public class SimpleList<T> implements Serializable {

    //-----------------------------Attributes--------------------------
    private SimpleNode head;
    private int listSize;
    private IteratorList iteratorList;
    private static final long serialVersionUID = 6529685098267757690L;

    //----------------------------Constructor-------------------------
    public SimpleList() {
        head = null;
        listSize = 0;
    }

    //-----------------------------Methods----------------------------
    /**
     * Adds to the head (Stack mode)
     *
     * @param info
     */
    public void addToHead(T info) {
        SimpleNode node = new SimpleNode(info);
        if (isEmpty()) {
            head = node;
        } else {
            node.setNext(head);
            head = node;
        }
        listSize++;
    }

    /**
     * Adds a T object to the end of the list, iterating until node's next
     * equals null, if so, then last node's next equals new node
     *
     * @param info
     */
    public void addToTail(T info) {
        if (isEmpty()) {
            head = new SimpleNode(info);
        } else {
            SimpleNode node = new SimpleNode(info);
            SimpleNode current = head;
            if (current != null) {
                // starting at the head node, crawl to the end of the list and then add element after last node
                while (current.getNext() != null) {
                    current = current.getNext();
                }
                // the last node's "next" reference set to our new node
                current.setNext(node);
            }
        }

        // increment the number of elements 
        listSize++;
    }

    /**
     * Add node to the list at the specified position
     *
     * @param index
     * @param info
     */
    public void addToIndex(int index, T info) {
        if (index < 0 || index > listSize) {
            addToTail(info);
        } else {
            SimpleNode previous = null;
            SimpleNode current = head;
            SimpleNode newNode = new SimpleNode(info);
            for (int i = 0; i < index; i++) {
                previous = current;
                current = previous.getNext();
            }
            /*if requested index is 0, incoming node will point to head which will take new Node's value
             *if not, new node's previous index will point to new node's reference and this will point to current's one
             */
            if (index == 0) {
                newNode.setNext(head);
                head = newNode;
            } else {
                previous.setNext(newNode);
                newNode.setNext(current);
            }
            // Update size
            listSize++;
        }
    }

    /**
     * Check if list doesn't contain nodes, equals to size = 0;
     *
     * @return true or false
     */
    public boolean isEmpty() {
        return head == null || listSize == 0;
    }

    /**
     * Removes node at requested index
     *
     * @param index
     */
    public void removeByIndex(int index) throws IndexOutOfBoundsException {
        /*
         *If requested index is smaller than zero or bigger than listSize or the list
         is empty, throws an IOOBE
         */
        if (index < 0 || index > listSize || isEmpty()) {
            throw new IndexOutOfBoundsException("Couldn't delete this object");
        } else {
            /*
             Create a node that has the head info
             */
            SimpleNode current = head;
            /*
             if index is 0 head takes head's next value
             */
            if (index == 0) {
                head = current.getNext();
                listSize--;
            } else {
                int counter = 0;
                while (current != null) {
                    if (index - 1 == counter) {
                        /*
                        *Node´s previous value will point to the node's next value
                        *otherwise, node's next value will be null in order to 
                        *make sure node's previous will be the new list's last
                         */
                        if (current.getNext().getNext() != null) {
                            current.setNext(current.getNext().getNext());
                        } else {
                            current.setNext(null);
                        }
                        listSize--;
                        break;
                    } else {
                        current = current.getNext();
                    }
                    counter++;
                }
            }
        }
    }

    public T removeHead() {
        if (!isEmpty()) {
            T info = (T) head.getInfo();
            head = head.getNext();
            listSize--;
            return info;
        } else {
            return null;
        }
    }

    /**
     * Removes node according with the requested object WARNING: PROBLEM
     * DELETING LIST'S LAST POSITION
     *
     * @param object
     */
    public T remove(T object) throws NullPointerException {
        /*
         Create a node that has the head info
         */
        SimpleNode current = head;

        while (current != null) {
            if (current.getInfo().equals(object)) {
                current.setNext(current.getNext().getNext());
                listSize--;
                return (T) current.getInfo();
            } else {
                current = current.getNext();
            }
        }
        throw new NullPointerException();
    }

    public int indexOf(T object) {
        /*
         Create a node that has the head info
         */
        SimpleNode current = head;
        int counter = 0;
        while (current != null) {
            if (current.getInfo().equals(object)) {
                return counter;
            } else {
                current = current.getNext();
                counter++;
            }
        }
        return -1;
    }

    public void clean() {
        head = null;
    }

    public T get(int index) {
        if (isEmpty() || index > listSize || index < 0) {
            throw new IndexOutOfBoundsException("List is empty");
        } else {
            SimpleNode current = null;
            if (head != null) {
                current = head;
                for (int i = 0; i < index; i++) {
                    if (current.getNext() == null) {
                        return null;
                    } else {
                        current = current.getNext();
                    }
                }
                return (T) current.getInfo();
            }
            return (T) current.getInfo();
        }
    }

//------------------------ Getters & Setters-----------------------
    public SimpleNode getHead() {
        return head;
    }

    public void setHead(SimpleNode head) {
        this.head = head;
    }

    public SimpleList(SimpleNode head) {
        this.head = head;
    }

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    public Iterator<T> iterator() {
        iteratorList = new IteratorList();
        return iteratorList;
    }

    //------------------------------To String-------------------------
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (isEmpty()) {
            builder.append("Is empty");
        } else {
            SimpleNode auxnode = head;
            while (auxnode != null) {
                builder.append(auxnode.toString());
                auxnode = auxnode.getNext();
            }
        }
        return builder.toString();
    }

    //------------------Private Class-----------------------
    private class IteratorList implements Iterator<T>, Serializable {

        //---------------Attributes--------------------
        private SimpleNode current;
        private static final long serialVersionUID = 6529685098267757690L;

        //---------------Constructors-------------------
        public IteratorList() {
            current = head;
        }

        //----------------Inherited methods-------------
        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            T aux = (T) current.getInfo();
            current = current.getNext();
            return aux;
        }
    }
}
