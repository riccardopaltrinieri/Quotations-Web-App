package beans;

public class User {

	private int id;
	private String role;
	private String username;
	
	
	public User(int id, String role, String name) {
		this.id = id;
		this.setRole(role);
		this.username = name;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return username;
	}
	public void setName(String name) {
		this.username = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

}
