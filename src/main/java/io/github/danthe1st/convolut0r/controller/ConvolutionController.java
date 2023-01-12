package io.github.danthe1st.convolut0r.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.danthe1st.convolut0r.model.Convolution;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ConvolutionController implements Initializable {
	
	private static final Convolution IDENTITY_CONVOLUTION = new Convolution(
			new double[][][] {
					{
							{ 0, 0, 0 },
							{ 0, 1, 0 },
							{ 0, 0, 0 }
					},
					{
							{ 0, 0, 0 },
							{ 0, 1, 0 },
							{ 0, 0, 0 }
					},
					{
							{ 0, 0, 0 },
							{ 0, 1, 0 },
							{ 0, 0, 0 }
					}
			}, 1, 1
	);
	@FXML
	private GridPane convolutionInputR;
	@FXML
	private GridPane convolutionInputG;
	@FXML
	private GridPane convolutionInputB;
	private GridPane[] convolutionInputPanes;
	@FXML
	private Canvas inputCanvas;
	@FXML
	private Canvas outputCanvas;
	@FXML
	private CheckBox grayscaleCheckbox;
	@FXML
	private Spinner<Integer> paddingSpinner;
	@FXML
	private Spinner<Integer> stridingSpinner;
	@FXML
	private HBox conlvolutionInputHolder;
	@FXML
	private ComboBox<Map.Entry<String, Convolution>> presetSelector;
	
	private final Map<String, Convolution> PRESETS = Map.ofEntries(
			Map.entry(
					"horizontal edge detection", new Convolution(
							divideArrayBy(
									new double[][] {
											{ -1, -2, -1 },
											{ 0, 0, 0 },
											{ 1, 2, 1 }
									}, 9
							)
					)
			),
			Map.entry(
					"vertical edge detection",
					new Convolution(
							divideArrayBy(
									new double[][] {
											{ -1, 0, 1 },
											{ -2, 0, 2 },
											{ -1, 0, 1 }
									}, 9
							)
					)
			),
			Map.entry(
					"horizontal filter",
					new Convolution(
							divideArrayBy(
									new double[][] {
											{ 1, 1, -1, -1 },
											{ 1, 1, -1, -1 },
											{ 1, 1, -1, -1 },
											{ 1, 1, -1, -1 }
									}, 9
							)
					)
			),
			Map.entry(
					"vertical filter",
					new Convolution(
							divideArrayBy(
									new double[][] {
											{ 1, 1, 1, 1 },
											{ 1, 1, 1, 1 },
											{ -1, -1, -1, -1 },
											{ -1, -1, -1, -1 }
									}, 9
							)
					)
			),
			Map.entry(
					"Prewitt vertical",
					new Convolution(
							divideArrayBy(
									new double[][] {
											{ 1, 0, -1 },
											{ 1, 0, -1 },
											{ 1, 0, -1 }
									}, 9
							)
					)
			),
			Map.entry(
					"Prewitt horizontal",
					new Convolution(
							divideArrayBy(
									new double[][] {
											{ 1, 1, 1 },
											{ 0, 0, 0 },
											{ -1, -1, -1 }
									}, 9
							)
					)
			),
			Map.entry(
					"Gaussian Blur",
					new Convolution(
							getGaussianBlurMatrix()
					)
			),
			Map.entry(
					"only red",
					new Convolution(
							new double[][][] {
									{ { 1 } }, { { 0 } }, { { 0 } }
							}
					)
			),
			Map.entry(
					"only green",
					new Convolution(
							new double[][][] {
									{ { 0 } }, { { 1 } }, { { 0 } }
							}
					)
			),
			Map.entry(
					"only blue",
					new Convolution(
							new double[][][] {
									{ { 0 } }, { { 0 } }, { { 1 } }
							}
					)
			),
			// https://en.wikipedia.org/wiki/Kernel_(image_processing)
			Map.entry(
					"identity (3x3)", IDENTITY_CONVOLUTION
			),
//			Map.entry(
//					"generic edge detection", new Convolution(
//							new double[][] {
//									{ 0, -1, 0 },
//									{ -1, 4, -1 },
//									{ 0, -1, 0 }
//							}
//					)
//			),
			Map.entry(
					"simple blur", new Convolution(
							new double[][][] {
									divideArrayBy(
											new double[][] {
													{ 1, 1, 1 },
													{ 1, 1, 1 },
													{ 1, 1, 1 }
											}, 9
									),
									divideArrayBy(
											new double[][] {
													{ 1, 1, 1 },
													{ 1, 1, 1 },
													{ 1, 1, 1 }
											}, 9
									),
									divideArrayBy(
											new double[][] {
													{ 1, 1, 1 },
													{ 1, 1, 1 },
													{ 1, 1, 1 }
											}, 9
									)
							}
					)
			),
			Map.entry(
					"sharpen", new Convolution(
							new double[][][] {
									{
											{ 0, -1, 0 },
											{ -1, 5, 0 },
											{ 0, -1, 0 }
									},
									{
											{ 0, -1, 0 },
											{ -1, 5, 0 },
											{ 0, -1, 0 }
									},
									{
											{ 0, -1, 0 },
											{ -1, 5, 0 },
											{ 0, -1, 0 }
									}
							}
					)
			)
	);
	
	private double[][][] getGaussianBlurMatrix() {
		double[][] singleDimension = { { 1, 4, 6, 4, 1 },
				{ 4, 16, 24, 14, 4 },
				{ 6, 24, 36, 24, 6 },
				{ 4, 16, 24, 14, 4 },
				{ 1, 4, 6, 4, 1 } };
		divideArrayBy(singleDimension, 256.0);
		return new double[][][] {
				singleDimension, singleDimension, singleDimension
		};
	}
	
	private double[][] divideArrayBy(double[][] arr, double divisor) {
		for(int i = 0; i < arr.length; i++){
			for(int j = 0; j < arr[i].length; j++){
				arr[i][j] = arr[i][j] / divisor;
			}
		}
		return arr;
	}
	
	@SuppressWarnings("unchecked")
	private Spinner<Double>[][][] inputSpinners = (Spinner<Double>[][][]) new Spinner<?>[3][3][3];
	private Image inputImage;
	
	private ExecutorService convolutorThread = Executors.newSingleThreadExecutor();
	
	private Stage stage;
	
	@FXML
	void convolute(ActionEvent event) {
		int pixelWidth = (int) inputImage.getWidth();
		int pixelHeight = (int) inputImage.getHeight();
		boolean grayscale = grayscaleCheckbox.isSelected();
		Convolution convolution = loadConvolutionFromUI(grayscale);
		convolutorThread.execute(() -> {
			int channelCount = 3;
			int[][][] pixels = new int[grayscale ? 1 : channelCount][pixelWidth][pixelHeight];
			for(int i = 0; i < pixels[0].length; i++){
				for(int j = 0; j < pixels[0][i].length; j++){
					int argb = inputImage.getPixelReader().getArgb(i, j);
					if(grayscale){
						int r = ((argb >> 16) & 0xFF);
						int g = (argb >> 8) & 0xFF;
						int b = argb & 0xFF;
//						pixels[0][i][j] = (int)((r+g+b)/3);
						pixels[0][i][j] = (int) (0.299 * r + 0.587 * g + 0.114 * b);// https://stackoverflow.com/a/15056209/10871900
					}else{
						for(int c = 0; c < channelCount; c++){
							pixels[channelCount - c - 1][i][j] = (argb >> (8 * c)) & 0xFF;
						}
						
					}
				}
			}
			pixels = convolution.convolute(pixels);
			WritableImage outputImage = new WritableImage(pixels[0].length, pixels[0][0].length);
			for(int i = 0; i < pixels[0].length; i++){
				for(int j = 0; j < pixels[0][i].length; j++){
					int argb = 0;
					if(grayscale){
						int pixelValue = pixels[0][i][j] & 0xFF;
						argb = 0xFF << 24 | pixelValue << 16 | pixelValue << 8 | pixelValue;
					}else{
						argb = 0xFF000000;
						for(int c = 0; c < channelCount; c++){
							argb |= (pixels[channelCount - c - 1][i][j] & 0xFF) << (8 * c);
						}
					}
					outputImage.getPixelWriter().setArgb(i, j, argb);
				}
			}
			Platform.runLater(() -> {
				outputCanvas.getGraphicsContext2D().clearRect(0, 0, outputCanvas.getWidth(), outputCanvas.getHeight());
				outputCanvas.getGraphicsContext2D().drawImage(outputImage, 0, 0, outputCanvas.getWidth(), outputCanvas.getHeight());
			});
			
		});
	}
	
	@FXML
	void selectPreset(ActionEvent event) {
		saveConvolutionToUI(presetSelector.getSelectionModel().getSelectedItem().getValue());
	}
	
	@FXML
	void selectImage(ActionEvent event) {
		selectImage();
	}
	
	private Convolution loadConvolutionFromUI(boolean grayscale) {
		return new Convolution(getConvolutionArray(grayscale), stridingSpinner.getValue(), paddingSpinner.getValue());
	}
	
	@SuppressWarnings("unchecked")
	private void saveConvolutionToUI(Convolution convolution) {
		double[][][] conv = convolution.convolution();
		if(conv.length != 1 && conv.length != 3){
			throw new IllegalArgumentException("only convolutions with 1 or 3 channels allowed");
		}
		grayscaleCheckbox.setSelected(conv.length == 1);
		inputSpinners = (Spinner<Double>[][][]) new Spinner<?>[3][conv[0].length][conv[0].length];
		for(int k = 0; k < 3; k++){
			convolutionInputPanes[k].getChildren().clear();
			convolutionInputPanes[k].getColumnConstraints().clear();
			for(int i = 0; i < conv[0].length; i++){
				for(int j = 0; j < conv[0][i].length; j++){
					createConvolutionSpinner(i, j, inputSpinners[k], convolutionInputPanes[k], k < conv.length ? (conv[k][j][i]) : 0);
				}
				ColumnConstraints cc = new ColumnConstraints();
				cc.setPercentWidth(100.0 / conv[0].length);
				convolutionInputPanes[k].getColumnConstraints().add(cc);
			}
		}
		stridingSpinner.getValueFactory().setValue(convolution.striding());
		paddingSpinner.getValueFactory().setValue(convolution.padding());
	}
	
	private double[][][] getConvolutionArray(boolean grayscale) {
		double[][][] ret = new double[grayscale ? 1 : 3][inputSpinners[0].length][inputSpinners[0][0].length];
		for(int i = 0; i < ret[0].length; i++){
			for(int j = 0; j < ret[0][i].length; j++){
				ret[0][i][j] = inputSpinners[0][i][j].getValue();
				if(!grayscale){
					ret[1][i][j] = inputSpinners[1][i][j].getValue();
					ret[2][i][j] = inputSpinners[2][i][j].getValue();
				}
			}
		}
		return ret;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		convolutionInputPanes = new GridPane[] { convolutionInputR, convolutionInputG, convolutionInputB };
		for(GridPane gridPane : convolutionInputPanes){
			gridPane.setBorder(
					new Border(
							new BorderStroke(
									Color.BLACK,
									BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT
							)
					)
			);
			gridPane.setGridLinesVisible(true);
		}
		paddingSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
		stridingSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
		saveConvolutionToUI(IDENTITY_CONVOLUTION);
		GraphicsContext gc = inputCanvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, inputCanvas.getWidth(), inputCanvas.getHeight());
		gc.setFill(Color.BLACK);
		gc.setStroke(Color.BLACK);
		gc.fillOval(inputCanvas.getWidth() / 5, inputCanvas.getHeight() / 5, inputCanvas.getWidth() / 10, inputCanvas.getHeight() / 10);
		gc.fillOval(inputCanvas.getWidth() - inputCanvas.getWidth() / 3, inputCanvas.getHeight() / 5, inputCanvas.getWidth() / 10, inputCanvas.getHeight() / 10);
		gc.strokeOval(inputCanvas.getWidth() / 5, inputCanvas.getHeight() / 2, 3 * inputCanvas.getWidth() / 5, inputCanvas.getHeight() / 3);
		gc.setFill(Color.WHITE);
		gc.fillRect(0, inputCanvas.getHeight() / 2 - 1, inputCanvas.getWidth(), inputCanvas.getHeight() / 4);
		inputImage = inputCanvas.snapshot(null, null);
		convolutionInputG.visibleProperty().bind(grayscaleCheckbox.selectedProperty().not());
		convolutionInputB.visibleProperty().bind(grayscaleCheckbox.selectedProperty().not());
		
		presetSelector.setCellFactory(param -> new ListCell<>() {
			{
				setContentDisplay(ContentDisplay.TEXT_ONLY);
			}
			
			@Override
			public void updateItem(Map.Entry<String, Convolution> item, boolean empty) {
				super.updateItem(item, empty);
				if(item == null){
					setText(null);
				}else{
					setText(item.getKey());
				}
			}
		});
		presetSelector.setConverter(new StringConverter<Map.Entry<String, Convolution>>() {
			
			@Override
			public String toString(Entry<String, Convolution> e) {
				return e == null ? null : e.getKey();
			}
			
			@Override
			public Entry<String, Convolution> fromString(String key) {
				return Map.entry(key, PRESETS.get(key));
			}
		});
		presetSelector.getItems().addAll(PRESETS.entrySet());
	}
	
	private void createConvolutionSpinner(int i, int j, Spinner<Double>[][] spinnerStorage, GridPane position, double defaultValue) {
		Spinner<Double> spinner = new Spinner<>(-255, 255, defaultValue);
		spinner.setEditable(true);
		spinner.setValueFactory(new DoubleSpinnerValueFactory(-10, 10, defaultValue));
		spinnerStorage[i][j] = spinner;
		position.add(spinner, i, j);
		spinner.setPrefWidth(75);
	}
	
	public void selectImage() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("pick image to convolute");
		chooser.getExtensionFilters().add(new ExtensionFilter("Image", List.of("*.jpg", "*.jpeg", "*.png", "*.gif")));
		File imageFile = chooser.showOpenDialog(stage);
		if(imageFile == null || !imageFile.exists()){
			Alert alert = new Alert(AlertType.WARNING, "selected image does not exist");
			alert.setTitle("image Error");
			alert.show();
			return;
		}
		inputImage = new Image(imageFile.toURI().toString());
		drawInputImage();
	}
	
	private void drawInputImage() {
		// TODO adapt canvas size
		inputCanvas.getGraphicsContext2D().clearRect(0, 0, inputCanvas.getWidth(), inputCanvas.getHeight());
		inputCanvas.getGraphicsContext2D().drawImage(inputImage, 0, 0, inputCanvas.getWidth(), inputCanvas.getHeight());
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
}
