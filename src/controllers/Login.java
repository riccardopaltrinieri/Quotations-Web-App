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

import beans.User;
import dao.UserDAO;

/**
 * Servlet implementation class LogIn
 * Used to get user info from database if the credentials
 * are right
 */
@WebServlet({"/LogIn", "/Login"})
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
    
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
			connection  = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e ) {
			throw new UnavailableException("Can't load db Driver " + context.getInitParameter("dbDriver"));
		} catch (SQLException e ) {
			throw new UnavailableException("Couldn't connect");
		}
		
		//Thymeleaf initialization
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "/index.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		UserDAO usr = new UserDAO(connection);
		
		String usrn = request.getParameter("username");
		String pwd = request.getParameter("password");
		Boolean html = Boolean.valueOf(request.getParameter("html"));
		String path;
		
		User user;
		try {
			user = usr.checkCredentials(usrn, pwd);
			// If the credentials were right the user is redirected to the Home
			if(user.getRole().equals("customer")) {
				if(html) 	path = getServletContext().getContextPath() + "/HomeCustomer";
				else 		path = getServletContext().getContextPath() + "/HomeCustomerJS";
				request.getSession().setAttribute("user",user);
				response.sendRedirect(path);
			} else if(user.getRole().equals("employee")) {
				if(html) 	path = getServletContext().getContextPath() + "/HomeEmployee";
				else 		path = getServletContext().getContextPath() + "/HomeEmployeeJS";
				request.getSession().setAttribute("user",user);
				response.sendRedirect(path);
			}
		} catch (SQLException e) {
			// If the credentials weren't right an error message is shown in the 
			// console and on the page
			System.out.println(e);
			request.setAttribute("notvalid", "true");
			doGet(request,response);
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
