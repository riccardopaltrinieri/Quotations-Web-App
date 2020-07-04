package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

import beans.Quotation;
import beans.User;
import dao.QuotationDAO;

/**
 * Servlet implementation class PriceQuote
 */
@WebServlet("/PriceQuote")
public class PriceQuote extends HttpServlet {
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
		
		QuotationDAO qtn = new QuotationDAO(connection);
		
		try {
			int idQuotation = Integer.valueOf(request.getParameter("quotation"));
			Quotation quotation = qtn.getQuotation(idQuotation);
			
			if(request.getParameter("javascript") == null) {
				
				request.getSession().setAttribute("idQuotation", idQuotation);
				request.setAttribute("quotation", quotation);
				String path = "/WEB-INF/PriceQuote.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				templateEngine.process(path, ctx, response.getWriter());
				
			} else {

				request.getSession().setAttribute("idQuotation", idQuotation);
				String json = qtn.getQuotationJson(idQuotation);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
				
			}
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		User user = (User) request.getSession().getAttribute("user");
		QuotationDAO qtn = new QuotationDAO(connection);
		int idQuotation = (int) request.getSession().getAttribute("idQuotation");
		
		try {
			Float price = Float.valueOf(request.getParameter("price"));
			if (price > 0) qtn.priceQuotation(user, idQuotation, price);
			else {
				request.setAttribute("notvalid", "true");
				request.setAttribute("quotation", qtn.getQuotation(idQuotation));
				String path = "/WEB-INF/PriceQuote.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}
		
		if(request.getParameter("javascript") == null) response.sendRedirect(request.getContextPath() + "/HomeEmployee");
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
