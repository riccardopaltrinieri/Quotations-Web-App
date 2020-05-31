package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Product;

public class ProductDAO {

	private Connection connection;

	public ProductDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Product> getProducts() throws SQLException {

		List<Product> products = new ArrayList<>();
		
		String query = "SELECT * FROM price_quotations_db.products";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					while(result.next()) {
						int id = result.getInt("id");
						int code = result.getInt("code");
						String name = result.getString("name");
						Product prd = new Product(id, code, name);
						products.add(prd);
					}
					return products;
				}
		}
	}
}
