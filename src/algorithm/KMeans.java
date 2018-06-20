package algorithm;

import models.Data;
import models.Pixel;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by Tillytet13 on 10/22/2016.
 */
public class KMeans {

    private ArrayList<Data> rawData;
    private ArrayList<Data> centroids;

    private ArrayList<Pixel> rawPixelData;
    private ArrayList<Pixel> pixelCentroids;

    private int k;                 //number of clusters

    public KMeans(int k){
        this.k = k;
        centroids = new ArrayList<>();
    }

    public ArrayList<Pixel> getRawPixelData() {
        return rawPixelData;
    }

    public void setRawPixelData(ArrayList<Pixel> rawPixelData) {
        this.rawPixelData = rawPixelData;
    }

    public ArrayList<Pixel> getPixelCentroids() {
        return pixelCentroids;
    }

    public ArrayList<Data> getRawData() {
        return rawData;
    }

    public void setRawData(ArrayList<Data> rawData) {
        this.rawData = rawData;
    }

    public ArrayList<Data> getCentroids() {
        return centroids;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public void cluster2(){
        //randomly assign data items clusters
        for(int i=0; i<rawData.size(); i++){
            rawData.get(i).setCluster(i%k);
        }

        //refine randomization with the Fisher-Yates shuffle
        for(int i=0; i<rawData.size(); i++){
            int r = i + (int)(Math.random() * (rawData.size() - i));

            //swap clusters
            Data d = ((ArrayList<Data>)rawData.clone()).get(i);
            rawData.set(i, rawData.get(r));
            rawData.set(r, d);
        }

        boolean stableAssignment = false;

        while (!stableAssignment){
            //compute centroid for each cluster
            centroids.clear();

            for(int i=0; i<k; i++){
                ArrayList<Data> clusterI = new ArrayList<>();
                double sumX =0, sumY =0;

                for(Data d: rawData){
                    if(d.getCluster() == i) {
                        clusterI.add(d);
                        sumX += d.getX();
                        sumY += d.getY();
                    }
                }

                //create centroids
                Data centroid = new Data();
                centroid.setX(sumX/clusterI.size());
                centroid.setY(sumY/clusterI.size());

                centroids.add(centroid);
            }

            stableAssignment = true;
            //for each point
            for(int i=0; i<rawData.size(); i++){
                //compute and minimize the point-to-centroid distance
                int minCluster = 0;
                double minDist = distance(rawData.get(i), centroids.get(0));

                for(int j=1; j <k; j++){
                    double dist = distance(rawData.get(i), centroids.get(j));

                    if(dist < minDist){
                        minCluster = j;
                        minDist = dist;
                    }
                }

                //uodate cluster assignment if necessary
                if(minCluster != rawData.get(i).getCluster()){
                    stableAssignment = false;
                    rawData.get(i).setCluster(minCluster);
                }
            }
        }
    }

    public void clusterPixel(){
        pixelCentroids = new ArrayList<>();
        //randomly assign data items clusters
        for(int i=0; i<rawPixelData.size(); i++){
            rawPixelData.get(i).setCluster(i%k);
        }

        //refine randomization with the Fisher-Yates shuffle
        for(int i=0; i<rawPixelData.size(); i++){
            int r = i + (int)(Math.random() * (rawPixelData.size() - i));

            //swap clusters
            Pixel p = ((ArrayList<Pixel>)rawPixelData.clone()).get(i);
            rawPixelData.set(i, rawPixelData.get(r));
            rawPixelData.set(r, p);
        }

        boolean stableAssignment = false;

        while (!stableAssignment){
            //compute centroid for each cluster
            pixelCentroids.clear();

            for(int i=0; i<k; i++){
                ArrayList<Pixel> clusterI = new ArrayList<>();
                double sumR =0, sumG =0, sumB = 0;

                for(Pixel p: rawPixelData){
                    if(p.getCluster() == i) {
                        clusterI.add(p);
                        sumR += p.getR();
                        sumB += p.getB();
                        sumG += p.getG();
                    }
                }


                //create centroids
                Pixel centroid = new Pixel();
                centroid.setB(sumB/clusterI.size());
                centroid.setG(sumG/clusterI.size());
                centroid.setR(sumR/clusterI.size());


                pixelCentroids.add(centroid);
            }

            stableAssignment = true;
            //for each point
            for(int i=0; i<rawPixelData.size(); i++){
                //compute and minimize the point-to-centroid distance
                int minCluster = 0;
                double minDist = pixelDist(rawPixelData.get(i), pixelCentroids.get(0));

                for(int j=1; j <k; j++){
                    double dist = pixelDist(rawPixelData.get(i), rawPixelData.get(j));

                    if(dist < minDist){
                        minCluster = j;
                        minDist = dist;
                    }
                }

                //uodate cluster assignment if necessary
                if(minCluster != rawPixelData.get(i).getCluster()){
                    stableAssignment = false;
                    rawPixelData.get(i).setCluster(minCluster);
                }
            }
        }
    }

    private double distance(Data d1, Data d2){
        return sqrt(pow(d1.getX() - d2.getX(), 2) + pow(d1.getY() - d2.getY(), 2));
    }

    private double pixelDist(Pixel p1, Pixel p2){
        return sqrt(pow(p1.getR() - p2.getR(), 2) + pow(p1.getB() - p2.getB(), 2) + pow(p1.getG() - p2.getG(), 2));
    }

    //additional method to set grid getBounds
    public double[] getBounds(){
        double minX = 10000, maxX =0;
        double minY = 10000, maxY = 0;

        for(Data d: rawData){
            if(minX > d.getX())
                minX = d.getX();

            if(maxX < d.getX())
                maxX = d.getX();

            if (minY > d.getY())
                minY = d.getY();

            if(maxY < d.getY())
                maxY = d.getY();
        }

        double[] bound = new double[4];
        bound[0] = minX;    bound[1] = maxY;
        bound[2] = minY;    bound[3] = maxY;

        return bound;
    }
}
