package Product;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import BidsTransaction.BidDAO;
import BidsTransaction.BidDTO;
import Notification.NotificationDAO;

import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet("/auctionmanage")
public class AuctionManagementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// 요청 파라미터 추출
		String action = request.getParameter("action");
		String productIdStr = request.getParameter("product_id");
		
		// 응답 설정
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		if (productIdStr == null || productIdStr.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("상품 ID가 필요합니다.");
			return;
		}
		
		int productId;
		try {
			productId = Integer.parseInt(productIdStr);
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("유효하지 않은 상품 ID 형식입니다.");
			return;
		}
		
		// 사용자 ID 가져오기 (없으면 null)
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("user_id");
		
		if (userId == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			out.print("로그인이 필요한 기능입니다.");
			return;
		}
		
		// 액션에 따라 처리
		if ("processBid".equals(action)) {
			processBid(productId, userId);
			response.setStatus(HttpServletResponse.SC_OK);
			out.print("낙찰 처리가 완료되었습니다.");
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("지원하지 않는 작업입니다.");
		}
	}
	
	// 낙찰 처리 메소드
	private void processBid(int productId, String userId) {
	    try {
	        BuyingProductDAO productDAO = new BuyingProductDAO();
	        BidDAO bidDAO = new BidDAO();
	        
	        // 최고 입찰 정보 가져오기
	        BidDTO highestBid = bidDAO.getHighestBid(productId);
	        
	        // 상품 정보 가져오기
	        ProductDTO product = productDAO.getProductById(productId);
	        
	        if (product == null) {
	            System.out.println("상품 정보를 찾을 수 없습니다: " + productId);
	            return;
	        }
	        
	        String sellerId = product.getSeller_id();
	        String productTitle = product.getTitle();
	        
	        if (highestBid != null) {
	            // 최고 입찰자가 있을 경우 낙찰 처리
	            boolean soldSuccessfully = productDAO.markAsSold(productId, highestBid.getBid_amount());
	            
	            if (soldSuccessfully) {
	                // 판매자와 구매자에게 알림 전송
	                NotificationDAO notificationDAO = new NotificationDAO();
	                
	                // 구매자에게 알림
	                String buyerMessage = productTitle + " 상품의 경매가 종료되었습니다. 귀하께서 " + 
	                                     highestBid.getBid_amount() + "원에 낙찰받으셨습니다.";
	                notificationDAO.addNotification(highestBid.getUser_id(), buyerMessage, "낙찰알림", productId);
	                
	                // 판매자에게 알림
	                String sellerMessage = "귀하의 " + productTitle + " 상품이 " + 
	                                     highestBid.getBid_amount() + "원에 낙찰되었습니다.";
	                notificationDAO.addNotification(sellerId, sellerMessage, "판매알림", productId);
	            } else {
	                // 낙찰 처리 실패 시 로그 기록
	                System.out.println("경매 낙찰 처리 실패: 상품 ID = " + productId);
	            }
	        } else {
	            // 입찰자가 없는 경우 상태만 변경
	            productDAO.updateProductStatus(productId, "경매종료");
	            
	            // 판매자에게 알림
	            NotificationDAO notificationDAO = new NotificationDAO();
	            String message = "귀하의 " + productTitle + " 상품 경매가 입찰자 없이 종료되었습니다.";
	            notificationDAO.addNotification(sellerId, message, "경매종료알림", productId);
	        }
	    } catch (Exception e) {
	        System.out.println("경매 처리 중 오류 발생: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
} 