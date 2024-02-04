package com.Adrien.Snake;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Snake extends Application {

    private static final int SEGMENT_SIZE = 20;
    private static final int SPEED = 5;

    private Rectangle boundary;
    private Rectangle redPixel;
    private Rectangle[] snakeSegments;
    private int snakeLength;
    private int dx;
    private int dy;
    private boolean isMoving;
    private boolean gameOver;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        // Create boundary
        boundary = new Rectangle(400, 400);
        boundary.setFill(null);
        boundary.setStroke(Color.BLACK);

        // Create red pixel
        redPixel = new Rectangle(SEGMENT_SIZE, SEGMENT_SIZE);
        redPixel.setFill(Color.RED);

        // Create snake segments
        snakeSegments = new Rectangle[100]; // Maximum snake length
        snakeLength = 1;
        snakeSegments[0] = createSnakeSegment(0, 0);

        // Create control buttons
        Button btnUp = new Button("Up");
        Button btnDown = new Button("Down");
        Button btnLeft = new Button("Left");
        Button btnRight = new Button("Right");

        VBox buttonBox = new VBox(10, btnUp, btnDown, btnLeft, btnRight);
        buttonBox.setTranslateX(200);
        buttonBox.setTranslateY(200);

        root.getChildren().addAll(boundary, redPixel, buttonBox);

        Scene scene = new Scene(root, 400, 400);
        scene.setOnKeyPressed(new KeyPressedHandler());
        scene.setOnKeyReleased(new KeyReleasedHandler());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();

        startGameLoop();
    }

    private Rectangle createSnakeSegment(double x, double y) {
        Rectangle segment = new Rectangle(SEGMENT_SIZE, SEGMENT_SIZE);
        segment.setFill(Color.GREEN);
        segment.setTranslateX(x);
        segment.setTranslateY(y);
        return segment;
    }

    private void placeRedPixel() {
        double x = Math.random() * 20 * SEGMENT_SIZE;
        double y = Math.random() * 20 * SEGMENT_SIZE;

        redPixel.setTranslateX(x);
        redPixel.setTranslateY(y);
    }

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000 / SPEED) {
                    moveSnake();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    private void moveSnake() {
        if (!isMoving || gameOver)
            return;

        for (int i = snakeLength - 1; i > 0; i--) {
            snakeSegments[i].setTranslateX(snakeSegments[i - 1].getTranslateX());
            snakeSegments[i].setTranslateY(snakeSegments[i - 1].getTranslateY());
        }

        snakeSegments[0].setTranslateX(snakeSegments[0].getTranslateX() + dx * SEGMENT_SIZE);
        snakeSegments[0].setTranslateY(snakeSegments[0].getTranslateY() + dy * SEGMENT_SIZE);

        checkCollision();
    }

    private void checkCollision() {
        // Check if the head of the snake collides with any of its body segments
        for (int i = 1; i < snakeLength; i++) {
            if (snakeSegments[0].getTranslateX() == snakeSegments[i].getTranslateX() &&
                    snakeSegments[0].getTranslateY() == snakeSegments[i].getTranslateY()) {
                gameOver = true;
                return;
            }
        }

        if (snakeSegments[0].getTranslateX() == redPixel.getTranslateX() &&
                snakeSegments[0].getTranslateY() == redPixel.getTranslateY()) {
            snakeLength++;
            snakeSegments[snakeLength - 1] = createSnakeSegment(
                    snakeSegments[snakeLength - 2].getTranslateX(),
                    snakeSegments[snakeLength - 2].getTranslateY()
            );
            placeRedPixel();
        }
    }

    private class KeyPressedHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            if (!isMoving) {
                isMoving = true;
            }

            if (event.getCode() == KeyCode.UP && dy != 1) {
                dx = 0;
                dy = -1;
            } else if (event.getCode() == KeyCode.DOWN && dy != -1) {
                dx = 0;
                dy = 1;
            } else if (event.getCode() == KeyCode.LEFT && dx != 1) {
                dx = -1;
                dy = 0;
            } else if (event.getCode() == KeyCode.RIGHT && dx != -1) {
                dx = 1;
                dy = 0;
            }
        }
    }

    private class KeyReleasedHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            // No action on key released
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
