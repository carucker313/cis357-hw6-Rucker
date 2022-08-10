package com.example.hw6;
// Homework 6: Topics: Threads + Network + Database programming
// Course: CIS357
// Due date: 8/9/2022
// Name: Christopher Rucker
// Instructor: Il-Hyung Cho
//youtube link:https://youtu.be/H8WIPeeh6vU
// Program description: This program implements the POS system using database, threads, and network programming.
/* Program features: o	Conformance to the OO Design: YES
	Support of item change: full
	Javadoc conformed comments on the classes, methods, and attributes: full
	Handling wrong input and invalid input: full
	Connects to the database: YES
	Successfully uses threads and network programming: YES
	Program does not crash with exceptions: does not crash
	Correct handling of payment and taxes: partial
	Overall layout of GUI and ease of use: GOOD
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HW6Rucker extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HW6Rucker.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 592, 506);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();


    }


}