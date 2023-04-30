package model.service;

import java.util.List;

import dao.FabricanteDAO;
import model.Fabricante;

public class FabricanteService {

	private FabricanteDAO fabricanteDAO;
	Fabricante fabricante = new Fabricante();

	public FabricanteService() throws Exception {
		fabricanteDAO = new FabricanteDAO();
	}

	public List<Fabricante> findAll() {
		return fabricanteDAO.listar();
	}

	public void salvarOuAtualizar(Fabricante fab) {
		if (fab.getId() == null) {
			fabricanteDAO.salvar(fab);
		} else {
			fabricanteDAO.atualizar(fab);

		}

	}
	
	public void remover(Fabricante fab) {
		fabricanteDAO.excluir(fab.getId());
	}
	
}
