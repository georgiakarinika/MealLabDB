package karanika.meallab.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image; 
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import karanika.meallab.model.MealLabInfo; 
import karanika.meallab.services.MealLabServices;
import karanika.meallab.exception.MealLabException;

/**
 * Creates the Search & Results Scene.
 * Implements a Tab-based interface:
 * - Tab 1: Search, Details, Image, Actions (Add to Fav/Cooked).
 * - Tab 2: Favorites List.
 * - Tab 3: Cooked History List.
 */

public class MealSearchSceneCreator implements EventHandler<MouseEvent> {
	
	//--- Data Lists ---
	// ObservableLists update the TableViews automatically when modified
	private ObservableList<MealLabInfo> favoritesList;
	private ObservableList<MealLabInfo> cookedList;
	
	// --- UI Components ---
	TabPane mainTabPane; 
	
	// Tab 1: Search Components 
	HBox topControlsBox;      // Holds the inputs at the top
	VBox rightSidePane;       // Holds the Image and Action Buttons on the right
	GridPane searchGridPane;
	 
	
	// UI Controls: Buttons
	Button searchByNameBtn, searchByIngredientBtn, searchByIdBtn, randomBtn, backBtn;
	Button addToFavBtn, markCookedBtn;    // Action Buttons
	
	// UI Controls: Inputs & Labels
	Label textLbl, instructionsLbl, ingredientsLbl;
	
	// Scene text field
	TextField paramField;
	
	// UI Controls: Data Display
	TableView<MealLabInfo> searchTableView;
	TextArea instructionsArea, ingredientsArea;
	ImageView mealImageView;
	
	// Tab 2 & Tab 3: Lists Components
	TableView<MealLabInfo> favTableView;
	TableView<MealLabInfo> cookedTableView;
	Button removeFromFavBtn, removeFromCookedBtn;
	
	//API Configuration
	private static final String apiUrl = "https://www.themealdb.com/api/json/v1/";
	private static final String apiKey = "1";
	
	// Files to store data
		private static final String FAV_FILE = "favorites.json";
		private static final String COOKED_FILE = "cooked.json";
		private ObjectMapper mapper = new ObjectMapper();
	
	public MealSearchSceneCreator() {
		// 1. Initialize Data Lists
		favoritesList = FXCollections.observableArrayList();
		cookedList = FXCollections.observableArrayList();
		
		// Load data from disk when app starts
				loadData();
				
				// Auto-save when lists change
				favoritesList.addListener((ListChangeListener<MealLabInfo>) c -> saveData());
				cookedList.addListener((ListChangeListener<MealLabInfo>) c -> saveData());
		
		
		// 2. Initialize Main Container (TabPane)
		mainTabPane = new TabPane();
		mainTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);  // Prevent closing tabs
		
		// --- SET UP TAB 1: SEARCH ---
		Tab searchTab = new Tab("Search Recipes");
		searchTab.setContent(createSearchContent());
		
		// --- SET UP TAB 2: FAVORITES ---
		Tab favTab = new Tab("Favorites");
		favTab.setContent(createFavoritesContent());
		
		// --- SET UP TAB 3: COOKED HISTORY ---
		Tab cookedTab = new Tab("Cooked History");
		cookedTab.setContent(createCookedContent());
		
		// Add Tabs to the Pane
		mainTabPane.getTabs().addAll(searchTab, favTab, cookedTab);
		
						
		// 3. Register Event Handlers
		searchByNameBtn.setOnMouseClicked(this);
		searchByIngredientBtn.setOnMouseClicked(this);
		searchByIdBtn.setOnMouseClicked(this);
		randomBtn.setOnMouseClicked(this);
		backBtn.setOnMouseClicked(this);
		
		addToFavBtn.setOnMouseClicked(this);
		markCookedBtn.setOnMouseClicked(this);
		
