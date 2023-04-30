package dao;

import model.Fabricante;
import model.Veiculo;

import java.util.List;


public interface VeiculoDAO {
    void salvar(Veiculo veiculo);
    void atualizar(Veiculo veiculo);
    void excluir(Integer id);

    Veiculo buscarPorId (Integer id);

    List<Veiculo> listarCarrosPorFabricante(Fabricante fabricante);


}
