package Ranking;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Product.FavoriteDAO;

@WebServlet("/product/rankings")
public class ProductRankingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		try {
			// 랭킹 유형 파라미터 (기본값: views - 조회순)
			String type = request.getParameter("type");
			System.out.println("요청된 랭킹 유형 파라미터: [" + type + "]");

			if (type == null || type.trim().isEmpty()) {
				type = "views";
				System.out.println("유형 파라미터 비어있음, 기본값 'views'로 설정");
			}

			// 결과 개수 제한 (기본값: 10)
			int limit = 10;
			try {
				String limitParam = request.getParameter("limit");
				if (limitParam != null && !limitParam.trim().isEmpty()) {
					limit = Integer.parseInt(limitParam);
				}
			} catch (NumberFormatException e) {
				// 숫자 형식이 아닌 경우 기본값 사용
				limit = 10;
			}

			// 세션에서 사용자 ID 가져오기 (로그인한 경우)
			HttpSession session = request.getSession(false);
			String userId = null;
			if (session != null) {
				userId = (String) session.getAttribute("user_id");
			}

			// 랭킹 데이터 가져오기
			ProductRankingDAO rankingDAO = new ProductRankingDAO();
			JSONArray jsonResponse = new JSONArray();

			// 요청 타입에 따라 다른 랭킹 데이터 조회
			System.out.println("랭킹 데이터 조회 시작, 유형: " + type);

			if ("bids".equals(type)) {
				// 입찰 횟수 기준 인기 상품
				System.out.println("입찰 횟수 기준 데이터 요청");
				jsonResponse = rankingDAO.getPopularProductsByBids(limit);
			} else if ("favorites".equals(type)) {
				// 즐겨찾기 횟수 기준 인기 상품
				System.out.println("즐겨찾기 기준 데이터 요청");
				jsonResponse = rankingDAO.getPopularProductsByFavorites(limit);
			} else if ("recommended".equals(type)) {
				// 사용자 맞춤 추천 상품
				if (userId != null) {
					System.out.println("사용자 " + userId + "의 추천 상품 요청");
					jsonResponse = rankingDAO.getJsonRecommendedProducts(userId, limit);
				} else {
					// 로그인하지 않은 경우 일반 인기 상품 제공
					System.out.println("비로그인 사용자의 인기 상품 요청 (추천 대체)");
					jsonResponse = rankingDAO.getPopularProductsByViews(limit);
				}
			} else {
				// 기본: 조회수 기준 인기 상품
				System.out.println("조회수 기준 인기 상품 요청");
				jsonResponse = rankingDAO.getPopularProductsByViews(limit);
			}

			// 디버그 로그 추가
			System.out.println("랭킹 데이터 반환 (" + type + ") - 항목 수: " + jsonResponse.size());
			if (jsonResponse.size() > 0) {
				Object firstItem = jsonResponse.get(0);
				System.out.println("첫 번째 항목 샘플: " + firstItem.toString());
			} else {
				System.out.println("경고: 반환된 랭킹 데이터 없음");
			}

			// JSON 응답 전송
			PrintWriter out = response.getWriter();
			out.print(jsonResponse);
			out.flush();

		} catch (Exception e) {
			System.err.println("랭킹 조회 중 예외 발생: " + e.getMessage());
			e.printStackTrace();

			// 에러 응답 생성
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			JSONObject errorResponse = new JSONObject();
			errorResponse.put("error", "랭킹 데이터 조회 중 오류가 발생했습니다: " + e.getMessage());

			PrintWriter out = response.getWriter();
			out.print(errorResponse.toJSONString());
			out.flush();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 로그인 확인
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("user_id");

		if (userId == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			JSONObject errorResponse = new JSONObject();
			errorResponse.put("error", "로그인이 필요합니다.");

			PrintWriter out = response.getWriter();
			out.print(errorResponse.toJSONString());
			out.flush();
			return;
		}

		// 요청 파라미터 추출
		String action = request.getParameter("action");
		int productId;

		try {
			productId = Integer.parseInt(request.getParameter("product_id"));
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			JSONObject errorResponse = new JSONObject();
			errorResponse.put("error", "올바른 상품 ID가 필요합니다.");

			PrintWriter out = response.getWriter();
			out.print(errorResponse.toJSONString());
			out.flush();
			return;
		}

		// 응답 헤더 설정
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// 액션 실행
		ProductRankingDAO rankingDAO = new ProductRankingDAO();
		boolean success = false;

		if ("view".equals(action)) {
			// 조회 액션
			success = rankingDAO.incrementViewCount(productId);
			rankingDAO.recordUserAction(userId, productId, "view");
		} else if ("favorite".equals(action)) {
			// 즐겨찾기 추가 액션
			FavoriteDAO favoriteDAO = new FavoriteDAO();
			success = favoriteDAO.addFavorite(userId, productId);
			if (success) {
				rankingDAO.recordUserAction(userId, productId, "favorite");
			}
		} else if ("unfavorite".equals(action)) {
			// 즐겨찾기 삭제 액션
			FavoriteDAO favoriteDAO = new FavoriteDAO();
			success = favoriteDAO.removeFavorite(userId, productId);
			if (success) {
				rankingDAO.recordUserAction(userId, productId, "unfavorite");
			}
		}

		// 응답 생성
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("success", success);
		jsonResponse.put("action", action);
		jsonResponse.put("product_id", productId);

		PrintWriter out = response.getWriter();
		out.print(jsonResponse.toJSONString());
		out.flush();
	}
}