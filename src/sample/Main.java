package sample;

import algorithm.KMeans;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import models.Data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

       // readFile("SmallRandData.csv", primaryStage);

        primaryStage.setTitle("Lab 6");

        primaryStage.setScene(new Scene(root, 1200, 675));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);

    }

    private static void readFile(String path, Stage stage){
        ArrayList<Data> dataArrayList = new ArrayList<>();

        String line = "";

        try(BufferedReader br = new BufferedReader(new FileReader(path))){

            while((line = br.readLine()) != null){
                String[] values = line.split(",");

                if(!values[0].equals("X") && !values[1].equals("Y")){
                    double x = Double.parseDouble(values[0]);
                    double y = Double.parseDouble(values[1]);

                    Data d = new Data(x,y);
                    dataArrayList.add(d);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        testKMeans(dataArrayList, stage);
    }

    private static void testKMeans(ArrayList<Data> rawData, Stage stage){

        KMeans kMeans = new KMeans(4);
        kMeans.setRawData(rawData);

        kMeans.cluster2();
        //get the centroids
        ArrayList<Data> centroids = kMeans.getCentroids();

        for(Data d: centroids){
            System.out.println(d.toString());
        }

        System.out.println("Data points");

        for(Data d: rawData){
            System.out.println(d.toString());
        }


        showChart(stage, kMeans);
    }

    private static void showChart(Stage stage, KMeans kMeans){
        double[] bounds = kMeans.getBounds();

        double minX = bounds[0], maxX = bounds[1];
        double minY = bounds[2], maxY = bounds[3];

        final NumberAxis xAxis = new NumberAxis(minX -2, maxX +2, 1);
        final NumberAxis yAxis = new NumberAxis(minY - 2, maxY + 2, 1);

        final ScatterChart<Number, Number> sc = new ScatterChart<Number, Number>(xAxis, yAxis);
        xAxis.setLabel("X - axis");
        yAxis.setLabel("Y - axis");

        sc.setTitle("KMeans cluster plot");

       ArrayList<Data> centroids = kMeans.getCentroids();
       ArrayList<Data> rawData = kMeans.getRawData();

        ArrayList<XYChart.Series> seriesList = new ArrayList<>();


       for(int i=0; i<centroids.size(); i++){
            XYChart.Series series = new XYChart.Series();
           series.setName("Cluster " + (i+1));

            //add the points that are in the cluster
            for(Data d: rawData){
                //get the points
                if(d.getCluster() == i)
                    series.getData().add(new XYChart.Data(d.getX(), d.getY()));
            }

            seriesList.add(series);
        }

        for(XYChart.Series s: seriesList){
            sc.getData().add(s);
        }

        //create centroids series
        XYChart.Series cSeries = new XYChart.Series();
        cSeries.setName("Centroids");

        for(Data d: centroids){
            cSeries.getData().add(new XYChart.Data(d.getX(), d.getY()));
        }

        sc.getData().add(cSeries);

        Scene scene = new Scene(sc, 500, 500);
        stage.setScene(scene);
        stage.show();
    }
}
