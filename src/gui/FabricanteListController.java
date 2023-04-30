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
import model.Fabricante;
import model.exceptions.DbIntegrityException;
import model.service.FabricanteService;

public class FabricanteListController implements Initializable, DataChanceListener {

	private FabricanteService service;

	@FXML
	private TableView<Fabricante> tableViewFabricante;

	@FXML
	private TableColumn<Fabricante, Integer> tableColumnId;
	@FXML
	private TableColumn<Fabricante, String> tableColumnNome;
	@FXML
	private TableColumn<Fabricante, String> tableColumnPaisNome;

	@FXML
	private TableColumn<Fabricante, Fabricante> tableColumnEDIT;
	
	@FXML
	private TableColumn<Fabricante, Fabricante> tableColumnREMOVE; 


	@FXML
	private Button btCadastrar;

	private ObservableList<Fabricante> obsList;
		
	
	//Cria uma nova janela para inserir o cadastro.
	@FXML
	public void onBtCadastrar(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Fabricante fab = new Fabricante();
		createDialogForm(fab, "/gui/FabricanteForm.fxml", parentStage);
	}
	//injetar a instância do fabricanteservice.
	public void setFabricanteService(FabricanteService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnPaisNome.setCellValueFactory(new PropertyValueFactory<>("paisOrigem"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewFabricante.prefHeightProperty().bind(stage.heightProperty());
	}
	
	//lista a tabela atualizada e adiciona os botões de editar e excluir.
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("O Serviço estava nulo");

		}

		List<Fabricante> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewFabricante.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	//Vai criar uma nova janela para atualizar puxando as informações do id selecionado.
	private void createDialogForm(Fabricante fab, String absoluteName, Stage parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FabricanteFormController controller = loader.getController();
			controller.setFabricante(fab);
			controller.setFabricanteService(new FabricanteService());
			controller.subscribeDataChanceListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Digite os dados dos fabricantes");
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
	
	//adiciona um botão de editar a cada linha da tabela.
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Fabricante, Fabricante>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Fabricante fab, boolean empty) {
				super.updateItem(fab, empty);
				if (fab == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(fab, "/gui/FabricanteForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	//adiciona um botão excluir a cada linha da tabela
	private void initRemoveButtons() { 
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Fabricante, Fabricante>() { 
		 private final Button button = new Button("Excluir"); 
		 @Override
		 protected void updateItem(Fabricante fab, boolean empty) { 
		 super.updateItem(fab, empty); 
		 if (fab == null) { 
		 setGraphic(null); 
		 return; 
		 } 
		 setGraphic(button); 
		 button.setOnAction(event -> removeEntity(fab)); 
		 } 
		 }); 
		}
	
	//Exclui o id da tabela e possui uma janela com um alerta.
	private void removeEntity(Fabricante fab) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza de que deseja excluir?");
	
		if(result.get() == ButtonType.OK) {
			if(service == null) {
					throw new IllegalStateException("O serviço está nulo");
			}
			try {
			service.remover(fab);
			updateTableView();
			
			}
			catch( DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover o objeto", null, e.getMessage(), AlertType.ERROR);
				}
			}
	
	
	} 

}
