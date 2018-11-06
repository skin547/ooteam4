package main.java.model.bean;

import java.io.Serializable;
import java.util.List;

public class Brand implements Serializable {

    private String name;
    private int id;
    private Category category;
    private List<Product> products;
    
		
    /* Getter and Setter */
	public String getName() {
        return name;
    }
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
        return id;
    }
	public void setId(int id) {
		this.id = id;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
    

}
