package beans;

public class Quotation {

	private int idCustomer;
	private int idEmployee;
	private Product product;
	private float price;
	
	public Quotation(int idCustomer, int idEmployee, Product product) {
		this.idCustomer = idCustomer;
		this.idEmployee = idEmployee;
		this.product = product;
	}

	public Quotation(int idCustomer, Product product, float price) {
		this.idCustomer = idCustomer;
		this.product = product;
		this.price = price;
	}
	
	public int getIdCustomer() {
		return idCustomer;
	}
	public void setIdCustomer(int idCustomer) {
		this.idCustomer = idCustomer;
	}
	public int getIdEmployee() {
		return idEmployee;
	}
	public void setIdEmployee(int idEmployee) {
		this.idEmployee = idEmployee;
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
}
