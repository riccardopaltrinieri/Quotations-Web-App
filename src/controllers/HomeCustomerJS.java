package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Option;
import beans.Product;
import beans.User;
import dao.OptionDAO;
import dao.ProductDAO;
import dao.QuotationDAO;

/**
 * Servlet implementation class HomeClient
 */
@WebServlet("/HomeCustomerJS")
public class HomeCustomerJS extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    
	private TemplateEngine templateEngine;
    
	@Override
	public void init() throws ServletException {
		
		ServletContext context = getServletContext();

		//MySQL database connection initialization
		try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e ) {
			throw new UnavailableException("Can't load db Driver");
		} catch (SQLException e ) {
			throw new UnavailableException("Couldn't connect");
		}
		// Thymeleaf initialization
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		ProductDAO prd = new ProductDAO(connection);
		OptionDAO opt = new OptionDAO(connection);
		List<Product> products;
		List<Option> options;
		try {
			products = prd.getProducts();
			options = opt.getOptions(products.get(0).getId());
			request.setAttribute("products", products);
			request.setAttribute("options", options);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("time", String.valueOf((int) new Date().getTime()));
		String path = "/WEB-INF/HomeCustomerJS.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		QuotationDAO qtn = new QuotationDAO(connection);
		OptionDAO opt = new OptionDAO(connection);
		User user = (User) request.getSession().getAttribute("user");
		String product = request.getParameter("product");
		List<Option> options;
			
		try {
			if(product != null) {
				int idProduct = Integer.valueOf(product);
				options = opt.getOptions(idProduct);
				
				
				List<Option> optionsChecked = new ArrayList<>();
				
				for (Option o : options) {
					String optionParameter = request.getParameter(String.valueOf(o.getOptionCode()));
					if(optionParameter != null) optionsChecked.add(o);
				}
				
				qtn.addQuotation(user, idProduct, optionsChecked);
				request.getSession().setAttribute("product", null);
			} else 
				throw new NullPointerException("No product selected");
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}