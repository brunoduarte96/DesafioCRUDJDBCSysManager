package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import gui.listeners.DataChanceListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.Carros;
import model.Fabricante;
import model.exceptions.ValidationException;
import model.service.CarrosService;
import model.service.FabricanteService;

public class CarrosFormController implements Initializable {

	private Carros entity;

	private CarrosService service;

	private FabricanteService fabricanteService;

	private List<DataChanceListener> dataChanceListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtMarca;

	@FXML
	private TextField txtModelo;

	@FXML
	private TextField txtAno;

	@FXML
	private TextField txtPreco;

	@FXML
	private ComboBox<Fabricante> comboBoxFabricante;

	@FXML
	private Label labelErrorMarca;

	@FXML
	private Label labelErrorModelo;

	@FXML
	private Label labelErrorAno;

	@FXML
	private Label labelErrorPreco;

	@FXML
	private Button btCadastrar;
	@FXML
	private Button btCancelar;

	private ObservableList<Fabricante> obsList;

	public void setCarros(Carros entity) {
		this.entity = entity;

	}

	public void setServices(CarrosService service, FabricanteService fabricanteservice) {
		this.service = service;
		this.fabricanteService = fabricanteservice;
	}

	public void subscribeDataChanceListener(DataChanceListener listener) {
		dataChanceListeners.add(listener);
	}

	@FXML
	public void onBtCadastrarAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entidade está nula");
		}
		if (service == null) {
			throw new IllegalStateException("O serviço está nulo");
		}
		try {
			entity = getFormData();
			service.salvarOuAtualizar(entity);
			notifyDataChanceListener();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErros());
		} catch (Exception e) {
			Alerts.showAlert("Erro ao salvar o objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChanceListener() {
		for (DataChanceListener listener : dataChanceListeners) {
			listener.onDataChanged();
		}

	}

	private Carros getFormData() {
		Carros car = new Carros();

		ValidationException exception = new ValidationException("Erro de validação");

		car.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtMarca.getText() == null || txtMarca.getText().trim().equals("")) {
			exception.addError("marca", "O campo não pode ser vazio");
		}
		car.setMarca(txtMarca.getText());

		if (txtModelo.getText() == null || txtModelo.getText().trim().equals("")) {
			exception.addError("modelo", "O campo não pode ser vazio");
		}
		car.setModelo(txtModelo.getText());

		if (txtAno.getText() == null || txtAno.getText().trim().equals("")) {
			exception.addError("ano", "O campo não pode ser vazio");
		}
		car.setAno(Utils.tryParseToInt(txtAno.getText()));

		if (txtPreco.getText() == null || txtPreco.getText().trim().equals("")) {
			exception.addError("preco", "O campo não pode ser vazio");
		}

		car.setPreco(Utils.tryParseToDouble(txtPreco.getText()));
		
		car.setFabricante(comboBoxFabricante.getValue());

		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return car;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		InitializeNodes();

	}

	private void InitializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtMarca, 60);
		Constraints.setTextFieldMaxLength(txtModelo, 60);
		Constraints.setTextFieldInteger(txtAno);
		Constraints.setTextFieldDouble(txtPreco);

		initializeComboBoxFabricante();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entidade está nula");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtMarca.setText(entity.getMarca());
		txtModelo.setText(entity.getModelo());
		txtAno.setText(String.valueOf(entity.getAno()));
		Locale.setDefault(Locale.US);
		txtPreco.setText(String.format("%.2f", entity.getPreco()));

		if (entity.getFabricante() == null) {
			comboBoxFabricante.getSelectionModel().selectFirst();
		} else {
			comboBoxFabricante.setValue(entity.getFabricante());
		}

	}

	public void loadAssociatedObjects() {
		if (fabricanteService == null) {
			throw new IllegalStateException("Fabricante está nulo");
		}
		List<Fabricante> list = fabricanteService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxFabricante.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		labelErrorMarca.setText((fields.contains("marca") ? errors.get("marca") : ""));
		labelErrorModelo.setText((fields.contains("modelo") ? errors.get("modelo") : ""));
		labelErrorAno.setText((fields.contains("ano") ? errors.get("ano") : ""));
		labelErrorPreco.setText((fields.contains("preco") ? errors.get("preco") : ""));

	}

	private void initializeComboBoxFabricante() {
		Callback<ListView<Fabricante>, ListCell<Fabricante>> factory = lv -> new ListCell<Fabricante>() {
			@Override
			protected void updateItem(Fabricante item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxFabricante.setCellFactory(factory);
		comboBoxFabricante.setButtonCell(factory.call(null));
	}

}
