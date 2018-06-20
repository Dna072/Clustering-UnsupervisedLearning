package models;

/**
 * Created by Tillytet13 on 10/22/2016.
 */
public class Data {

    private double x;
    private double y;
    private int cluster;

    public Data(){

    }

    public Data(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public String toString(){
        return "X: " +x+" Y: "+y + " cluster: " +cluster;
    }
}
