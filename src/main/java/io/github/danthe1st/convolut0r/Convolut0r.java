package io.github.danthe1st.convolut0r;

import io.github.danthe1st.convolut0r.controller.ConvolutionController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Convolut0r extends Application {
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource("ctl.fxml"));
		Pane root = loader.load();
		ConvolutionController controller = loader.getController();
		controller.setStage(primaryStage);
		primaryStage.setScene(new Scene(root));
		primaryStage.sizeToScene();
		primaryStage.show();
		controller.selectImage();
	}
	
}
