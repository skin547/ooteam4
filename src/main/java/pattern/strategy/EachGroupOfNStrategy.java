package main.java.pattern.strategy;

import java.util.List;

import main.java.model.bean.OrderItem;
import main.java.model.bean.Promotion;
import main.java.model.bean.PromotionItem;
import main.java.model.dao.PromotionDAO;
import main.java.model.dao.PromotionItemDAO;
import main.java.pattern.chainOfResponsibility.DiscountRequest;

/**
 * Strategy Pattern - concrete strategy
 */
public class EachGroupOfNStrategy implements Strategy {

	public DiscountRequest Execute(DiscountRequest discountRequest) {
		List<OrderItem> orderItems = discountRequest.getOrderItems();

		PromotionDAO promotionDAO = new PromotionDAO();
		Promotion promotion;
		
		PromotionItemDAO promotionItemDAO = new PromotionItemDAO();
		PromotionItem promotionItem;
		
		// EachGroupOfNStrategy Algorithm
		// e.g. If a customer buys 100 units of Product X, he gets a discount of 15%.
		float discountAmount = 0;
		for (OrderItem oi : orderItems) {
			promotionItem = promotionItemDAO.getByProduct(oi.getProduct().getId());
			promotion = promotionDAO.get(promotionItem.getPromotion().getId());
			
			if (promotion.getState() == 1 && oi.getQuantity() >= promotionItem.getMinQuantity()) {
				discountAmount += oi.getQuantity() * oi.getProduct().getPrice() * ((float)promotionItem.getDiscountOf() / 100);
				discountAmount = Math.round(discountAmount);
			}
		}
		
		discountRequest.setTotalDiscount(discountRequest.getTotalDiscount() + discountAmount);
		discountRequest.setDiscountMsg(discountRequest.getDiscountMsg() + " EachGroupOfN Discount: -" + discountAmount + " ");
		return discountRequest;
	}

}
