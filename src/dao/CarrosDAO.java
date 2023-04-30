package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import factory.ConexaoBD;
import model.Carros;
import model.Fabricante;



public class CarrosDAO {

    private Connection connection;

    public CarrosDAO() throws Exception {
        this.connection = ConexaoBD.CreateConnection();
    }


    public void salvar(Carros carros) {
     
        String sql = "INSERT INTO carros(marca, modelo, ano,preco, id_fabricante)VALUES(?,?,?,?,?)";


        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, carros.getMarca());
            pstmt.setString(2, carros.getModelo());
            pstmt.setInt(3, carros.getAno());
            pstmt.setDouble(4, carros.getPreco());
            pstmt.setInt(5, carros.getFabricante().getId());

            pstmt.execute();
            pstmt.close();
            System.out.println("Carro salvo com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao salvar o carro: " + e.getMessage());
        }


    }

    
    public void atualizar(Carros carros) {
        

        String sql = "UPDATE carros SET marca = ?, modelo = ?, ano = ?, preco= ?, id_fabricante = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, carros.getMarca());
            pstmt.setString(2, carros.getModelo());
            pstmt.setInt(3, carros.getAno());
            pstmt.setDouble(4, carros.getPreco());
            pstmt.setInt(5, carros.getFabricante().getId());
            pstmt.setInt(6, carros.getId());

            pstmt.execute();
            pstmt.close();
            System.out.println("Carro atualizado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar o carro: " + e.getMessage());

        }
    }

    
    public void excluir(Integer id) {
        String sql = "DELETE FROM carros WHERE id = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);

           int rows = pstmt.executeUpdate();

            if (rows == 0) {
                System.out.println("ID não encontrado!");

            }
            else {
                System.out.println("Carro excluído com sucesso!");
            }




            pstmt.close();


        } catch (SQLException e) {
            System.out.println("Erro ao excluir o carro: " + e.getMessage());
        }

    }


    private Carros instantiateCarros(ResultSet rs, Fabricante fab) throws SQLException {
        Carros carros = new Carros();
        carros.setId(rs.getInt("id"));
        carros.setMarca(rs.getString("marca"));
        carros.setModelo(rs.getString("modelo"));
        carros.setAno(rs.getInt("ano"));
        carros.setPreco(rs.getDouble("preco"));
        carros.setFabricante(fab);

        return carros;
    }

    private Fabricante instantiateFabricante(ResultSet rs) throws SQLException {
        Fabricante fab = new Fabricante();
        fab.setId(rs.getInt("id_fabricante"));
        fab.setNome(rs.getString("nome"));
        fab.setPaisOrigem(rs.getString("pais_origem"));
        return fab;

    }

    public List<Carros> listarCarrosPorFabricante(Fabricante fabricante) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(
                    "SELECT * FROM carros c INNER JOIN fabricante f ON c.id_fabricante = f.id WHERE f.id =?"

            );

            pstmt.setInt(1, fabricante.getId());
            rs = pstmt.executeQuery();

            List<Carros> list = new ArrayList<>();
            Map<Integer, Fabricante> map = new HashMap<>();

            while (rs.next()) {
                Fabricante fab = map.get(rs.getInt("id_fabricante"));
                if (fab == null) {
                    fab = instantiateFabricante(rs);
                    map.put(rs.getInt("id_fabricante"), fab);
                }
                Carros vei = instantiateCarros(rs, fab);
                list.add(vei);
            }
            System.out.println("Lista carregada com sucesso!");
            return list;


        } catch (Exception e) {
            System.out.println("Ocorreu um erro no carregamento da lista!");
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

  
    public Carros buscarPorId(Integer id) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM carros c INNER JOIN fabricante f ON c.id_fabricante = f.id WHERE c.id = ?";

        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();


            if (rs.next()) {

                Fabricante fab = instantiateFabricante(rs);
                Carros vei = instantiateCarros(rs,fab);
                return vei;

            } else {
                System.out.println("Nenhum carro encontrado com o ID informado.");
            }
            return null;

        } catch (SQLException e) {
            System.out.println("Erro ao buscar o carro por ID: " + e.getMessage());
            return null;
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
    }

    public List<Carros> listar() {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM carros c INNER JOIN fabricante f ON c.id_fabricante = f.id");
            rs = pstmt.executeQuery();

            List<Carros> list = new ArrayList<>();
            Map<Integer, Fabricante> map = new HashMap<>();

            while (rs.next()) {
                Fabricante fab = map.get(rs.getInt("id_fabricante"));
                if (fab == null) {
                    fab = instantiateFabricante(rs);
                    map.put(rs.getInt("id_fabricante"), fab);
                }
                Carros vei = instantiateCarros(rs, fab);
                list.add(vei);
            }
            System.out.println("Lista carregada com sucesso!");
            return list;
        } catch (Exception e) {
            System.out.println("Ocorreu um erro no carregamento da lista!");
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

