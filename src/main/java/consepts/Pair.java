/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consepts;

import java.util.Objects;

/**
 *
 * @author Sami
 * @param <A>
 * @param <B>
 */
public class Pair <A,B> implements Comparable{
    A a;
    B b;
    
    public Pair(A ax, B by) {
        a = ax;
        b = by;
    }
    
    public A getX(){
        return a;
    }
    
    public B getY(){
        return b;
    }

    @Override
    public boolean equals(Object t) {
        if (t.getClass() != this.getClass()){
            return false;
        }
        return ((Pair) t).a.equals(a) && ((Pair) t).b.equals(b);
        
        
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.a);
        hash = 37 * hash + Objects.hashCode(this.b);
        return hash;
    }

    @Override
    public int compareTo(Object t) {
        if (t.getClass() != this.getClass()){
            return 0;
        }
        return (int)(((Double) b - (Double)((Pair) t).b)*100);
    }
}
