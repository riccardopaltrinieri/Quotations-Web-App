package beans;

public class Quotation {

	private int id;
	private String customer;
	private String employee;
	private Product product;
	private float price;
	
	public Quotation(int id, String customer, String employee, Product product) {
		this.setId(id);
		this.setCustomer(customer);
		this.setEmployee(employee);
		this.setProduct(product);
	}

	public Quotation(int idQuotation, Product product, float price) {
		this.setId(idQuotation);
		this.setProduct(product);
		this.setPrice(price);
	}
	
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
