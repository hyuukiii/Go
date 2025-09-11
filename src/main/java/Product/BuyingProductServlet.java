package Product;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import BidsTransaction.BidDAO;
import BidsTransaction.BidDTO;

@WebServlet("/buyingproduct")
public class BuyingProductServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		String userId = (String) session.getAttribute("user_id");

		// 로그인하지 않은 사용자는 로그인 페이지로 리다이렉트
		if (userId == null) {
			resp.sendRedirect("login");
			return;
		}

		int productId = Integer.parseInt(req.getParameter("product_id"));

		BuyingProductDAO dao = new BuyingProductDAO();
		dao.incrementViewCount(productId);

		BuyingProductService service = new BuyingProductService();
		ProductDTO product = service.getProduct(productId);

		// product 가 null 인 경우 처리
		if (product == null) {
			// 오류 메시지 설정
			req.setAttribute("errorMessage", "요청하신 상품을 찾을 수 없습니다.");
			// 메인 페이지로 리다이렉트
			resp.sendRedirect("index");
			return;
		}
		
		BidDAO bidDAO = new BidDAO();
		Integer currentBid = bidDAO.getCurrentBid(productId);

		if (currentBid == null) {
			currentBid = 0;
		}
		req.setAttribute("current_bid", currentBid);

		ArrayList<BidDTO> bidHistory = bidDAO.getBidHistoryByProductId(productId);
		req.setAttribute("bidHistory", bidHistory);

		FavoriteDAO favDAO = new FavoriteDAO();
		boolean isFavorited = favDAO.isFavorited(userId, productId);
		req.setAttribute("isFavorited", isFavorited);

		// 상품 소유자 확인 로직 추가 (product 가 null 이 아닌 경우에만 실행)
		boolean isProductOwner = userId.equals(product.getSeller_id());
		req.setAttribute("isProductOwner", isProductOwner);

		req.setAttribute("product", product);
		req.getRequestDispatcher("/WEB-INF/views/BuyingProduct.jsp").forward(req, resp);
	}
}
