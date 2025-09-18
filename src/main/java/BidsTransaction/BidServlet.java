package BidsTransaction;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Notification.NotificationDAO;
import Product.BuyingProductDAO;
import Product.ProductDTO;

@WebServlet("/bid")
public class BidServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("user_id");

		if (userId == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		int productId = Integer.parseInt(request.getParameter("product_id"));
		int bidAmount = Integer.parseInt(request.getParameter("bid_price"));

		BuyingProductDAO productDAO = new BuyingProductDAO();
		ProductDTO product = productDAO.getProductAuctionInfo(productId);

		if (userId.equals(product.getSeller_id())) {
			alertAndBack(response, "판매자는 자신의 상품에 입찰할 수 없습니다.");
			return;
		}
		
		int minPrice = product.getMinPrice();
		int maxPrice = product.getMaxPrice();

		if ("판매완료".equals(product.getStatus()) || "경매종료".equals(product.getStatus())) {
			response.sendRedirect("buyingproduct?product_id=" + productId + "&error=ended");
			return;
		}

		BidDAO bidDAO = new BidDAO();
		Integer currentBid = bidDAO.getCurrentBid(productId);
		if (currentBid == null)
			currentBid = 0;

		if (bidAmount < minPrice) {
			alertAndBack(response, "최소입찰가보다 낮게 입찰할 수 없습니다.");
			return;
		}

		if (bidAmount > maxPrice) {
			alertAndBack(response, "최대입찰가보다 높게 입찰할 수 없습니다.");
			return;
		}

		if (bidAmount <= currentBid) {
			alertAndBack(response, "현재 입찰가보다 높은 금액을 입력해야 합니다.");
			return;
		}

		bidDAO.insertBid(userId, productId, bidAmount);

		if (bidAmount == maxPrice) {
			productDAO.markAsSold(productId, bidAmount);

			TransactionDAO transDAO = new TransactionDAO();
			transDAO.insertTransaction(userId, product.getSeller_id(), productId, bidAmount);
			bidDAO.markWinningBid(productId);
		}

		// 입찰 처리 성공 후 알림 추가
		NotificationDAO notificationDAO = new NotificationDAO();
		ProductDTO productDTO = productDAO.getProductById(productId);
		String content;
		
		if (bidAmount == maxPrice) {
			// 희망가에 도달한 입찰일 경우
			content = "[" + productDTO.getTitle() + "] 상품에 희망 입찰가를 받았습니다.";
		} else if (bidAmount > currentBid && currentBid > 0) {
			// 이전 입찰가보다 높은 입찰인 경우
			content = "[" + productDTO.getTitle() + "] 상품에 이전 입찰가(" + currentBid + "원)보다 " + (bidAmount - currentBid) + "원 높은 새 입찰이 있습니다.";
		} else {
			// 첫 입찰인 경우
			content = "[" + productDTO.getTitle() + "] 상품에 " + bidAmount + "원의 새로운 입찰이 있습니다.";
		}
		notificationDAO.addNotification(productDTO.getSeller_id(), content, "BID", productId);
		
		// 이전 최고 입찰자가 있다면 알림
		BidDTO highestBid = bidDAO.getHighestBid(productId);
		if (highestBid != null && !highestBid.getUser_id().equals(userId)) {
			if (bidAmount == maxPrice) {
				// 희망 입찰가로 낙찰되어 경매 종료된 경우
				content = "[" + productDTO.getTitle() + "] 상품이 다른 입찰자의 희망 입찰가 입찰로 경매가 종료되었습니다.";
			} else {
				// 일반적인 상위 입찰
				content = "[" + productDTO.getTitle() + "] 상품에 귀하의 입찰가보다 " + (bidAmount - highestBid.getBid_amount()) + "원 높은 새 입찰이 있습니다.";
			}
			notificationDAO.addNotification(highestBid.getUser_id(), content, "OUTBID", productId);
		}

		response.sendRedirect("buyingproduct?product_id=" + productId);
	}

	private void alertAndBack(HttpServletResponse response, String message) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + message + "');");
		out.println("history.back();");
		out.println("</script>");
		out.flush();
	}
}
