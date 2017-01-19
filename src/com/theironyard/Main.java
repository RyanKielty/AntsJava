package com.theironyard;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class Main extends Application {
    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    static final int ANT_COUNT = 100;

    ArrayList<Ant> ants;

    static  ArrayList<Ant> createAnts() {
        ArrayList<Ant> ants = new ArrayList<>();
        for (int i = 0; i < ANT_COUNT; i++) {
            Random randomObject = new Random();
            ants.add(new Ant(randomObject.nextInt(WIDTH), randomObject.nextInt(HEIGHT)));
        }
        return ants;
    }

    static  double randomStep() {
        return Math.random() * 2-1;
    }

    Ant moveAnt(Ant ant) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ant.x += randomStep();
        ant.y += randomStep();
        return ant;
    }

    long lastTimestamp = 0;

    int fps(long now) {
        double diff = now - lastTimestamp;
        double diffSeconds = diff/1000000000;
        return (int) (1/diffSeconds);
    }

    void moveAnts() {
        ants = ants.parallelStream()
                .map(this::moveAnt)
                .collect(Collectors.toCollection(ArrayList<Ant>::new));
    }

    void drawAnts(GraphicsContext context) {
        context.clearRect(0, 0, WIDTH, HEIGHT);
        for (Ant ant : ants) {
            context.setFill(Color.BLACK);
            context.fillOval(ant.x, ant.y, 5, 5);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene= new Scene(root, WIDTH, HEIGHT);

        primaryStage.setTitle("Ants");
        primaryStage.setScene(scene);
        primaryStage.show();

        Canvas canvasObject = (Canvas) scene.lookup(("#canvas"));
        GraphicsContext context = canvasObject.getGraphicsContext2D();

        ants = createAnts();

        Label fpsLabel = (Label) scene.lookup("#fps");

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveAnts();
                drawAnts(context);
                fpsLabel.setText(fps(now) + "");
                lastTimestamp = now;
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
