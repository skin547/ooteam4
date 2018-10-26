package main.java.controller.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.web.util.HtmlUtils;

import main.java.model.bean.*;
import main.java.model.dao.*;
import main.java.model.util.Page;
import main.java.pattern.chainOfResponsibility.BuyXGetYFreePolicy;
import main.java.pattern.chainOfResponsibility.DiscountPolicy;
import main.java.pattern.chainOfResponsibility.DiscountRequest;
import main.java.pattern.chainOfResponsibility.EachGroupOfNPolicy;
import main.java.pattern.chainOfResponsibility.NoDiscountPolicy;
import main.java.pattern.chainOfResponsibility.ProductSetPolicy;
import main.java.pattern.facade.ClearOrderFacade;
import main.java.pattern.memento.Caretaker;
import main.java.pattern.memento.Memento;
import main.java.pattern.memento.ShoppingCart;
import main.java.pattern.chainOfResponsibility.BroughtMoreThanInLastYearPolicy;

@WebServlet("/foreServlet")
public class ForeServlet extends BaseForeServlet {

    public String home(HttpServletRequest request, HttpServletResponse response, Page page) {
    	List<Segment> segments = segmentDAO.list();
    	new CategoryDAO().fill(segments);
    	new CategoryDAO().fillByRow(segments);
    	
        List<Brand> brands = brandDAO.list();
        new ProductDAO().fill(brands);
        
        for (Brand c : brands) {
          for (Product p : c.getProducts()) {
            PromotionItem promotionItem = promotionItemDAO.getByProduct(p.getId()); 
            Promotion promotionByProduct = promotionItem.getPromotion();
            if (promotionByProduct != null && !(promotionItem.getDiscountOf() == 100 && promotionByProduct.getDiscountType() == PromotionDAO.buyXGetYFree)) {
              String promotionName = "";
              if (promotionByProduct.getState() == 1) {
                promotionName = promotionByProduct.getName();
              }
                String discountTypeName = promotionByProduct.getDiscountTypeDescription();
                p.setPromotionName(promotionName);
                p.setDiscountTypeName(discountTypeName);
            }
          }
        }
        
        request.setAttribute("segments", segments);
        request.setAttribute("brands", brands);
        return "home.jsp";
    }
    
    public String category(HttpServletRequest request, HttpServletResponse response, Page page) {
    	int cid = Integer.parseInt(request.getParameter("cid"));
    	
    	List<Segment> segments = segmentDAO.list();
    	new CategoryDAO().fill(segments);
    	new CategoryDAO().fillByRow(segments);
    	
        List<Brand> brands = brandDAO.list(cid);
        new ProductDAO().fill(brands);
        
        for (Brand c : brands) {
          for (Product p : c.getProducts()) {
            PromotionItem promotionItem = promotionItemDAO.getByProduct(p.getId()); 
            Promotion promotionByProduct = promotionItem.getPromotion();
            if (promotionByProduct != null && !(promotionItem.getDiscountOf() == 100 && promotionByProduct.getDiscountType() == PromotionDAO.buyXGetYFree)) {
              String promotionName = "";
              if (promotionByProduct.getState() == 1) {
                promotionName = promotionByProduct.getName();
              }
                String discountTypeName = promotionByProduct.getDiscountTypeDescription();
                p.setPromotionName(promotionName);
                p.setDiscountTypeName(discountTypeName);
            }
          }
        }
        
        request.setAttribute("segments", segments);
        request.setAttribute("brands", brands);
        return "category.jsp";
    }

    public String register(HttpServletRequest request, HttpServletResponse response, Page page) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        email = HtmlUtils.htmlEscape(email);
        boolean exist = userDAO.isExist(email);

        if (exist) {
            request.setAttribute("msg", "Email has been used!!!");
            return "%fail";
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        userDAO.add(user);
        User newUser = userDAO.get(email, password);

        request.getSession().setAttribute("user", newUser);
        return "%success";
    }

