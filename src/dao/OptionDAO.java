package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import beans.Option;

public class OptionDAO {

	private Connection connection;

	public OptionDAO(Connection connection) {
		this.connection = connection;
	}
	
	
	public List<Option> getQuotationOptions(int idQuotation, int idProduct) throws SQLException {
		
		List<Option> options = new ArrayList<>();
		
		String query = "SELECT o.id, o.optionCode, o.name, o.type "
					 + "FROM price_quotations_db.options as o JOIN price_quotations_db.requested_options as ro "
					 + "ON ro.id_option = o.id "
					 + "WHERE ro.id_product = ? AND ro.id_quotation = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
				pstatement.setInt(1, idProduct);
				pstatement.setInt(2, idQuotation);
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					while(result.next()) {
						int id = result.getInt("o.id");
						int optionCode = result.getInt("o.optionCode");
						String name = result.getString("o.name");
						String type = result.getString("o.type");
						Option opt = new Option(id, optionCode, type, name);
						options.add(opt);
					}
					return options;
				}
		}
	}


	public List<Option> getOptions(int idProduct) throws SQLException {		
		List<Option> options = new ArrayList<>();
	
		String query = "SELECT o.id, o.optionCode, o.name, o.type "
					 + "FROM price_quotations_db.options as o JOIN price_quotations_db.product_options as ro "
					 + "ON ro.id_option = o.id "
					 + "WHERE ro.id_product = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
				pstatement.setInt(1, idProduct);
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					while(result.next()) {
						int id = result.getInt("o.id");
						int optionCode = result.getInt("o.optionCode");
						String name = result.getString("o.name");
						String type = result.getString("o.type");
						Option opt = new Option(id, optionCode, type, name);
						options.add(opt);
					}
					return options;
				}
		}
	}


	public String getOptionsJson(int idProduct) throws SQLException {
		
		Gson gson = new Gson();
		List<Option> options = new ArrayList<>();
		
		String query = "SELECT o.id, o.optionCode, o.name, o.type "
					 + "FROM price_quotations_db.options as o JOIN price_quotations_db.product_options as ro "
					 + "ON ro.id_option = o.id "
					 + "WHERE ro.id_product = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query); ) {
				
				pstatement.setInt(1, idProduct);
				pstatement.execute();
	
				try (ResultSet result = pstatement.executeQuery();) {
					while(result.next()) {
						int id = result.getInt("o.id");
						int optionCode = result.getInt("o.optionCode");
						String name = result.getString("o.name");
						String type = result.getString("o.type");
						Option opt = new Option(id, optionCode, type, name);
						options.add(opt);
					}
					return gson.toJson(options);
				}
		}
	}

}
