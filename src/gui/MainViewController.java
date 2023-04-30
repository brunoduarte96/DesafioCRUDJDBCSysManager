package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.service.CarrosService;
import model.service.FabricanteService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemFabricantes;

	@FXML
	private MenuItem menuItemCarros;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemFabricantesAction() {
		loadView("/gui/FabricanteList.fxml", (FabricanteListController controller) -> {
		
				try {
					controller.setFabricanteService(new FabricanteService());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemCarrosAction() {
		loadView("/gui/CarrosList.fxml", (CarrosListController controller) -> {
			
			try {
				controller.setCarrosService(new CarrosService());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}
	//loadView é usado para carregar outras telas da aplicação. Ele usa um FXMLLoader para carregar um arquivo FXML e obter o controlador da tela.
	//Em seguida, ele adiciona a tela carregada à cena principal e chama uma função de inicialização personalizada que é passada como um parâmetro para o método.
	//Essa função é responsável por configurar o controlador da tela carregada.
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();

			Scene mainScene = Main.getMainScene();
			VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVbox.getChildren().get(0);
			mainVbox.getChildren().clear();
			mainVbox.getChildren().add(mainMenu);
			mainVbox.getChildren().addAll(newVBox.getChildren());
			
			T controller = loader.getController();
			initializingAction.accept(controller);

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
