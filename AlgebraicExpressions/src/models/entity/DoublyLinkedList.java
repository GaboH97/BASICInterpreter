/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.entity;

/**
 *
 * @author Gabriel Huertas
 * @param <T>
 */
public class DoublyLinkedList<T> {

    //------------------------------Atributos-----------------------
    private DoublyLinkedNode<T> head;
    private DoublyLinkedNode<T> tail;

    // -------------------------Constructores------------------------
    public DoublyLinkedList() {
        head = null;
        tail = null;
    }

    //---------------------------Metodos----------------------------
    public void addHead(T info) {
        DoublyLinkedNode aux = new DoublyLinkedNode(info);
        if (isEmpty()) {
            head = aux;
            tail = aux;
        } else {
            aux.setNext(head);
            head.setPrevoius(aux);
            head = aux;
        }
    }

    public void addLast(T info) {
        DoublyLinkedNode doubleNode = new DoublyLinkedNode(info);
        if (isEmpty()) {
            head = doubleNode;
            tail = doubleNode;
        }else{
            DoublyLinkedNode aux = head;
            while (aux.getNext()!=null) {
                aux = aux.getNext();   
            } 
        }
    }

    public boolean isEmpty() {
        return head == null;
    }

    //-----------------------Getters && Setters-----------------------
    public DoublyLinkedNode<T> getHead() {
        return head;
    }

    public DoublyLinkedNode<T> getTail() {
        return tail;
    }

    public void setHead(DoublyLinkedNode<T> head) {
        this.head = head;
    }

    public void setTail(DoublyLinkedNode<T> tail) {
        this.tail = tail;
    }

    //---------------------To String-----------------------------
    @Override
    public String toString() {
        return java.util.ResourceBundle.getBundle("properties/MessageBoundle_en_US").getString("");
    }
}
