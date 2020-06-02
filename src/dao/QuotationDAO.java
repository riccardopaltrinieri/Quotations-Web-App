package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import beans.Option;
import beans.Product;
import beans.Quotation;
import beans.User;

public class QuotationDAO {

	private Connection connection;

	public QuotationDAO(Connection connection) {
		this.connection = connection;
	}

	public Quotation getQuotation(int idQuotation) throws SQLException {

		OptionDAO opt = new OptionDAO(connection);
		
		String query = "SELECT c.username, p.id, p.name, p.code "
					 + "FROM price_quotations_db.quotations as q JOIN price_quotations_db.products as p "
					 	+ "JOIN price_quotations_db.users as c "
				 	 + "WHERE q.id = ? AND q.id_customer = c.id AND q.id_product = p.id";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
			
			pstatement.setInt(1, idQuotation);
			pstatement.execute();

			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {
					String customer = result.getString("c.username");
					int idProduct = result.getInt("p.id");
					int productCode = result.getInt("p.code");
					String productName = result.getString("p.name");
					Product product = new Product(idProduct, productCode, productName);
					product.setOptions(opt.getQuotationOptions(idQuotation, idProduct));
					
					return(new Quotation(idQuotation, customer, null, product));
				}
			}
		}
		return null;
	}

	public List<Quotation> getCustomerQuotations(User user) throws SQLException {

		List<Quotation> quotations = new ArrayList<>();
		OptionDAO opt = new OptionDAO(connection);
		
		String query = "SELECT q.id, u.username, q.price, p.id, p.name, p.code "
					 + "FROM price_quotations_db.quotations as q JOIN price_quotations_db.products as p "
					 + "JOIN price_quotations_db.users as u "
					 + "ON q.id_product = p.id AND q.id_employee = u.id "
					 + "WHERE q.id_customer = ? AND q.price <> 0";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
				pstatement.setInt(1, user.getId());
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					while(result.next()) {
						String employee = result.getString("u.username");
						int idQuotation = result.getInt("q.id");
						float price = result.getFloat("q.price");
						int idProduct = result.getInt("p.id");
						int productCode = result.getInt("p.code");
						String productName = result.getString("p.name");
						Product product = new Product(idProduct, productCode, productName);
						product.setOptions(opt.getQuotationOptions(idQuotation, idProduct));
						
						Quotation qtn = new Quotation(idQuotation, product, price);
						qtn.setEmployee(employee);
						quotations.add(qtn);
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

	public List<Quotation> getEmployeeQuotations(User user) throws SQLException {

		List<Quotation> quotations = new ArrayList<>();
		OptionDAO opt = new OptionDAO(connection);
		
		String query = "SELECT q.id, u.username, q.price, p.id, p.name, p.code "
					 + "FROM price_quotations_db.quotations as q JOIN price_quotations_db.products as p "
					 + "JOIN price_quotations_db.users as u "
					 + "ON q.id_product = p.id AND q.id_customer = u.id "
					 + "WHERE q.id_employee = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
				pstatement.setInt(1, user.getId());
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					while(result.next()) {
						String customer = result.getString("u.username");
						int idQuotation = result.getInt("q.id");
						float price = result.getFloat("q.price");
						int idProduct = result.getInt("p.id");
						int productCode = result.getInt("p.code");
						String productName = result.getString("p.name");
						Product product = new Product(idProduct, productCode, productName);
						product.setOptions(opt.getQuotationOptions(idQuotation, idProduct));
						Quotation qtn = new Quotation(idQuotation, product, price);
						qtn.setCustomer(customer);
						
						quotations.add(qtn);
					}
					return quotations;
				}
		}
	}

	public List<Quotation> getPricelessQuotations() throws SQLException {

		List<Quotation> quotations = new ArrayList<>();
		OptionDAO opt = new OptionDAO(connection);
		
		String query = "SELECT q.id, q.price, p.id, p.name, p.code "
					 + "FROM price_quotations_db.quotations as q JOIN price_quotations_db.products as p "
					 + "ON q.id_product = p.id "
					 + "WHERE q.price = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
				pstatement.setInt(1, 0);
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					while(result.next()) {
						int idQuotation = result.getInt("q.id");
						int idProduct = result.getInt("p.id");
						int productCode = result.getInt("p.code");
						String productName = result.getString("p.name");
						Product product = new Product(idProduct, productCode, productName);
						product.setOptions(opt.getQuotationOptions(idQuotation, idProduct));
						
						quotations.add(new Quotation(idQuotation, product, 0));
					}
					return quotations;
				}
		}
	}

	public void priceQuotation(User user, int idQuotation, float price) throws SQLException {
		
		String query = "UPDATE price_quotations_db.quotations "
					 + "SET price = ?, id_employee = ? "
					 + "WHERE id = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setFloat(1, price);
			pstatement.setInt(2, user.getId());
			pstatement.setInt(3, idQuotation);
			pstatement.executeUpdate();
		}
	}


	public String getCustomerQuotationsJson(User user) throws SQLException {
		
		JsonArray finalJson = new JsonArray();
		OptionDAO opt = new OptionDAO(connection);
		
		String query = "SELECT q.id, u.username, q.price, p.id, p.name, p.code "
					 + "FROM price_quotations_db.quotations as q JOIN price_quotations_db.products as p "
					 + "JOIN price_quotations_db.users as u "
					 + "ON q.id_product = p.id AND q.id_employee = u.id "
					 + "WHERE q.id_customer = ? AND q.price <> 0";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
			pstatement.setInt(1, user.getId());
			pstatement.execute();

			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					JsonObject quotationJson = new JsonObject();
					String employee = result.getString("u.username");
					int idQuotation = result.getInt("q.id");
					float price = result.getFloat("q.price");
					int idProduct = result.getInt("p.id");
					int productCode = result.getInt("p.code");
					String productName = result.getString("p.name");
					Product product = new Product(idProduct, productCode, productName);
					product.setOptions(opt.getQuotationOptions(idQuotation, idProduct));
					StringBuilder options = new StringBuilder();
					for(Option op : product.getOptions()) {
						options.append(op.getName() + " ");
					}
					
					quotationJson.addProperty("Product", productName);
					quotationJson.addProperty("Code", productCode);
					quotationJson.addProperty("Options", options.toString());
					quotationJson.addProperty("Employee", employee);
					quotationJson.addProperty("Price", price);
					finalJson.add(quotationJson);
				}
			}
			return finalJson.toString();
		}
	}

	public String getPendingRequests(User user) throws SQLException {
		
		String query = "SELECT count(*) "
					 + "FROM price_quotations_db.quotations as q "
					 + "WHERE q.id_customer = ? AND q.price = 0";
	
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
			pstatement.setInt(1, user.getId());
			pstatement.execute();
	
			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {
					return String.valueOf(result.getInt("count(*)"));
				} else {
					throw new SQLException("Error in finding pending requests");
				}
			}
		}
	}
	
}
