package wdsr.exercise3.client;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import wdsr.exercise3.model.Product;
import wdsr.exercise3.model.ProductType;
import wdsr.exercise3.server.ProductResource;

public class ProductService extends RestClientBase {
	protected ProductService(final String serverHost, final int serverPort, final Client client) {
		super(serverHost, serverPort, client);
	}
	
	/**
	 * Looks up all products of given types known to the server.
	 * @param types Set of types to be looked up
	 * @return A list of found products - possibly empty, never null.
	 */
	public List<Product> retrieveProducts(Set<ProductType> types) {
		WebTarget statusTarget = baseTarget.path("/products");
		List<Product> response = statusTarget.queryParam("type",types.toArray())
		        .request(MediaType.APPLICATION_JSON)
		        .get(new GenericType<List<Product>>() {});
		return response;
	}
	
	/**
	 * Looks up all products known to the server.
	 * @return A list of all products - possibly empty, never null.
	 */
	public List<Product> retrieveAllProducts() {
		WebTarget statusTarget = baseTarget.path("/products");
		List<Product> response = statusTarget
		        .request(MediaType.APPLICATION_JSON)
		        .get(new GenericType<List<Product>>() {});
		
		return response;
	}
	
	/**
	 * Looks up the product for given ID on the server.
	 * @param id Product ID assigned by the server
	 * @return Product if found
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public Product retrieveProduct(int id) {
		WebTarget statusTarget = baseTarget.path("/products").path(String.valueOf(id));
		Product response = statusTarget
		        .request(MediaType.APPLICATION_JSON)
		        .get(Product.class);
		
		if(response == null)return null;
		else return response;
	}	
	
	/**
	 * Creates a new product on the server.
	 * @param product Product to be created. Must have null ID field.
	 * @return ID of the new product.
	 * @throws WebApplicationException if request to the server failed
	 */
	public int storeNewProduct(Product product) {
		int idOfnewProduct = 0;
		
		Response response;
			WebTarget newProduct = baseTarget.path("/products");
			response = newProduct.request(MediaType.APPLICATION_JSON)
			        .post(Entity.json(product));
			
			if(response.getStatus() >= 400)throw new WebApplicationException();
			
		URI locString = response.getLocation();
		int lastSlash = locString.toString().lastIndexOf("/")+1;
		idOfnewProduct = Integer.parseInt(locString.toString().substring(lastSlash));
		response.close();
		
		return idOfnewProduct;
	}
	
	/**
	 * Updates the given product.
	 * @param product Product with updated values. Its ID must identify an existing resource.
	 * @throws NotFoundException if no product found for the given ID.
	 */
	
	public void updateProduct(Product product) {
	
			WebTarget statusTarget = baseTarget.path("/products").path(String.valueOf(product.getId()));
			Response response = statusTarget
			        .request(MediaType.APPLICATION_JSON)
			        .put(Entity.json(product));
			
			if(response.getStatus() == 404)throw new NotFoundException();
	}
	
	/**
	 * Deletes the given product.
	 * @param product Product to be deleted. Its ID must identify an existing resource.
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public void deleteProduct(Product product) {
		WebTarget statusTarget = baseTarget.path("/products").path(String.valueOf(product.getId()));
		Response response = statusTarget.request(MediaType.APPLICATION_JSON).delete();
		if(response.getStatus() == 404) throw new NotFoundException();
	}
}
