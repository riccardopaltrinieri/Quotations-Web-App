package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Option;
import beans.Product;
import beans.Quotation;
import beans.User;

public class QuotationDAO {

	private Connection connection;

	public QuotationDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Quotation> getCustomerQuotations(User user) throws SQLException {

		List<Quotation> quotations = new ArrayList<>();
		OptionDAO opt = new OptionDAO(connection);
		
		String query = "SELECT q.id, q.price, p.id, p.name, p.code "
					 + "FROM price_quotations_db.quotations as q JOIN price_quotations_db.products as p "
					 + "ON q.id_product = p.id "
					 + "WHERE q.id_customer = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
				pstatement.setInt(1, user.getId());
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					while(result.next()) {
						int idQuotation = result.getInt("q.id");
						float price = result.getFloat("q.price");
						int idProduct = result.getInt("p.id");
						int productCode = result.getInt("p.code");
						String productName = result.getString("p.name");
						Product product = new Product(idProduct, productCode, productName);
						product.setOptions(opt.getQuotationOptions(idQuotation, idProduct));
						
						quotations.add(new Quotation(idQuotation, product, price));
					}
					return quotations;
				}
		}
	}

	public void addQuotation(User user, int idProduct, List<Option> optionsChecked) throws SQLException {

		String query = "INSERT INTO price_quotations_db.quotations (id_product, id_customer) VALUES (?, ?);";
		int idQuotation;
		
		try (PreparedStatement pstatement = connection.prepareStatement(query, 1);) {

			pstatement.setInt(1, idProduct);
			pstatement.setInt(2, user.getId());
			pstatement.executeUpdate();
			
			try (ResultSet result = pstatement.getGeneratedKeys()) {
				if(result.next()) idQuotation = result.getInt(1);
				else throw new SQLException("Generated key for price quotation not returned");
			}
			
		}
		
		query = "INSERT INTO price_quotations_db.requested_options (id_product, id_option, id_quotation) VALUES (?, ?, ?);";
		
		for (Option option : optionsChecked) {
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
	
				pstatement.setInt(1, idProduct);
				pstatement.setInt(2, option.getId());
				pstatement.setInt(3, idQuotation);
				pstatement.executeUpdate();
			}
		}
	}
}
