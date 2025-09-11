package Mainpage;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BidsTransaction.BidDAO;
import Product.BuyingProductService;
import Product.ProductDTO;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		BuyingProductService service = new BuyingProductService();

		// 판매중인 상품 가져오기
		ArrayList<ProductDTO> availableProducts = service.getAvailableProducts();
		request.setAttribute("products", availableProducts);

		// 인기 상품(조회순) 가져오기
		ArrayList<ProductDTO> popularProducts = service.getPopularProducts();
		request.setAttribute("popularProducts", popularProducts);

		// 경매 종료된 상품 가져오기
		ArrayList<ProductDTO> completedAuctions = service.getCompletedAuctions();
		request.setAttribute("completedAuctions", completedAuctions);

		String userId = (String) request.getSession().getAttribute("user_id");
		if (userId != null) {
			ArrayList<ProductDTO> favorites = service.getFavoriteProducts(userId);
			request.setAttribute("favorites", favorites);

			BidDAO bidDAO = new BidDAO();
			ArrayList<ProductDTO> winningProducts = bidDAO.selectAllWinningProducts(userId);
			request.setAttribute("winningProducts", winningProducts);
		}

		// 상품 삭제 후 메시지 처리
		String message = request.getParameter("message");
		if (message != null && message.equals("deleted")) {
			request.setAttribute("statusMessage", "상품이 성공적으로 삭제되었습니다.");
		}

		request.getRequestDispatcher("WEB-INF/views/Index.jsp").forward(request, response);
	}
}