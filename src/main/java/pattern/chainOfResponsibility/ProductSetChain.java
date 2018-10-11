package main.java.pattern.chainOfResponsibility;
import java.util.ArrayList;
import java.util.List;

import main.java.model.bean.OrderItem;
import main.java.model.bean.Promotion;
import main.java.model.bean.PromotionItem;
import main.java.model.dao.PromotionDAO;
import main.java.model.dao.PromotionItemDAO;
import main.java.pattern.strategy.StrategyContext;
import main.java.pattern.strategy.EachGroupOfNStrategy;
import main.java.pattern.strategy.ProductSetStrategy;
import main.java.pattern.strategy.Strategy;

/**
 * Chain Of Responsibility Pattern - concrete handler
 */
public class ProductSetChain extends DiscountPolicy {

	@Override
	public DiscountRequest handleDiscount(DiscountRequest discountRequest) {
		List<OrderItem> orderItems = discountRequest.getOrderItems();
		
		List<OrderItem> discountOrderItems = new ArrayList<OrderItem>();
		List<OrderItem> otherOrderItems = new ArrayList<OrderItem>();
		
		PromotionItemDAO promotionItemDAO = new PromotionItemDAO();
		PromotionItem promotionItem;
		
		PromotionDAO promotionDAO = new PromotionDAO();
		Promotion promotion;
		
	    // Collect orderItem that discountType equals strategyDiscountType into discountOrderItems. 
		// Else put into otherOrderItems
	    int strategyDiscountType = PromotionDAO.productSet;
	    for (OrderItem oi : orderItems) {
	    	promotionItem = promotionItemDAO.getByProduct(oi.getProduct().getId());
	    	promotion = promotionDAO.get(promotionItem.getPromotion().getId());
	    	
	    	if (promotion.getDiscountType() == (strategyDiscountType)) {
		    	discountOrderItems.add(oi);
		    } else {
		    	otherOrderItems.add(oi);
		    }
	    }
	    
	    // Prepare data with discountOrderItems for Strategy Pattern
	    discountRequest.setOrderItems(discountOrderItems);
	    

	    // Use Strategy Pattern
	    Strategy strategy = new ProductSetStrategy();
	    if (!discountOrderItems.isEmpty()) {
	    	StrategyContext cc = new StrategyContext(strategy);
		    discountRequest = cc.ExecuteStrategy(discountRequest);
	    }
	    
	    
	    // If otherOrderItems are not empty, pass request to the next chain
	    if (!otherOrderItems.isEmpty()) {
	    	discountRequest.setOrderItems(otherOrderItems);
		    if (nextDiscountPolicy != null) {
				nextDiscountPolicy.handleDiscount(discountRequest);
			}	
	    }
	    
	    return discountRequest;
	}

}
