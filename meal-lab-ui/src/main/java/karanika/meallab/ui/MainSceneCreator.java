package karanika.meallab.ui;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;


// Creator for the Main Menu Scene. Displays navigation buttons
public class MainSceneCreator implements EventHandler<MouseEvent> {
	
	// Root container for the scene layout
	FlowPane rootFlowPane;
	
	// Navigation Button
	Button searchBtn; 
	
	public MainSceneCreator() {
		// Initialize the root container
		rootFlowPane = new FlowPane ();
		
		// Initialize UI controls
		searchBtn = new Button("Go to Meal Search");
		
		// Register Event Handler (this class handles the click)
		searchBtn.setOnMouseClicked(this);
		
		// Configure Layout (Styling)
		rootFlowPane.setAlignment(Pos.CENTER);
		rootFlowPane.setHgap(10);
		
		// Set Button Size
		searchBtn.setMinSize(200, 50);
		
		// Add button to the layout
		rootFlowPane.getChildren().add(searchBtn);
		}
	
	//Creates and returns the Scene object
	Scene createScene() {
		return new Scene(rootFlowPane, 800, 600);
		}
	
	@Override
	public void handle(MouseEvent event) {
		// Check if the source of the click is the search button
		if (event.getSource() == searchBtn) {
			// Switch to the Search Scene
			MealLabApp.primaryStage.setScene(MealLabApp.searchScene);
			}
	    }
	}
