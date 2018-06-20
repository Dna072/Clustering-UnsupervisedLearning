package models;

import java.awt.*;

/**
 * Created by Tillytet13 on 10/26/2016.
 */
public class Pixel {

    private double r;          //red field
    private double g;          //green field
    private double b;          //blue field
    private int cluster;


    public Pixel() {
    }

    public Pixel(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }


    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public int getRGB(){
        Color c = new Color((int)r,(int)g,(int)b);
        return c.getRGB();
    }
}
