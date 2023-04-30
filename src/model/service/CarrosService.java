package model.service;

import java.util.List;

import dao.CarrosDAO;
import model.Carros;

public class CarrosService {

	private CarrosDAO carrosDAO;
	Carros carros = new Carros();

	public CarrosService() throws Exception {
		carrosDAO = new CarrosDAO();
	}

	public List<Carros> findAll() {
		return carrosDAO.listar();
	}

	public void salvarOuAtualizar(Carros fab) {
		if (fab.getId() == null) {
			carrosDAO.salvar(fab);
		} else {
			carrosDAO.atualizar(fab);

		}

	}
	
	public void remover(Carros car) {
	    carrosDAO.excluir(car.getId());
	}
	
}
