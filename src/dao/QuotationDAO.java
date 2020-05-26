package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
						Quotation qtn = new Quotation(idQuotation, product, price);
						
						qtn.setOption(opt.getOptions(idProduct));
						
						quotations.add(qtn);
					}
					return quotations;
				}
		}
	}
}
