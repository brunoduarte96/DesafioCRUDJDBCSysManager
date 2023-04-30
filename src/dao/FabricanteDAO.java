package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import factory.ConexaoBD;
import model.Fabricante;
import model.exceptions.DbIntegrityException;

public class FabricanteDAO {
    private Connection connection;
    
    
    
    

    public FabricanteDAO() throws Exception {
        this.connection = ConexaoBD.CreateConnection();
    }

    public int salvar(Fabricante fabricante) {
        String sql = "INSERT INTO fabricante (nome, pais_origem) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, fabricante.getNome());
            pstmt.setString(2, fabricante.getPaisOrigem());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                fabricante.setId(id);
                System.out.println("Fabricante salvo com sucesso!");
                return id;
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Erro ao salvar o fabricante: " + e.getMessage());
        }
        return -1;
    }


    public void atualizar(Fabricante fabricante) {


        String sql = "UPDATE fabricante SET nome = ?, pais_origem = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, fabricante.getNome());
            pstmt.setString(2, fabricante.getPaisOrigem());
            pstmt.setInt(3, fabricante.getId());


             pstmt.execute();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar fabricante: " + e.getMessage());

        }
    }

    public void excluir(Integer id) {


        String sql = "DELETE FROM fabricante WHERE id = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1,id);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                System.out.println("ID não encontrado!");

            }
            else {
                System.out.println("Fabricante excluído com sucesso!");
            }
            pstmt.close();

        } catch (SQLException e) {
        	throw new DbIntegrityException(e.getMessage());        }
    }

    public List<Fabricante> listar() {
        List<Fabricante> fabricantes = new ArrayList<>();
        String sql = "SELECT * FROM fabricante";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                int id = result.getInt("id");
                String nome = result.getString("nome");
                String paisOrigem = result.getString("pais_origem");

                Fabricante fabricante = new Fabricante();
                fabricante.setId(id);
                fabricante.setNome(nome);
                fabricante.setPaisOrigem(paisOrigem);

                fabricantes.add(fabricante);
            }

            pstmt.close();
            result.close();
        } catch (SQLException e) {
            System.out.println("Erro ao listar os fabricantes: " + e.getMessage());
        }

        return fabricantes;
    }

    public Fabricante buscarPorID(int id) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM fabricante where id = ?");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Fabricante fabricante = new Fabricante();
                fabricante.setId(rs.getInt("id"));
                fabricante.setNome(rs.getString("nome"));
                fabricante.setPaisOrigem(rs.getString("pais_origem"));
                return fabricante;
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar fabricante por id " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                System.out.println("Erro ao fechar statement: " + e.getMessage());
            }
        }
        return null;
    }
}
