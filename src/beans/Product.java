package beans;

import java.util.List;

public class Product {

	private int id;
	private int productCode;
	private String name;
	private List<Option> options;
	
	


	public Product(int id, int productCode, String name) {
		this.id = id;
		this.productCode = productCode;
		this.name = name;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProductCode() {
		return productCode;
	}
	public void setProductCode(int productCode) {
		this.productCode = productCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Option> getOptions() {
		return options;
	}
	public void setOptions(List<Option> options) {
		this.options = options;
	}
}
