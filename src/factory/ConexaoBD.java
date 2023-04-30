package factory;

import java.sql.Connection;
import java.sql.DriverManager;


public class ConexaoBD {

    private static final String USERNAME = "root";
    private static final String PASSWORD = "bruno2121";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/veiculos";


    public static Connection CreateConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        return conn;
    }

    public static void main(String[] args) throws Exception {
        Connection con = CreateConnection();

        if (con != null) {
            System.out.println("Conexão obetida com sucesso!");
            con.close();
        }


    }

}
