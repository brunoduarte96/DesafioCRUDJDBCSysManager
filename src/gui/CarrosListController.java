package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChanceListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Carros;
import model.exceptions.DbIntegrityException;
import model.service.CarrosService;
import model.service.FabricanteService;

public class CarrosListController implements Initializable, DataChanceListener {

	private CarrosService service;

	@FXML
	private TableView<Carros> tableViewCarros;

	@FXML
	private TableColumn<Carros, Integer> tableColumnId;
	@FXML
	private TableColumn<Carros, String> tableColumnMarca;
	@FXML
	private TableColumn<Carros, String> tableColumnModelo;
	@FXML
	private TableColumn<Carros, Integer> tableColumnAno;
	@FXML
	private TableColumn<Carros, Double> tableColumnPreco;

	@FXML
	private TableColumn<Carros, Carros> tableColumnEDIT;
	
	@FXML
	private TableColumn<Carros, Carros> tableColumnREMOVE; 


	@FXML
	private Button btCadastrar;

	private ObservableList<Carros> obsList;

	@FXML
	public void onBtCadastrar(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Carros car = new Carros();
		createDialogForm(car, "/gui/CarrosForm.fxml", parentStage);
	}
	//injetar a instância do Carrosservice.
	public void setCarrosService(CarrosService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
		tableColumnModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
		tableColumnAno.setCellValueFactory(new PropertyValueFactory<>("ano"));
		tableColumnPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
		Utils.formatTableColumnDouble(tableColumnPreco, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewCarros.prefHeightProperty().bind(stage.heightProperty());
	}
	//lista a tabela atualizada e adiciona os botões de editar e excluir.
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("O Serviço estava nulo");

		}

		List<Carros> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewCarros.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	//Vai criar uma nova janela para atualizar puxando as informações do id selecionado.
	private void createDialogForm(Carros car, String absoluteName, Stage parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			CarrosFormController controller = loader.getController();
			controller.setCarros(car);
			controller.setServices(new CarrosService(),new FabricanteService());
			controller.loadAssociatedObjects();
			controller.subscribeDataChanceListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Digite os dados do carro");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		}

		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Carros, Carros>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Carros car, boolean empty) {
				super.updateItem(car, empty);
				if (car == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(car, "/gui/CarrosForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	
	private void initRemoveButtons() { 
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Carros, Carros>() { 
		 private final Button button = new Button("Excluir"); 
		 @Override
		 protected void updateItem(Carros car, boolean empty) { 
		 super.updateItem(car, empty); 
		 if (car == null) { 
		 setGraphic(null); 
		 return; 
		 } 
		 setGraphic(button); 
		 button.setOnAction(event -> removeEntity(car)); 
		 } 
		 }); 
		}

	private void removeEntity(Carros car) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza de que deseja excluir?");
	
		if(result.get() == ButtonType.OK) {
			if(service == null) {
					throw new IllegalStateException("O serviço está nulo");
			}
			try {
			service.remover(car);
			updateTableView();
			
			}
			catch( DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover o objeto", null, e.getMessage(), AlertType.ERROR);
				}
			}
	
	
	} 

}
