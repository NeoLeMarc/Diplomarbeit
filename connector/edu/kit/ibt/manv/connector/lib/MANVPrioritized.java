package edu.kit.ibt.manv.connector.lib;

/* ** Meta classes ** */
public class MANVPrioritized implements Comparable {
    protected int priority;

    public int compareTo(Object o){
        return 0;
    }

    public int compareTo(MANVPrioritized o){
        return this.priority - o.priority;
    }

}
