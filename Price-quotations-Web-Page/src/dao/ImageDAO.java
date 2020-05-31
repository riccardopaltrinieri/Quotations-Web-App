package dao;

import java.io.File;



/**
 * An Object that can be used by the user to extract all the info about 
 * {@link Image} from a database. It uses the static database location :
 * {@value #dbLocation}
 * in the File System.
 * 
 */
public class ImageDAO {

	private static final String dbLocation = "C:\\Users\\ricky\\Documents\\GitHub\\TIW_Web_Page_Gestione_Preventivi\\WebContent\\WEB-INF\\images";
	private static final String productDir = "\\products";
	

	/**
	 * Construct the DAO with the database location.
	 * @param connection to a specific database
	 */
	public ImageDAO() {
		super();
	}
	
	
	public File getImage(String requestedImage) {
			
		String pathImage = productDir + "\\" + requestedImage + ".jpg";
		return new File(dbLocation, pathImage);
	}

}
