package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Option;
import beans.User;
import dao.OptionDAO;
import dao.QuotationDAO;

/**
 * Servlet implementation class HomeClient
 */
@WebServlet("/GetQuotations")
public class GetQuotations extends HttpServlet {
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
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		User user = (User) request.getSession().getAttribute("user");
		QuotationDAO qtn = new QuotationDAO(connection);
		
		try {
			String json = qtn.getCustomerQuotationsJson(user);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		QuotationDAO qtn = new QuotationDAO(connection);
		OptionDAO opt = new OptionDAO(connection);
		User user = (User) request.getSession().getAttribute("user");
		String product = request.getParameter("product");
		List<Option> options;
		int idProduct;
		
		if (product != null) {
			
			idProduct = Integer.valueOf(product);
			request.getSession().setAttribute("product", idProduct);
			try {
				options = opt.getOptions(idProduct);
				request.setAttribute("options", options);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			doGet(request, response);
			
		} else {
			
			idProduct = (int) request.getSession().getAttribute("product");
			
			try {
				options = opt.getOptions(idProduct);
				List<Option> optionsChecked = new ArrayList<>();
				
				for (Option o : options) {
					String optionParameter = request.getParameter(String.valueOf(o.getOptionCode()));
					if(optionParameter != null) optionsChecked.add(o);
				}
				
				qtn.addQuotation(user, idProduct, optionsChecked);
				request.getSession().setAttribute("product", null);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			response.sendRedirect(request.getContextPath() + "/HomeCustomer");
		}
	}


	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}