		removeFromFavBtn.setOnMouseClicked(this);
		removeFromCookedBtn.setOnMouseClicked(this);
	}	
		
		
//---------------------------------------Helper Method: Creates the content for the "Search" tab--------------------------------
		 
		//Includes inputs, buttons, table, details, image, and action buttons
	private BorderPane createSearchContent() {
		BorderPane root = new BorderPane();

		// --- Top Header Section ---
		// Instead of scattering buttons, we group them all in a top HBox.
		textLbl = new Label("Search Parameter: ");
		paramField = new TextField();
		paramField.setPrefWidth(200);

		searchByNameBtn = new Button("Search Name");
		searchByIngredientBtn = new Button("Search Ingredient");
		searchByIdBtn = new Button("Search ID");
		randomBtn = new Button("Random Meal");

		topControlsBox = new HBox(15); // 15px spacing
		topControlsBox.setAlignment(Pos.CENTER_LEFT);
		topControlsBox.setPadding(new Insets(10));
		topControlsBox.getChildren().addAll(textLbl, paramField, searchByNameBtn, searchByIngredientBtn, searchByIdBtn, randomBtn);


		// --- The Main Grid ---
		searchGridPane = new GridPane();
		searchGridPane.setHgap(20);
		searchGridPane.setVgap(15);
		searchGridPane.setPadding(new Insets(20));

		// Define Columns (60% Left for Table, 40% Right for Image)
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(45); 
		
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(55);
		
		searchGridPane.getColumnConstraints().addAll(col1, col2);

		// --- Left Side: Table ---
		searchTableView = createMealTable();
		searchTableView.setPrefHeight(350); // Fixed minimum height
		
		// Add Listener
		searchTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) updateDetails(newSelection);
		});

		// --- Right Side Container ---
		// Grouping the Image and the Action Buttons vertically
		mealImageView = new ImageView();
		mealImageView.setFitWidth(280); 
		mealImageView.setFitHeight(280);
		mealImageView.setPreserveRatio(true);

		addToFavBtn = new Button("💚 Add to Favorites");
		markCookedBtn = new Button("✅ Mark as Cooked");
		addToFavBtn.setStyle("-fx-background-color: #ffcccc; -fx-font-weight: bold;");
		markCookedBtn.setStyle("-fx-background-color: #ccffcc; -fx-font-weight: bold;");
		
		// Buttons expand to match image width
		addToFavBtn.setMaxWidth(Double.MAX_VALUE);
		markCookedBtn.setMaxWidth(Double.MAX_VALUE);

		VBox actionBtnsBox = new VBox(10, addToFavBtn, markCookedBtn);
		actionBtnsBox.setAlignment(Pos.CENTER);
		

		rightSidePane = new VBox(20); // 20px spacing
		rightSidePane.setAlignment(Pos.TOP_CENTER);
		rightSidePane.getChildren().addAll(mealImageView, actionBtnsBox);

		// --- Bottom Text Areas (Full Width) ---
		ingredientsLbl = new Label("Ingredients & Measures:");
		ingredientsLbl.setStyle("-fx-font-weight: bold;");
		ingredientsArea = new TextArea();
		ingredientsArea.setWrapText(true);
		ingredientsArea.setEditable(false);
		ingredientsArea.setPrefHeight(200);
		ingredientsArea.setMaxWidth(Double.MAX_VALUE); // Expand horizontally

		instructionsLbl = new Label("Cooking Instructions:");
		instructionsLbl.setStyle("-fx-font-weight: bold;");
		instructionsArea = new TextArea();
		instructionsArea.setWrapText(true);
		instructionsArea.setEditable(false);
		instructionsArea.setPrefHeight(200);
		instructionsArea.setMaxWidth(Double.MAX_VALUE); // Expand horizontally

		// --- PLACING ITEMS IN GRID ---
		// Row 0: Table (Left - Col 0) and Image Pane (Right - Col 1)
		searchGridPane.add(searchTableView, 0, 0); 
		searchGridPane.add(rightSidePane, 1, 0);

		// Row 1: Ingredients colSpan = 2 (Occupies full width)
		searchGridPane.add(ingredientsLbl, 0, 1, 2, 1);
		searchGridPane.add(ingredientsArea, 0, 2, 2, 1);

		// Row 2: Instructions colSpan = 2 (Occupies full width)
		searchGridPane.add(instructionsLbl, 0, 3, 2, 1);
		searchGridPane.add(instructionsArea, 0, 4, 2, 1);

		
		// --- Footer: Back Button ---
		backBtn = new Button("Go Back to Menu");
		HBox bottomBox = new HBox(backBtn);
		bottomBox.setAlignment(Pos.CENTER);
		bottomBox.setPadding(new Insets(10));

		// --- Final Assembly ---
		// Using BorderPane regions for clean separation
		root.setTop(topControlsBox);
		root.setCenter(searchGridPane);
		root.setBottom(bottomBox);

		return root;
	}
	
