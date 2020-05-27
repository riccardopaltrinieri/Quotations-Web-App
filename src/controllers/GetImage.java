package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ImageDAO;

/**
 * Servlet implementation class SendImage
 * Used for showing images in other pages, 
 * should be used only with <img src="/GetImage?image=*" alt=""> tag
 */
@WebServlet("/GetImage")
public class GetImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ImageDAO img = new ImageDAO();
        File image;

        String requestedImage = request.getParameter("image");
        
        if (requestedImage != null) 
        	image = img.getImage(requestedImage);
        else throw new NullPointerException();
        
        if (!image.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // Error 404.
            throw new NoSuchElementException(image.toString());
        }
        
        // Init servlet response.
        response.reset();
        response.setContentType("image/jpeg");
        response.setHeader("Content-Length", String.valueOf(image.length()));

        // Write image content to response.
        Files.copy(image.toPath(), response.getOutputStream());
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
