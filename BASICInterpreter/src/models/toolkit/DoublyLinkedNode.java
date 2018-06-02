/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.toolkit;

/**
 *
 * @author Lina Melo
 */
public class DoublyLinkedNode<T> {

    //-----------------------------------Atributos------------------------
    private T info;
    private DoublyLinkedNode<T> prevoius;
    private DoublyLinkedNode<T> next;

    //---------------------------------Constructores----------------------
    public DoublyLinkedNode(T info) {
        this.info = info;
        next = null;
        prevoius = null;
    }

    public DoublyLinkedNode() {
        info = null;
        next = null;
        prevoius = null;

    }

    //-------------------------------Getters and Setters--------------------
    public T getInfo() {
        return info;
    }

    public DoublyLinkedNode<T> getPrevoius() {
        return prevoius;
    }

    public DoublyLinkedNode<T> getNext() {
        return next;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public void setPrevoius(DoublyLinkedNode<T> prevoius) {
        this.prevoius = prevoius;
    }

    public void setNext(DoublyLinkedNode<T> next) {
        this.next = next;
    }
}