//-----------------Helper Method: Creates the content for the "Favorites" tab------------------------	
	
	private BorderPane createFavoritesContent() {
		BorderPane root = new BorderPane();
		VBox layout = new VBox(10);
		layout.setStyle("-fx-padding: 20;");
		
		Label title = new Label("My Favorite Recipes");
		title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		
		favTableView = createMealTable();
		favTableView.setItems(favoritesList); // Bind to ObservableList
		
		favTableView.setOnMouseClicked(e -> {
			    if (e.getClickCount() == 2) {
			    	MealLabInfo selected = favTableView.getSelectionModel().getSelectedItem();
			    	if (selected != null) {
			    		updateDetails(selected);
			    		
			    		searchTableView.getItems().clear();
						searchTableView.getItems().add(selected);
						searchTableView.getSelectionModel().select(0);
						
			    		mainTabPane.getSelectionModel().select(0);
			    	}
			    }
		});
		Label hintLbl = new Label("(Double-click a recipe to view details)");
		hintLbl.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
		
		removeFromFavBtn = new Button("Remove Selected from Favorites");
		
		layout.getChildren().addAll(title, favTableView, hintLbl, removeFromFavBtn);
		root.setCenter(layout);
		return root;
	}
		
//----------------------------Helper Method: Creates the content for the "Cooked" tab-------------------------------
	
private BorderPane createCookedContent() {
		BorderPane root = new BorderPane();
		VBox layout = new VBox(10);
		layout.setStyle("-fx-padding: 20;");
		
		Label title = new Label("Recipes I Have Cooked");
		title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		
		cookedTableView = createMealTable();
		cookedTableView.setItems(cookedList);      // Bind to ObservableList
		
		cookedTableView.setOnMouseClicked(e -> {
			    if (e.getClickCount() == 2) {
			    	MealLabInfo selected = cookedTableView.getSelectionModel().getSelectedItem();
			    	if (selected != null) {
			    		updateDetails(selected);
			    		
			    		searchTableView.getItems().clear();
						searchTableView.getItems().add(selected);
						searchTableView.getSelectionModel().select(0);
						
			    		mainTabPane.getSelectionModel().select(0);
			    	}
			    }
		});
		
		Label hintLbl = new Label("(Double-click a recipe to view details)");
		hintLbl.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
		
		removeFromCookedBtn = new Button("Remove Selected from History");
		
		layout.getChildren().addAll(title, cookedTableView, hintLbl, removeFromCookedBtn);
		root.setCenter(layout);
		return root;
	}
	
