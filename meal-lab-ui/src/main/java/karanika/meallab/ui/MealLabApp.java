package karanika.meallab.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * Main Application Entry Point.
 * Orchestrates the primary stage and scene transitions.
 */

public class MealLabApp extends Application {
	
	// Static references to allow global access to the Stage and Scenes
	public static Stage primaryStage;
	public static Scene mainScene , searchScene;
	
	@Override
	public void start(Stage stage) {
		
		// 1. Capture and store the primary stage
		this.primaryStage = stage;
		
		// 2. Initialize the Main Menu Scene (MainSceneCreator)
		MainSceneCreator mainSceneCreator = new MainSceneCreator();
		 mainScene = mainSceneCreator.createScene();
		
		
		// 3. Initialize the Search/Results Scene (MealSearchSceneCreator)
		MealSearchSceneCreator searchSceneCreator = new MealSearchSceneCreator();
		searchScene = searchSceneCreator.createScene();
		
		// 4. Configure Window Properties
		primaryStage.setTitle("MealLab Application");
		
		// 5. Set the initial scene (Menu)
		primaryStage.setScene(mainScene);
		
		// 6. Display the Application Window
		primaryStage.show();
		}
	
		public static void main(String[] args) {
		// Launch the JavaFx runtime
		launch(args);
	}

}
