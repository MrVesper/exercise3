package wdsr.exercise3.client;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;

import wdsr.exercise3.model.Product;
import wdsr.exercise3.model.ProductType;
import wdsr.exercise3.server.ServerApplication;

public class ProductServiceTest extends ClientTestBase {
	private ServerApplication serverApp;
	private ProductService productService;

	@Before
	public void setUp() {
		super.setUp();
		serverApp = new ServerApplication();
		server.deploy(serverApp);
		productService = new ProductService(SERVER_HOST, SERVER_PORT, client);
	}

	@Test
	public void retrieveAllProducts_shouldReturnEmptyListWhenNoProductsFound() {
		// given
		// when
		List<Product> result = productService.retrieveAllProducts();

		// then
		assertEquals(0, result.size());
	}

	@Test
	public void retrieveAllProducts_shouldReturnProductListWhenProductsFound() {
		// given
		List<Product> products = Arrays.asList(new Product(null, "copper", ProductType.METAL),
				new Product(null, "rice", ProductType.FOOD));
		for (Product p : products) {
			serverApp.addProduct(p);
		}

		// when
		List<Product> result = productService.retrieveAllProducts();

		// then
		assertEquals(2, result.size());
	}

	@Test
	public void retrieveProducts_shouldReturnEmptyListWhenNoProductsFound() {
		// given
		// when
		List<Product> result = productService.retrieveProducts(Collections.singleton(ProductType.ENERGY));

		// then
		assertEquals(0, result.size());
	}

	@Test
	public void retrieveProducts_shouldReturnProductListWhenProductsFound() {
		// given
		List<Product> products = Arrays.asList(new Product(null, "copper", ProductType.METAL),
				new Product(null, "rice", ProductType.FOOD), new Product(null, "wheat", ProductType.FOOD),
				new Product(null, "gas", ProductType.ENERGY), new Product(null, "oil", ProductType.ENERGY));
		for (Product p : products) {
			serverApp.addProduct(p);
		}

		// when
		List<Product> result = productService
				.retrieveProducts(new HashSet<ProductType>(Arrays.asList(ProductType.FOOD, ProductType.ENERGY)));

		// then
		assertEquals(4, result.size());
	}

	@Test
	public void storeNewProduct_shouldCreateNewProduct() {
		// given
		List<Product> products = Arrays.asList(new Product(null, "copper", ProductType.METAL),
				new Product(null, "rice", ProductType.FOOD), new Product(null, "wheat", ProductType.FOOD),
				new Product(null, "gas", ProductType.ENERGY), new Product(null, "oil", ProductType.ENERGY));
		
		List<Integer> resourceIds = new ArrayList<>();

		// when
		for (Product p : products) {
			resourceIds.add(productService.storeNewProduct(p));
		}

		// then
		assertEquals(Arrays.asList(1, 2, 3, 4, 5), resourceIds);
	}

	@Test(expected = WebApplicationException.class)
	public void storeNewProduct_shouldRaiseAnExceptionIfIDFieldSet() {
		// given
		Product product = new Product(1, "oil", ProductType.ENERGY);

		// when
		productService.storeNewProduct(product);

		// then
		// exception should have been thrown
	}

	@Test
	public void updateProduct_shouldUpdateProductIfFound() {
		// given
		Product product = new Product(null, "oil", ProductType.ENERGY);
		int productId = serverApp.addProduct(product);
		Product updatedProduct = new Product(productId, "gold", ProductType.METAL);

		// when
		productService.updateProduct(updatedProduct);

		// then
		assertEquals(updatedProduct, serverApp.getProduct(productId));
	}

	@Test(expected = NotFoundException.class)
	public void updateProduct_shouldThrowExceptionIfProductNotFound() {
		// given
		Product product = new Product(1, "gold", ProductType.METAL);

		// when
		productService.updateProduct(product);

		// then
		// exception should have been thrown
	}

	@Test
	public void deleteProduct_shouldDeleteProduct() {
		// given
		Product product = new Product(null, "oil", ProductType.ENERGY);
		serverApp.addProduct(product);

		// when
		productService.deleteProduct(product);

		// then
		assertEquals(0, serverApp.getAllProducts().size());
	}

	@Test(expected = NotFoundException.class)
	public void deleteProduct_shouldThrowExceptionIfProductNotFound() {
		// given
		Product product = new Product(1, "gold", ProductType.METAL);

		// when
		productService.deleteProduct(product);

		// then
		// exception should have been thrown
	}

	@Test
	public void retrieveProduct_shouldRetrieveProductIfExists() {
		// given
		Product product = new Product(null, "oil", ProductType.ENERGY);
		int productId = serverApp.addProduct(product);

		// when
		Product result = productService.retrieveProduct(productId);

		// then
		assertEquals(product, result);
	}

	@Test(expected = NotFoundException.class)
	public void retrieveProduct_shouldThrowExceptionIfProductNotFound() {
		// given

		// when
		productService.retrieveProduct(1);

		// then
		// exception should have been thrown
	}
}