//--------------------------------Helper Method: Create a standard TableView with required columns------------------------------	
		   
	private TableView<MealLabInfo> createMealTable() {
		    TableView<MealLabInfo> table = new TableView<>();
	     	
		
		// Column 1. ID
		TableColumn<MealLabInfo, String> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		idColumn.setPrefWidth(60);
		idColumn.setResizable(false);
				
		// Column 2: Name
		TableColumn<MealLabInfo, String> nameColumn = new TableColumn<>("Meal Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		
				
		// Column 3: Category
		TableColumn<MealLabInfo, String> categoryColumn = new TableColumn<>("Category");
		categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
		categoryColumn.setPrefWidth(90);
		categoryColumn.setResizable(false);
				
		// Column 4: Area 
		TableColumn<MealLabInfo, String> areaColumn = new TableColumn<>("Area");
		areaColumn.setCellValueFactory(new PropertyValueFactory<>("area"));
		areaColumn.setPrefWidth(90);
		areaColumn.setResizable(false);
		
		table.getColumns().addAll(idColumn, nameColumn, categoryColumn, areaColumn);
		
		nameColumn.prefWidthProperty().bind(
				table.widthProperty()
				.subtract(idColumn.widthProperty())
				.subtract(categoryColumn.widthProperty())
				.subtract(areaColumn.widthProperty())
				.subtract(20)
				);
		
		return table;
	}
	
	
//-----------------------------Helper Method: Updates the details fields and image when a meal is selected---------------------------
	
	private void updateDetails(MealLabInfo meal) {
		
			
			instructionsArea.setText(meal.getInstructions());
			ingredientsArea.setText(meal.getFormattedIngredients());
							
            String thumbUrl = meal.getThumbnail();
                if (thumbUrl != null && ! thumbUrl.isEmpty()) {
                	try {
                		// Load image from URL
                		mealImageView.setImage(new Image(thumbUrl));
                	} catch (Exception e) {             
                		// Fail silently if image cannot be loaded
                		mealImageView.setImage(null);
                	}
                }else {
                	  mealImageView.setImage(null);
                }
		}
	
	    Scene createScene() {
	    	     return new Scene(mainTabPane, 1000, 750);
	    }
		
		
// ----------------------------------------- [JSON FEATURE] Save & Load Methods ---------------------
		
		private void saveData() {
			try {
				// Save Favorites
				mapper.writeValue(new File(FAV_FILE), new ArrayList<>(favoritesList));
				// Save Cooked
				mapper.writeValue(new File(COOKED_FILE), new ArrayList<>(cookedList));
				// (Using ArrayList because ObservableList isn't directly serializable)
			} catch (IOException e) {
				e.printStackTrace(); // In real app, maybe log this
			}
		}
		
		private void loadData() {
			try {
				// Load Favorites
				File fFile = new File(FAV_FILE);
				if (fFile.exists()) {
					List<MealLabInfo> loadedFavs = mapper.readValue(fFile, new TypeReference<List<MealLabInfo>>(){});
					favoritesList.addAll(loadedFavs);
				}
				
				// Load Cooked
				File cFile = new File(COOKED_FILE);
				if (cFile.exists()) {
					List<MealLabInfo> loadedCooked = mapper.readValue(cFile, new TypeReference<List<MealLabInfo>>(){});
					cookedList.addAll(loadedCooked);
				}
			} catch (IOException e) {
				e.printStackTrace(); // First run will likely fail or if file is corrupt, just ignore
			}
		}
	
	@Override
	public void handle(MouseEvent event) {
		MealLabServices service = new MealLabServices(apiUrl, apiKey);
		
		
		try {
			
		    // --- SEARCH & RANDOM ACTIONS
			if (event.getSource() == searchByNameBtn || event.getSource() == searchByIngredientBtn ||
					event.getSource() == searchByIdBtn || event.getSource() == randomBtn) {
				
				List<MealLabInfo> results = new ArrayList<>();
				
				// Clear UI elements
				instructionsArea.clear();
				ingredientsArea.clear();
				mealImageView.setImage(null);
				
				
			// Execute API call based on button source
			if (event.getSource() == searchByNameBtn) {
				results = service.searchMealsByName(paramField.getText());
			}else if (event.getSource() == searchByIngredientBtn) {
				 results = service.searchMealsByIngredient(paramField.getText());
			}else if (event.getSource() == searchByIdBtn) {
				 MealLabInfo m = service.getMealDetailsById(paramField.getText());
				 if (m != null) results.add(m) ;
			}else if (event.getSource() == randomBtn) {
				 MealLabInfo m = service.getRandomMeal();
				 if (m != null) results.add(m);
			}
			
			
			// Update Table
			searchTableView.getItems().clear();
			if (results != null && !results.isEmpty()) {
				searchTableView.getItems().addAll(results);
				// Auto-select if only one result
				if (results.size() == 1) searchTableView.getSelectionModel().select(0);
			}else {
				showAlert(AlertType.INFORMATION, "No Results", "No meals found.");
			}
	  }	
			
	 // --- NAVIGATION ---
			else if (event.getSource() == backBtn) {
				MealLabApp.primaryStage.setScene(MealLabApp.mainScene);
			}
				
			// --- FAVORITES LOGIC ---
			
			else if (event.getSource() == addToFavBtn) {
				MealLabInfo selected = searchTableView.getSelectionModel().getSelectedItem();
				if (selected == null) {
					showAlert(AlertType.WARNING, "Selection Eroor", "Please select a meal first");
					return;
				}
				
			// Check for duplicated based on Meal ID
				boolean exists = favoritesList.stream().anyMatch(m -> m.getId().equals(selected.getId()));
				
				if (!exists) {
					  favoritesList.add(selected);
					  showAlert(AlertType.INFORMATION, "Success", "Added to Favorites");
				}else {
					  showAlert(AlertType.WARNING, "Duplicates", "This meal is already in Favorites.");
				}
		    }
			else if (event.getSource() == removeFromFavBtn) {
				MealLabInfo selected = favTableView.getSelectionModel().getSelectedItem();
				if (selected != null) {
					favoritesList.remove(selected);
				}else {
					  showAlert(AlertType.WARNING, "Selection Error", "Select a meal to remove.");
				}
			}
				
			
			// --- COOKED HISTORY LOGIC ---
			else if (event.getSource() == markCookedBtn) {
				MealLabInfo selected = searchTableView.getSelectionModel().getSelectedItem();
				if (selected == null) {
					showAlert(AlertType.WARNING, "Selection Error", "Please select a meal first.");
					return;
				}
				boolean exists = cookedList.stream().anyMatch(m -> m.getId().equals(selected.getId()));
				
				if (!exists) {
					cookedList.add(selected);
					showAlert(AlertType.INFORMATION, "Success", "Marked as Cooked!");
				} else {
					showAlert(AlertType.WARNING, "Duplicate", "This meal is already in Cooked History.");
				}
			}
			else if (event.getSource() == removeFromCookedBtn) {
				MealLabInfo selected = cookedTableView.getSelectionModel().getSelectedItem();
				if (selected != null) {
					cookedList.remove(selected);
				} else {
					showAlert(AlertType.WARNING, "Selection Error", "Select a meal to remove.");
				}
			}
			
		
		} catch (IllegalArgumentException e) {
			// Catch Validation Errors
			showAlert(AlertType.WARNING, "Input Error", e.getMessage());
		} catch (MealLabException e) {
			// Catch API Errors
			showAlert(AlertType.ERROR, "API Error", e.getMessage());
		} catch (Exception e) {
			// Catch Unexpected System Errors
			e.printStackTrace();
			showAlert(AlertType.ERROR, "System Error", e.getMessage());
		}
	
	}

// ---------------------------------Helper Method to display Alerts efficiently----------------------------------
	 private void showAlert(AlertType type, String title, String content) {
		 Alert a = new Alert(type);
		 a.setTitle(title);
		 a.setContentText(content);
		 a.show();
	 }
}	 



