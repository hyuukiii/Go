package Product;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BidsTransaction.BidDAO;
import BidsTransaction.BidDTO;
import BidsTransaction.TransactionDAO;
import Notification.NotificationDAO;

@WebServlet("/auctionend")
public class AuctionEndServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String productIdStr = req.getParameter("product_id");
		if (productIdStr == null || productIdStr.isEmpty()) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		int productId;
		try {
			productId = Integer.parseInt(productIdStr);
		} catch (NumberFormatException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// 해당 상품의 경매 종료 처리 실행
		boolean success = processAuctionEnd(productId);

		if (success) {
			resp.setStatus(HttpServletResponse.SC_OK);
		} else {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	// 경매 종료 처리 로직
	private boolean processAuctionEnd(int productId) {
		BidDAO bidDAO = new BidDAO();
		BuyingProductDAO productDAO = new BuyingProductDAO();
		NotificationDAO notificationDAO = new NotificationDAO();

		// 해당 상품의 최고 입찰 정보 가져오기
		BidDTO highestBid = bidDAO.getHighestBid(productId);
		ProductDTO product = productDAO.getProductById(productId);

		if (highestBid != null) {
			// 최고 입찰자가 있으면 낙찰 처리
			bidDAO.markWinningBid(productId);

			// 새로 추가: 최고 입찰 기록을 is_winning = 'Y'로 설정
			bidDAO.setWinningBid(productId);

			// 상품 상태 "경매종료"로 변경 및 최종 가격 설정
			productDAO.markAsSold(productId, highestBid.getBid_amount());

			// 거래 내역 추가
			TransactionDAO transactionDAO = new TransactionDAO();
			try {
				// 판매자 ID 가져오기
				if (product != null) {
					transactionDAO.addTransaction(product.getSeller_id(), // 판매자 ID
							highestBid.getUser_id(), // 구매자 ID (최고 입찰자)
							productId, // 상품 ID
							highestBid.getBid_amount()); // 낙찰 가격

					// 낙찰자에게 알림
					String content = "[" + product.getTitle() + "] 상품을 " + highestBid.getBid_amount() + "원에 낙찰받았습니다.";
					notificationDAO.addNotification(highestBid.getUser_id(), content, "WINNER", productId);

					// 판매자에게 알림
					content = "[" + product.getTitle() + "] 상품이 " + highestBid.getBid_amount() + "원에 낙찰되었습니다.";
					notificationDAO.addNotification(product.getSeller_id(), content, "SOLD", productId);

					// 입찰했지만 낙찰받지 못한 사용자들에게 알림
					ArrayList<String> bidders = notificationDAO.getProductBidders(productId);
					for (String bidderId : bidders) {
						if (!bidderId.equals(highestBid.getUser_id())) { // 낙찰자 제외
							content = "[" + product.getTitle() + "] 상품의 경매가 종료되었습니다. 최종 낙찰가: "
									+ highestBid.getBid_amount() + "원";
							notificationDAO.addNotification(bidderId, content, "AUCTION_END", productId);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			return true;
		} else {
			// 입찰자가 없는 경우, 상품 상태만 업데이트
			boolean result = updateProductStatus(productId);

			// 판매자에게 알림
			if (product != null) {
				String content = "[" + product.getTitle() + "] 상품의 경매가 종료되었으나 입찰자가 없었습니다.";
				notificationDAO.addNotification(product.getSeller_id(), content, "AUCTION_END", productId);
			}

			return result;
		}
	}

	// 입찰자가 없는 경우 상품 상태 업데이트
	private boolean updateProductStatus(int productId) {
		BuyingProductDAO dao = new BuyingProductDAO();
		boolean result = dao.updateProductStatus(productId, "경매종료");

		dao.removeFavoritesByProductId(productId);

		return result;
	}
}