    public String loginAjax(HttpServletRequest request, HttpServletResponse response, Page page) {
        String email = request.getParameter("email");
        email = HtmlUtils.htmlEscape(email);
        String password = request.getParameter("password");
        User user = userDAO.get(email, password);

        if (null == user) {
            return "%fail";
        }

        request.getSession().setAttribute("user", user);
        return "%success";
    }

    public String checkLogin(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null)
            return "%success";
        else
            return "%fail";
    }

    public String logout(HttpServletRequest request, HttpServletResponse response, Page page) {
        request.getSession().removeAttribute("user");
        return "@forehome";
    }

    public String product(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("pid"));
        Product p = productDAO.get(id);
        List<ProductImage> productSingleImages = productImageDAO.list(p, ProductImageDAO.type_single);
        List<ProductImage> productDetailImages = productImageDAO.list(p, ProductImageDAO.type_detail);

        p.setProductSingleImages(productSingleImages);
        p.setProductDetailImages(productDetailImages);
        
        Promotion promotionByProduct = promotionItemDAO.getByProduct(p.getId()).getPromotion();
    if (promotionByProduct != null) {
    	String promotionName = promotionByProduct.getName();
        String discountTypeName = promotionByProduct.getDiscountTypeDescription();
        p.setPromotionName(promotionName);
        p.setDiscountTypeName(discountTypeName);
    }

        request.setAttribute("p", p);
        return "product.jsp";
    }

    
    public String brand(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Brand brand = brandDAO.get(cid);
        
        productDAO.fill(brand);

        request.setAttribute("c", brand);
        return "brand.jsp";
    }

    /*
    public String search(HttpServletRequest request, HttpServletResponse response, Page page) {
        String keyword = request.getParameter("keyword");
        String sort = request.getParameter("sort");
        List < Product > productList =
            productDAO.search(keyword, 0, 20);
        productDAO.setSaleAndReviewNumber(productList);
        request.setAttribute("ps",
            productList);
        return "searchResult.jsp";
    }

    public String buyone(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid =
            Integer.parseInt(request.getParameter("pid"));
        int num =
            Integer.parseInt(request.getParameter("num"));
        Product p =
            productDAO.get(pid);
        int oiid = 0;

        User user = (User) request.getSession().getAttribute("user");
        boolean found =
            false;
        List < OrderItem > ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem oi: ois) {
            if (oi.getProduct().getId() == p.getId()) {
                oi.setNumber(oi.getNumber() + num);
                orderItemDAO.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }

        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setNumber(num);
            oi.setProduct(p);
            orderItemDAO.add(oi);
            oiid = oi.getId();
        }
        return "@forebuy?oiid=" + oiid;

    }
    */
     
    public String buy(HttpServletRequest request, HttpServletResponse response, Page page) {
        String[] oiids = request.getParameterValues("oiid");
        List<OrderItem> ois = new ArrayList<OrderItem>();
        
        double total = 0;
        for (String oiidString : oiids) {
            int oiid = Integer.parseInt(oiidString);
            OrderItem ot = orderItemDAO.get(oiid);
            ot.setOriginalPrice(ot.getProduct().getPrice());
            ois.add(ot);
            total += (ot.getOriginalPrice() * ot.getQuantity());
        }
        
        
     
        /**
         * Chain Of Responsibility Pattern
         */
        // Init Chain
        DiscountPolicy nationHolidayDiscount = new BuyXGetYFreePolicy();
        DiscountPolicy lastYear100KDiscount = new BroughtMoreThanInLastYearPolicy();
		DiscountPolicy eachGroupOf100Discount = new EachGroupOfNPolicy();
		DiscountPolicy xyzDiscount = new ProductSetPolicy();
		DiscountPolicy noDiscount = new NoDiscountPolicy();
		  
		// Setting Chain Order
      	nationHolidayDiscount.setNextDiscountPolicy(lastYear100KDiscount);
		lastYear100KDiscount.setNextDiscountPolicy(eachGroupOf100Discount);
		eachGroupOf100Discount.setNextDiscountPolicy(xyzDiscount);
		xyzDiscount.setNextDiscountPolicy(noDiscount);
		    
		// Send OrderItem List to the pattern for calc discount.
	    // Return DiscountRequest, it contains:
	    // 1. (String) discountMsg (e.g. "eachGroupOfN: -100")
	    // 2. (float) totalDiscount (e.g. 100.0)
	    DiscountRequest dr = new DiscountRequest();
	    dr.setOrderItems(ois);
		dr.setNationHoliday(true);
		dr.setLastYearAmount(200000);
		dr = nationHolidayDiscount.handleDiscount(dr);
		    
		double totalWithoutDiscount = total;
        // calc actual payment amount
        total = total - dr.getTotalDiscount();

        request.getSession().setAttribute("ois", ois);
        request.setAttribute("totalWithoutDiscount", totalWithoutDiscount);
        request.setAttribute("discountMsg", dr.getDiscountMsg());
        request.setAttribute("total", total);
        return "buy.jsp";
    }

    public String addCart(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        User user = (User) request.getSession().getAttribute("user");
        Product p = productDAO.get(pid);
        
        boolean found = false;
        List<OrderItem> ois = orderItemDAO.listCartByUser(user.getId());
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId() == p.getId()) {
                oi.setQuantity(oi.getQuantity() + num);
                oi.setOriginalPrice(p.getPrice());
                orderItemDAO.update(oi);
                found = true;
                break;
            }
        }
        
        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setQuantity(num);
            oi.setState(1);
            oi.setOriginalPrice(p.getPrice());
            oi.setProduct(p);
            orderItemDAO.add(oi);
        }

        return "%success";
    }

    public String cart(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        List<OrderItem> ois = new ArrayList<>();

        ois = orderItemDAO.listCartByUser(user.getId());

        ShoppingCart shoppingCart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        Caretaker caretaker = (Caretaker) request.getSession().getAttribute("caretaker");
        if (shoppingCart == null) {
        	shoppingCart = new ShoppingCart();
        }
        if (caretaker == null) {
        	caretaker = new Caretaker();
        }
        
        shoppingCart.setOrderItems(ois);
        
        request.getSession().setAttribute("shoppingCart", shoppingCart);
        request.getSession().setAttribute("caretaker", caretaker);
        request.setAttribute("ois", ois);
        return "cart.jsp";
    }
    
    public String undoCart(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
        	return "%fail";
        }
        List<OrderItem> ois;

        ShoppingCart shoppingCart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        Caretaker caretaker = (Caretaker) request.getSession().getAttribute("caretaker");
        
        /**
         * Memento Pattern
         */
        shoppingCart.restoreFromMemento(caretaker.getMemento());
        ois = shoppingCart.getOrderItems();
        for (OrderItem oi : ois) {
        	orderItemDAO.update(oi);
        }
        
        request.getSession().setAttribute("shoppingCart", shoppingCart);
        request.getSession().setAttribute("caretaker", caretaker);

        return "%success";
    }

    public String changeOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) throws ClassNotFoundException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "%fail";
        }
        int num = Integer.parseInt(request.getParameter("num"));
        int oiid = Integer.parseInt(request.getParameter("oiid"));
        int state = Integer.parseInt(request.getParameter("state"));
        OrderItem oi = orderItemDAO.get(oiid);
        
        /**
         * Memento Pattern
         */
        ShoppingCart shoppingCart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        Caretaker caretaker = (Caretaker) request.getSession().getAttribute("caretaker");
        caretaker.saveMemento(shoppingCart.createMemento());
         
        oi.setQuantity(num);
        oi.setState(state);
        orderItemDAO.update(oi);
        
        List<OrderItem> ois;
        ois = orderItemDAO.listCartByUser(user.getId());
        shoppingCart.setOrderItems(ois);
        
        request.getSession().setAttribute("shoppingCart", shoppingCart);
        request.getSession().setAttribute("caretaker", caretaker);

        return "%success";
    }

    public String deleteOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) throws ClassNotFoundException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (null == user)
            return "%fail";
        
        int oiid = Integer.parseInt(request.getParameter("oiid"));
        OrderItem oi = orderItemDAO.get(oiid);
        
        /**
         * Memento Pattern
         */
        ShoppingCart shoppingCart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        Caretaker caretaker = (Caretaker) request.getSession().getAttribute("caretaker");
        caretaker.saveMemento(shoppingCart.createMemento());
        
        oi.setState(0);
        orderItemDAO.update(oi);
        
        List<OrderItem> ois;
        ois = orderItemDAO.listCartByUser(user.getId());
        shoppingCart.setOrderItems(ois);
        
        request.getSession().setAttribute("shoppingCart", shoppingCart);
        request.getSession().setAttribute("caretaker", caretaker);

        return "%success";
    }
    
    public String clearOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
    	List<OrderItem> ois = (List<OrderItem>) request.getSession().getAttribute("ois");
    	
    	/**
    	 * Facade Pattern
    	 */
    	ClearOrderFacade clearOrderFacade = new ClearOrderFacade(ois);
    	clearOrderFacade.operation();
    	
    	return "@forecart";
    }

    public String createOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");

        List<OrderItem> ois = (List<OrderItem>) request.getSession().getAttribute("ois");
        if (ois.isEmpty())
            return "@login.jsp";

        String address = request.getParameter("address");
        String paymentOption = request.getParameter("paymentOption");

        Order order = new Order();

        order.setUser(user);
        order.setDateOrdered(new Date());
        order.setState(OrderDAO.waitPay);
        order.setAddress(address);

        orderDAO.add(order);
        
        float total = 0;
        for (OrderItem oi : ois) {
            oi.setOrder(order);
            orderItemDAO.update(oi);
            total += oi.getProduct().getPrice() * oi.getQuantity();
        }

        return "@forepay?oid=" + order.getId() + "&total=" + total + "&paymentOption=" + paymentOption;
    }

    public String pay(HttpServletRequest request, HttpServletResponse response, Page page) {
        return "pay.jsp";
    }

    public String paied(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order order = orderDAO.get(oid);

        order.setState(orderDAO.waitDelivery);
        order.setDatePaid(new Date());
        orderDAO.update(order);

        request.setAttribute("o", order);
        return "paied.jsp";
    }

    public String bought(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        List<Order> os = orderDAO.list(user.getId(), orderDAO.delete);
        orderItemDAO.fill(os);

        request.setAttribute("os", os);
        return "bought.jsp";
    }

    public String deleteOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);

        o.setState(OrderDAO.delete);
        orderDAO.update(o);

        return "%success";
    }
    
    public String subscribe(HttpServletRequest request, HttpServletResponse response, Page page) {
    	
     	int uid=((User) request.getSession().getAttribute("user")).getId();
     	List<Subscription> subscriptions= subscriptionDAO.list(uid);
     	Product product=null;
     	List<Product> products= new ArrayList<Product>();
     	for(Subscription subscription:subscriptions) {
     		product=productDAO.get(subscription.getProduct().getId());
     		products.add(product);
     	}
     	request.setAttribute("products", products);
     	return "subscribe.jsp";
    	
    	
    }
    
    public String createsubscribe(HttpServletRequest request, HttpServletResponse response, Page page) {
    	int pid=Integer.parseInt(request.getParameter("pid"));
    	String returnURL=request.getParameter("returnpage");
    	System.out.println(pid);
    	System.out.println(returnURL);
    	if(request.getSession().getAttribute("user")!=null) {
    		int uid=((User) request.getSession().getAttribute("user")).getId();
    		if(subscriptionDAO.check(pid,uid)) {
    			return "%success";
    		}else{
    			subscriptionDAO.add(pid,uid);
    			return "%success";
    		}
    		
    		
    	}else{
    		return "%failed";
    	}
    
    }
    
    public String deleteSubscription(HttpServletRequest request, HttpServletResponse response, Page page) {
        int uid =((User) request.getSession().getAttribute("user")).getId();
    	int pid = Integer.parseInt(request.getParameter("pid"));
        subscriptionDAO.delete(uid,pid);
        return "%success";
    }

}
