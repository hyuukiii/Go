package Product;


import java.util.ArrayList;

public class BuyingProductService {

	BuyingProductDAO dao = new BuyingProductDAO();

	public ProductDTO getProduct(int productId) {
		return dao.selectProductById(productId);
	}

	public ArrayList<ProductDTO> getAvailableProducts() {
		return dao.selectAvailableProducts();
	}

	public ArrayList<ProductDTO> getPopularProducts() {
		return dao.selectPopularProducts();
	}

	public ArrayList<ProductDTO> getCompletedAuctions() {
		return dao.selectCompletedAuctions();
	}

	public ArrayList<ProductDTO> getFavoriteProducts(String userId) {
		return dao.selectFavoriteProducts(userId);
	}

}
