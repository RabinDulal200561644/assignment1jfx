package com.example.assignment1javafx;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MusicArtistDashboard extends Application {

    private Stage primaryStage;
    private Scene artistScene, barChartScene;
    private TableView<ArtistInfo> artistTableView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Set application title
        primaryStage.setTitle("Top Music Artists Dashboard");

        // Connect to MySQL database
        String dbUrl = "jdbc:mysql://localhost:3306/MusicSales";
        String dbUser = "root";
        String dbPassword = "W7301@jqir#";
        Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

        // Set up TableView for artist data
        artistTableView = createArtistTableView(connection);

        // Set up BarChart for artist sales
        BarChart<String, Number> artistBarChart = createArtistBarChart(connection);

        // Button to switch to BarChart scene
        Button switchToBarChartButton = new Button("Switch to Bar Chart");
        switchToBarChartButton.setOnAction(e -> primaryStage.setScene(barChartScene));

        // Button to switch back to TableView scene
        Button switchToTableViewButton = new Button("Back to Table View");
        switchToTableViewButton.setOnAction(e -> primaryStage.setScene(artistScene));

        // Layout for TableView scene
        VBox artistTableLayout = new VBox(10);
        artistTableLayout.getChildren().addAll(artistTableView, switchToBarChartButton);
        artistScene = new Scene(artistTableLayout, 800, 600);

        // Layout for BarChart scene
        VBox artistBarChartLayout = new VBox(10);
        artistBarChartLayout.getChildren().addAll(artistBarChart, switchToTableViewButton);
        barChartScene = new Scene(artistBarChartLayout, 800, 600);

        // Close database connection
        connection.close();

        // Set initial scene
        primaryStage.setScene(artistScene);
        primaryStage.show();
    }

    private TableView<ArtistInfo> createArtistTableView(Connection connection) throws Exception {
        TableView<ArtistInfo> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define columns
        TableColumn<ArtistInfo, String> artistNameColumn = new TableColumn<>("Artist Name");
        TableColumn<ArtistInfo, Integer> totalSalesColumn = new TableColumn<>("Total Sales");

        artistNameColumn.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        totalSalesColumn.setCellValueFactory(new PropertyValueFactory<>("totalSales"));

        tableView.getColumns().addAll(artistNameColumn, totalSalesColumn);

        // Query data from database
        String query = "SELECT artist_name, total_sales FROM TopSellingArtists";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        // Populate table with data
        ObservableList<ArtistInfo> artistList = FXCollections.observableArrayList();
        while (resultSet.next()) {
            String artistName = resultSet.getString("artist_name");
            int totalSales = resultSet.getInt("total_sales");
            artistList.add(new ArtistInfo(artistName, totalSales));
        }

        tableView.setItems(artistList);

        // Close database resources
        resultSet.close();
        statement.close();

        return tableView;
    }

    private BarChart<String, Number> createArtistBarChart(Connection connection) throws Exception {
        // Define axes
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> artistBarChart = new BarChart<>(xAxis, yAxis);

        // Set chart title
        artistBarChart.setTitle("Top Selling Artists Bar Chart");

        // Query data from database
        String query = "SELECT artist_name, total_sales FROM TopSellingArtists";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        // Prepare data for bar chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        while (resultSet.next()) {
            String artistName = resultSet.getString("artist_name");
            int totalSales = resultSet.getInt("total_sales");
            series.getData().add(new XYChart.Data<>(artistName, totalSales));
        }

        // Close database resources
        resultSet.close();
        statement.close();

        // Add series to bar chart
        artistBarChart.getData().add(series);

        return artistBarChart;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ArtistInfo class for TableView
    public static class ArtistInfo {
        private final String artistName;
        private final int totalSales;

        public ArtistInfo(String artistName, int totalSales) {
            this.artistName = artistName;
            this.totalSales = totalSales;
        }

        public String getArtistName() {
            return artistName;
        }

        public int getTotalSales() {
            return totalSales;
        }
    }
}
