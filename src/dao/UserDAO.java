package dao;

import beans.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;


/**
 * An Object that can be used by the user to extract all the info about 
 * {@link User} from a database. It uses the connection passed in the 
 * constructor.
 */
public class UserDAO {
	private Connection con;

	/**
	 * Construct the DAO connecting it to the database saving the parameter connection.
	 * @param connection to a specific database
	 */
	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	/**
	 * Create a new user in the database with all the info passed as parameters
	 * @param username
	 * @param password
	 * @param role
	 * @throws SQLException
	 */
	public void createNewUser(String username, String password, String role) throws SQLException {

		String query = "INSERT INTO price_quotations_db.users (username, password, role) VALUES (?, ?, ?);";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			pstatement.setString(1, username);
			pstatement.setString(2, password);
			pstatement.setString(3, role);
			pstatement.executeUpdate();
		}
	}
	
	/**
	 * Check if the user with this username (unique in the database) has a password that coincide 
	 * with pwd used by the User
	 * @param username inserted by the user in the login
	 * @param pwd password inserted by the user in the login
	 * @return the {@link User} object with all the info about the user
	 * @throws SQLException
	 */
	public User checkCredentials(String username, String pwd) throws SQLException {
		
		String query = "SELECT id, role FROM price_quotations_db.users WHERE username = ? AND password = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					throw new SQLException("User '" + username + "' not found");
				else {
					result.next();
					int id = result.getInt("id");
					String role = result.getString("role");
					User user = new User(id, username, role);
					return user;
				}
			}
		}
	}
}
