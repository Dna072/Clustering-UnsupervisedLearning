package sample;

import algorithm.KMeans;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Data;
import models.Pixel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    private TextField kTextField;

    @FXML
    private BorderPane borderPane;

    @FXML
    private BorderPane challengeBorderPane;

    @FXML
    private ComboBox fileBox;

    @FXML
    private Button imageButton;

    @FXML
    private TextField imagePathTxtField;

    @FXML
    private TextField pixelK;



    private int k_cluster = 2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initailize();
    }

    private void initailize(){
        kTextField.setText(String.valueOf(k_cluster));
        kTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldStr, String newStr) {
                String[] values = newStr.split(" ");
                if(values.length == 1){
                    try{
                        k_cluster = Integer.parseInt(values[0]);
                        runCurrentAlgorithm();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        pixelK.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String newStr) {
                String[] values = newStr.split(" ");

                if(newStr.length() > 0 && !(newStr.charAt(newStr.length() - 1) == ' ')){
                    return;
                }

                if(values.length == 1){
                    try{
                        k_cluster = Integer.parseInt(values[0]);
                        if(imagePathTxtField.getText().length() > 0){
                            imageCluster(k_cluster);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        final FileChooser fileChooser = new FileChooser();
        final Stage stage = new Stage();

        imageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                File file = fileChooser.showOpenDialog(stage);
                String filePath = file.getAbsolutePath();

                imagePathTxtField.setText(filePath);
                //imageCluster();
            }
        });

        ArrayList<String> files = new ArrayList<>();
        files.add("SmallRandData.csv");
        files.add("Health Facilities in Ghana");

        fileBox.setItems(FXCollections.observableList(files));

        fileBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                runCurrentAlgorithm();
            }
        });
    }

    private void runCurrentAlgorithm() {
        String fileName = (String) fileBox.getSelectionModel().getSelectedItem();

        switch (fileName){
            case "SmallRandData.csv":
                SmallRandDataClustering(k_cluster);
                break;

            case "Health Facilities in Ghana":
                healthFacilitiesClustering(k_cluster);
                break;
        }
    }

    //SmallRandDataClustering.csv KMeans
    private void SmallRandDataClustering(int k){
        //read the file
        ArrayList<Data> dataArrayList = new ArrayList<>();
        readSmallRandDataFile(dataArrayList);

        //run kMeans clustering
        KMeans kMeans = new KMeans(k);
        kMeans.setRawData(dataArrayList);

        //do the clustering magic
        kMeans.cluster2();

        //show chart
        showChart(kMeans);
    }

    /**
     * Method reads the SmallRandDataClustering.csv file outputs the data to arraylist
     * @param dataArrayList list to dump data
     * */
    private void readSmallRandDataFile(ArrayList<Data> dataArrayList) {

        String line = "";
        try(BufferedReader br = new BufferedReader(new FileReader("SmallRandData.csv"))){

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
    }

    /**
     * Method shows the chart of any clustering
     * @param kMeans the KMeans instance of the clustering
     * */
    private void showChart(KMeans kMeans){
        double[] bounds = kMeans.getBounds();

        double minX = bounds[0], maxX = bounds[1];
        double minY = bounds[2], maxY = bounds[3];

        final NumberAxis xAxis = new NumberAxis(minX -1, maxX +1, 1);
        final NumberAxis yAxis = new NumberAxis(minY-1 , maxY+1 , 1);

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
        borderPane.setCenter(sc);
    }

    private void healthFacilitiesClustering(int k){
        ArrayList<Data> dataArrayList = new ArrayList<>();
        String line = "";
        //read healthFacilites file
        readHealthFacilityFile(dataArrayList);

        //run kMeans clustering
        KMeans kMeans = new KMeans(k);
        kMeans.setRawData(dataArrayList);

        //do the clustering magic
        kMeans.cluster2();

        //show chart
        showChart(kMeans);
    }

    private void readHealthFacilityFile(ArrayList<Data> dataArrayList) {
        String line;
        try(BufferedReader br = new BufferedReader(new FileReader("HEALTH FACILITIES IN GHANA.csv"))){
            int i = 0;

            while ((line = br.readLine()) != null){
                String[] values = line.split(",");
                i++;
                if(!values[0].equals("Region") && values.length > 6){

                    int length = values.length;

                    if(values[length-1].equals("Government") || values[length-2].equals("Government")){
                        System.out.println("line: " + i);
                    }

                    double x = Double.parseDouble(values[length-1]);
                    double y = Double.parseDouble(values[length-2]);

                    Data d = new Data(x,y);
                    dataArrayList.add(d);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Image clustering
     * */
    private void imageCluster(int k){
      //read image file
      String imagePath = imagePathTxtField.getText();
      int height, width;
      int RGBArray[][];
        File file = new File(imagePath);
        try {

            BufferedImage bufferedImage = ImageIO.read(file);
            height = bufferedImage.getHeight();
            width = bufferedImage.getWidth();

            RGBArray = new int[width][height];

            for(int i=0; i<width; i++){
                for(int j=0; j<height; j++){
                    RGBArray[i][j] = bufferedImage.getRGB(i,j);

                }
            }

            analyzeRGB(RGBArray, width, height, k, bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void analyzeRGB(int[][] RGB, int width, int height, int k, BufferedImage image){
        ArrayList<Pixel> pixelList = new ArrayList<>();

        for(int i = 0; i<width; i++){
            for(int j=0; j<height; j++){
                Color c = new Color(RGB[i][j]);
                int red = c.getRed();
                int blue = c.getBlue();
                int green = c.getGreen();

                if(red < 50 & blue <50 & green < 50){
                    Color newC = new Color(163, 73, 164);
                    image.setRGB(i, j, newC.getRGB());
                }

                Pixel p = new Pixel(red,green,blue);
                pixelList.add(p);
            }
        }

        try{
            KMeans kMeans = new KMeans(k);
            kMeans.setRawPixelData(pixelList);

            kMeans.clusterPixel();
            showBarChart(kMeans);
        }catch (Exception e){
            e.printStackTrace();
        }

        showImage(image);
    }

    /**
     * Method draws bar chart, called in analyzeRGB
     * @param kMeans the kmeans instance
     *
     * */
    private void showBarChart(KMeans kMeans){
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        final BarChart<String,Number> bc = new BarChart<String,Number>(xAxis,yAxis);

        bc.setTitle("KMeans Pixel Clustering");
        xAxis.setLabel("Cluster");
        yAxis.setLabel("Count");

        ArrayList<XYChart.Series> seriesArrayList = new ArrayList<>();

        ArrayList<Pixel> pixelCentroids = kMeans.getPixelCentroids();
        ArrayList<Pixel> pixels = kMeans.getRawPixelData();

        int[] clusterCount = new int[pixelCentroids.size()];

        for(int i=0; i<clusterCount.length; i++){
            //loop through pixel array for clusters they belong to
            int sum = 0;
            for(Pixel p: pixels){

                if(p.getCluster() == i+1){
                    sum++;
                    clusterCount[i] = sum;
                }
            }
        }


        for(int i =0; i<pixelCentroids.size(); i++){
            final XYChart.Series series = new XYChart.Series();

            series.setName("Cluster " + i);

            XYChart.Data data = new XYChart.Data(String.valueOf(i), clusterCount[i]);

            final Pixel p = pixelCentroids.get(i);
            Color c = new Color(p.getRGB());
            String rStr = Integer.toHexString(c.getRed());
            String bStr = Integer.toHexString(c.getBlue());
            String gStr = Integer.toHexString(c.getGreen());

            final String resultStr = rStr+gStr+bStr;
            series.nodeProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observableValue, Object o, Object t1) {

                    String style = "-fx-bar-fill: #" +resultStr;
                    System.out.println(style);
                    ((Node)t1).setStyle(style);
                }
            });

            data.nodeProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observableValue, Object old, Object newNode) {
                    String style = "-fx-bar-fill: #" +resultStr;
                    System.out.println(style);
                    ((Node)newNode).setStyle(style);

                    // -fx-bar-fill: #22bad9
                }
            });
            series.getData().add(data);
            //change data color to be that of centroid pixel
            seriesArrayList.add(series);
        }

        //display on graph
        for (XYChart.Series s: seriesArrayList){
            bc.getData().add(s);
            bc.setCategoryGap(4);
        }

        challengeBorderPane.setCenter(bc);

    }

    private void showImage(BufferedImage image){
        Stage stage = new Stage();
        StackPane sp = new StackPane();
        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));

        sp.getChildren().add(imageView);
        Scene scene = new Scene(sp, image.getWidth(), image.getHeight());
        stage.setScene(scene);

        stage.show();
    }
}
