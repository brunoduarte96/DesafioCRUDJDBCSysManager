package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import gui.listeners.DataChanceListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Fabricante;
import model.exceptions.ValidationException;
import model.service.FabricanteService;

public class FabricanteFormController implements Initializable {

	private Fabricante entity;

	private FabricanteService service;

	private List<DataChanceListener> dataChanceListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtnome;

	@FXML
	private TextField txtpaisOrigem;

	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorPaisNome;

	@FXML
	private Button btCadastrar;
	@FXML
	private Button btCancelar;

	public void setFabricante(Fabricante entity) {
		this.entity = entity;

	}

	public void setFabricanteService(FabricanteService service) {
		this.service = service;
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
	
	//Recupera os dados e válida os campos
	private Fabricante getFormData() {
		Fabricante fab = new Fabricante();

		ValidationException exception = new ValidationException("Erro de validação");

		fab.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtnome.getText() == null || txtnome.getText().trim().equals("")) {
			exception.addError("nome", "O campo não pode ser vazio");
		}
		fab.setNome(txtnome.getText());

		if (txtpaisOrigem.getText() == null || txtpaisOrigem.getText().trim().equals("")) {
			exception.addError("paisOrigem", "O campo não pode ser vazio");
		}

		fab.setPaisOrigem(txtpaisOrigem.getText());

		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return fab;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		InitializeNodes();

	}
	
	//formatação dos campos com sua validações
	private void InitializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtnome, 100);
		Constraints.setTextFieldMaxLength(txtpaisOrigem, 100);
	}
	
	//Atualiza os campos com os dados
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entidade está nula");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtnome.setText(entity.getNome());
		txtpaisOrigem.setText(entity.getPaisOrigem());
	}
	
	//formatação dos campos com sua validações
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		labelErrorName.setText((fields.contains("nome") ? errors.get("nome") : ""));
		labelErrorPaisNome.setText((fields.contains("paisOrigem") ? errors.get("paisOrigem") : ""));

	}

}
