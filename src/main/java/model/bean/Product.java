package main.java.model.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.java.pattern.observer.Observer;
import main.java.pattern.observer.Subject;

/**
 * 
 * Observer Pattern - concrete subject
 *
 */
public class Product implements Subject, Serializable {
    
	private int id;
	private String name;
	private float price;
	private Date dateAdded;
	private Brand brand;
	private ProductImage firstProductImage;
	private List<ProductImage> productImages;
	private List<ProductImage> productSingleImages;
	private int discountType = -1;
	private String discountTypeName;
	private String promotionName;
	
	private int inventory;
	private ArrayList<Observer> observers = new ArrayList<>();
	
	/* Observer Pattern */
	@Override
	public void addObserver(Observer o) {
		observers.add(o);
	}
	
	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}
	
	@Override
	public void notifyObservers() {
		for(Observer o : observers) {
			o.update(this);
		}
	}
	
	public void setInventory(int inventory) {
		if (this.inventory == 0 && inventory > 0) {
			this.inventory = inventory;
			notifyObservers();
		} else {
			this.inventory = inventory;
		}
	}
	
	
	/* Getter and Setter */
	public int getId() {
        return id;
    }
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
        return name;
    }
	public void setName(String name) {
		this.name = name;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getInventory() {
		return inventory;
	}
	
	public Date getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	public Brand getBrand() {
		return brand;
	}
	public void setBrand(Brand brand) {
		this.brand = brand;
	}
	public ProductImage getFirstProductImage() {
		return firstProductImage;
	}
	public void setFirstProductImage(ProductImage firstProductImage) {
		this.firstProductImage = firstProductImage;
	}
	public List<ProductImage> getProductImages() {
		return productImages;
	}
	public void setProductImages(List<ProductImage> productImages) {
		this.productImages = productImages;
	}
	public List<ProductImage> getProductSingleImages() {
		return productSingleImages;
	}
	public void setProductSingleImages(List<ProductImage> productSingleImages) {
		this.productSingleImages = productSingleImages;
	}
	public int getDiscountType() {
		return discountType;
	}
	public void setDiscountType(int discountType) {
		this.discountType = discountType;
	}
	public String getDiscountTypeName() {
		return discountTypeName;
	}
	public void setDiscountTypeName(String discountTypeName) {
		this.discountTypeName = discountTypeName;
	}
	public String getPromotionName() {
		return promotionName;
	}
	public void setPromotionName(String promotionName) {
		this.promotionName = promotionName;
	}
}
