package main.java.controller.servlet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.util.HtmlUtils;

import main.java.model.bean.Category;
import main.java.model.bean.Order;
import main.java.model.bean.OrderItem;
import main.java.model.bean.Product;
import main.java.model.bean.ProductImage;
import main.java.model.bean.User;
import main.java.model.dao.CategoryDAO;
import main.java.model.dao.OrderDAO;
import main.java.model.dao.ProductDAO;
import main.java.model.dao.ProductImageDAO;
import main.java.model.util.Page;

@WebServlet("/foreServlet")
public class ForeServlet extends BaseForeServlet {

	public String home(HttpServletRequest request, HttpServletResponse response, Page page) {
		List<Category> cs = new CategoryDAO().list();

		new ProductDAO().fill(cs);
		new ProductDAO().fillByRow(cs);
		request.setAttribute("cs", cs);
		
		return "home.jsp";
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

	public String login(HttpServletRequest request, HttpServletResponse response, Page page) {
        String email = request.getParameter("email");
        email = HtmlUtils.htmlEscape(email);
        String password = request.getParameter("password");
        
        System.out.println(email);
        System.out.println(password);

        User user = userDAO.get(email, password);

        if (null == user) {
            request.setAttribute("msg", "Account Error!!!");
            return "login.jsp";
        }
        request.getSession().setAttribute("user", user);
        return "%forehome";
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

        request.setAttribute("p", p);
        return "product.jsp";
    }

   

    /*
    public String category(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category category = categoryDAO.get(cid);
        productDAO.fill(category);
        productDAO.setSaleAndReviewNumber(category.getProducts());
        String sort = request.getParameter("sort");
        if (sort != null) {
            switch (sort) {
            case "review":
                Collections.sort(category.getProducts(), new ProductReviewComparator());
                break;
            case "date":
                Collections.sort(category.getProducts(), new ProductDateComparator());
                break;

            case "saleCount":
                Collections.sort(category.getProducts(), new ProductSaleCountComparator());
                break;

            case "price":
                Collections.sort(category.getProducts(), new ProductPriceComparator());
                break;

            case "all":
                Collections.sort(category.getProducts(), new ProductAllComparator());
                break;
            }
        }

        request.setAttribute("c", category);
        return "category.jsp";
    }

    public String search(HttpServletRequest request, HttpServletResponse response, Page page) {
        String keyword = request.getParameter("keyword");
        String sort = request.getParameter("sort");
        List<Product> productList = productDAO.search(keyword, 0, 20);
        productDAO.setSaleAndReviewNumber(productList);
        request.setAttribute("ps", productList);
        return "searchResult.jsp";
    }

    public String buyone(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        Product p = productDAO.get(pid);
        int oiid = 0;

        User user = (User) request.getSession().getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem oi : ois) {
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
            System.out.println("199: " + oiid);
            OrderItem ot = orderItemDAO.get(oiid);
            System.out.println("201: ");
            ois.add(ot);
            System.out.println("203: ");
            total += (ot.getProduct().getPrice() * ot.getQuantity());
            System.out.println("205: " + total);
        }
        request.getSession().setAttribute("ois", ois);
        request.setAttribute("total", total);
        return "buy.jsp";
    }



    public String addCart(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        User user = (User) request.getSession().getAttribute("user");
        Product p = productDAO.get(pid);

        boolean found = false;
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId() == p.getId()) {
            	oi.setQuantity(oi.getQuantity() + num);
                orderItemDAO.update(oi);
                found = true;
                break;
            }
        }

        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setQuantity(num);
            oi.setProduct(p);
            orderItemDAO.add(oi);
        }
        return "%success";
    }



    public String cart(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        List<OrderItem> ois = new ArrayList<>();
        ois = orderItemDAO.listByUser(user.getId());
        request.setAttribute("ois", ois);
        return "cart.jsp";
    }


    public String changeOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "%fail";
        }
        int num = Integer.parseInt(request.getParameter("num"));
        int oiid = Integer.parseInt(request.getParameter("oiid"));
        OrderItem oi = orderItemDAO.get(oiid);
        oi.setQuantity(num);
        orderItemDAO.update(oi);
        return "%success";
    }

    public String deleteOrderItem(HttpServletRequest request, HttpServletResponse response, Page page){
        User user =(User) request.getSession().getAttribute("user");
        if(null==user)
            return "%fail";
        int oiid = Integer.parseInt(request.getParameter("oiid"));
        orderItemDAO.delete(oiid);
        return "%success";
    }

    public String createOrder(HttpServletRequest request, HttpServletResponse response, Page page){
        User user =(User) request.getSession().getAttribute("user");


        List<OrderItem> ois= (List<OrderItem>) request.getSession().getAttribute("ois");
        if(ois.isEmpty())
            return "@login.jsp";

        String address = request.getParameter("address");

        Order order = new Order();

        order.setUser(user);
        order.setDateOrdered(new Date());
        order.setState(OrderDAO.waitPay);
        
        order.setAddress(address);

        orderDAO.add(order);
        float total =0;
        for (OrderItem oi: ois) {
            oi.setOrder(order);
            orderItemDAO.update(oi);
            total+=oi.getProduct().getPrice() * oi.getQuantity();
        }

        return "@forepay?oid=" + order.getId() + "&total=" + total;
    }


    public String pay(HttpServletRequest request, HttpServletResponse response, Page page){
        return "pay.jsp";
    }


    public String paied(HttpServletRequest request, HttpServletResponse response, Page page){
          int oid = Integer.parseInt(request.getParameter("oid"));
          Order order = orderDAO.get(oid);
          order.setState(orderDAO.waitDelivery);
          order.setDatePaid(new Date());
          orderDAO.update(order);
          request.setAttribute("o", order);
          return "paied.jsp";
    }


    public String bought(HttpServletRequest request, HttpServletResponse response, Page page){
        User user = (User) request.getSession().getAttribute("user");
        List<Order> os = orderDAO.list(user.getId(), orderDAO.delete);
        orderItemDAO.fill(os);
        request.setAttribute("os", os);
        return "bought.jsp";
    }


    public String deleteOrder(HttpServletRequest request, HttpServletResponse response, Page page){
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);
        o.setState(OrderDAO.delete);
        orderDAO.update(o);
        return "%success";
    }


}
