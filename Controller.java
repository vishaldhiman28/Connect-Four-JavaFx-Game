package com.Dhiman_vishal.connect4;

import com.sun.org.apache.regexp.internal.RECompiler;
import com.sun.prism.impl.BufferUtil;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS =7;
	private static final int ROWS=6;
	private static final int CIRCLE_Diameter=80;
	private static final String D1_COLOUR="#24303E";
	private static final String D2_COLOUR="#4CAA88";

	private  static String PLAYER1="Player One";
	private static String Player2="Player Two";

	private boolean isPlayer1Turn=true;


	public Disc[][] insertedDiscArray=new Disc[ROWS][COLUMNS];

	@FXML
	public GridPane rootGPane;
	@FXML
	public Pane insertedDiscPane;
	@FXML
	public Label playerName;

	@FXML
	public Button setNamesButton;
	@FXML
	public TextField playerOneTextField,playerTwoTextField;

    private boolean isAllowedToInsert=true;

   public void createPlayGround(){

   	  setNamesButton.setOnAction(event ->
      {
      	PLAYER1=playerOneTextField.getText();
   	   Player2=playerTwoTextField.getText();

   	   playerName.setText(PLAYER1);


      });
   	  Shape rectangleWithWholes=createGameStructuralGrid();
   	  rootGPane.add(rectangleWithWholes,0,1);

   	  List<Rectangle> rectanglelist=createClickableGrid();
	   for (Rectangle rectangle: rectanglelist) {


       	  rootGPane.add(rectangle,0,1);

	   }
   }
   private Shape createGameStructuralGrid(){
	   Shape rectangleWithWholes=new Rectangle((COLUMNS+1)*CIRCLE_Diameter,(ROWS+1)*CIRCLE_Diameter);

	   for (int row = 0; row <ROWS ; row++) {
		   for (int col= 0; col <COLUMNS;col++) {


			   Circle circle = new Circle();
			   circle.setRadius(CIRCLE_Diameter / 2);
			   circle.setCenterX(CIRCLE_Diameter / 2);
			   circle.setCenterY(CIRCLE_Diameter / 2);
               circle.setSmooth(true);
			   circle.setTranslateX(col * (CIRCLE_Diameter+5)+CIRCLE_Diameter/4);
			   circle.setTranslateY(row * (CIRCLE_Diameter+5)+CIRCLE_Diameter/4);



			   rectangleWithWholes = Shape.subtract(rectangleWithWholes, circle);

		   }
	   }
	   rectangleWithWholes.setFill(Color.WHITE);


      return rectangleWithWholes;
   }

   private List<Rectangle> createClickableGrid() {

	   List<Rectangle> rectangleList=new ArrayList<>();
	   for (int col = 0; col < COLUMNS; col++) {



	   Rectangle rectangle = new Rectangle(CIRCLE_Diameter, (ROWS + 1) * CIRCLE_Diameter);
	   rectangle.setFill(Color.TRANSPARENT);
	   rectangle.setTranslateX(col*(CIRCLE_Diameter+5)+CIRCLE_Diameter / 4);

	   rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee50")));
	   rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
       rectangleList.add(rectangle);
		   final int column=col;
       rectangle.setOnMouseClicked(event -> {
        if(isAllowedToInsert) {
	          isAllowedToInsert=false;
	          inserteDisc(new Disc(isPlayer1Turn), column);
        }
        });
   }
	   return rectangleList;
   }
   private void inserteDisc(Disc disc,int column){

   	  int row=ROWS-1;
   	  while(row>=0){
   	  	if(getDiscIfPresent(row,column)==null){
   	  		break;
        }
        row--;
      }
      if(row<0)
      	return;

   	  insertedDiscArray[row][column]=disc;
   	  insertedDiscPane.getChildren().add(disc);

   	  disc.setTranslateX(column*(CIRCLE_Diameter+5)+CIRCLE_Diameter/4);
	   int currentRow=row;
   	   TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);
	   translateTransition.setToY(row*(CIRCLE_Diameter+5)+CIRCLE_Diameter/4);
	   translateTransition.play();
       translateTransition.setOnFinished((ActionEvent event) -> {
       	 isAllowedToInsert=true;
       	  if(gameEnded(currentRow, column)){
                   gameOver();
        }
       	isPlayer1Turn=!isPlayer1Turn;
       	playerName.setText(isPlayer1Turn?PLAYER1:Player2);
       });
       
   	}
   	private boolean gameEnded(int row,int column){

	  List<Point2D> verticalPoints=  IntStream.rangeClosed(row-3,row+3).mapToObj(r->new Point2D(r,column)).collect(Collectors.toList());
	  List<Point2D> horizontalPoints=  IntStream.rangeClosed(column-3,column+3).mapToObj(c->new Point2D(row,c)).collect(Collectors.toList());

	  boolean isEnded=checkCombinations(verticalPoints)||checkCombinations(horizontalPoints);
	  return isEnded;
   }

	private boolean checkCombinations(List<Point2D> points) {
	 int chain=0;
   	 for (Point2D point: points) {
	     int rowIndexForArray = (int) point.getX();
	     int columnIndexForArray = (int) point.getY();
	     Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);
	     if (disc != null && disc.isPlayer1Turn == isPlayer1Turn) {
		     chain++;
		     if (chain == 4) {
			     return true;
		     }
	       }
	       else {
			     chain = 0;
		     }
	     }

     return false;
   	 }
	private Disc getDiscIfPresent(int row,int column){
   	 if(row>=ROWS||row<0||column>=COLUMNS||column<0)
		return  null;
     else
     	return insertedDiscArray[row][column];
	}

	private void gameOver() {
   	 String winner=isPlayer1Turn?PLAYER1:Player2;
		System.out.println("Winner is:="+winner);
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("The Winner is "+winner);
		alert.setContentText("Want to play again?");
		ButtonType yesBtn =new ButtonType("Yes");
		ButtonType noBtn=new ButtonType("No,Exit");
		alert.getButtonTypes().setAll(yesBtn,noBtn);
        Platform.runLater(()-> {
        	Optional<ButtonType> btnClicked= alert.showAndWait();

		if(btnClicked.isPresent()&&btnClicked.get()==yesBtn){
			resetGame();
		}else{
			Platform.exit();
			System.exit(0);
		}});
   }

	public void resetGame() {
   	 insertedDiscPane.getChildren().clear();
		for (int row = 0; row <insertedDiscArray.length ; row++) {
			for (int col = 0; col <insertedDiscArray[row].length ; col++) {
				insertedDiscArray[row][col]=null;
			}

		}
		playerOneTextField.clear();
		playerTwoTextField.clear();
		PLAYER1="Player One";
		Player2="Player Two";
		isPlayer1Turn=true;
		playerName.setText(PLAYER1);
		createPlayGround();
	}

	private static  class Disc extends Circle {
	    private final boolean isPlayer1Turn;

	    public Disc(boolean isPlayer1Turn) {
		    this.isPlayer1Turn = isPlayer1Turn;
		    setRadius(CIRCLE_Diameter / 2);
		    setFill(isPlayer1Turn ? Color.valueOf(D1_COLOUR) : Color.valueOf(D2_COLOUR));
		    setCenterX(CIRCLE_Diameter / 2);
		    setCenterY(CIRCLE_Diameter / 2);
	    }
    }
	    @Override
	    public void initialize(URL location, ResourceBundle resources) {

	    }
    }
