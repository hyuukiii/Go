package Ranking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// JNDI 관련 임포트 제거
// import javax.naming.Context;
// import javax.naming.InitialContext;
// import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Product.ProductDTO;

public class ProductRankingDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	// 데이터베이스 연결 메서드 - MySQL용
	public Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@localhost:1521:testdb";
			String user = "scott";
			String password = "tiger";
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	// 랭킹 계산 및 업데이트 (스케줄러에서 정기적으로 호출)
	public boolean calculateAndUpdateRankings() {
		boolean success = false;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);

			Map<Integer, Double> productScores = new HashMap<>();
			Map<Integer, Integer> previousRankings = new HashMap<>();

			boolean tableExists = false;
			try {
				String checkTableSQL = "SELECT COUNT(*) FROM user_tables WHERE table_name = 'PRODUCTRANKINGS'";
				pstmt = conn.prepareStatement(checkTableSQL);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					tableExists = rs.getInt(1) > 0;
				}
			} catch (Exception e) {
				System.out.println("ProductRankings 테이블 확인 중 오류: " + e.getMessage());
			}

			if (tableExists) {
				try {
					String getPrevRankSQL = "SELECT product_id, ranking FROM ProductRankings";
					pstmt = conn.prepareStatement(getPrevRankSQL);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						previousRankings.put(rs.getInt("product_id"), rs.getInt("ranking"));
					}
				} catch (Exception e) {
					System.out.println("기존 랭킹 정보 조회 중 오류: " + e.getMessage());
				}
			}

			try {
				String getDataSQL = "SELECT p.product_id, p.view_count, "
						+ "(SELECT COUNT(*) FROM Bids b WHERE b.product_id = p.product_id) AS bid_count, "
						+ "(SELECT COUNT(*) FROM Favorites f WHERE f.product_id = p.product_id) AS favorite_count "
						+ "FROM Products p WHERE p.status = '판매중'";

				pstmt = conn.prepareStatement(getDataSQL);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					int productId = rs.getInt("product_id");
					int viewCount = rs.getInt("view_count");
					int bidCount = rs.getInt("bid_count");
					int favoriteCount = rs.getInt("favorite_count");
					double score = viewCount * 1.0 + bidCount * 5.0 + favoriteCount * 3.0;
					productScores.put(productId, score);
				}
			} catch (Exception e) {
				System.out.println("상품 데이터 수집 중 오류: " + e.getMessage());
			}

			if (!tableExists) {
				try {
					String createTableSQL = "CREATE TABLE ProductRankings (" + "product_id NUMBER PRIMARY KEY, "
							+ "ranking NUMBER NOT NULL, " + "previous_ranking NUMBER DEFAULT 0, "
							+ "total_score NUMBER(10, 2) NOT NULL, " + "view_count NUMBER DEFAULT 0, "
							+ "bid_count NUMBER DEFAULT 0, " + "favorite_count NUMBER DEFAULT 0, "
							+ "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
					pstmt = conn.prepareStatement(createTableSQL);
					pstmt.executeUpdate();
					System.out.println("ProductRankings 테이블이 생성되었습니다.");
				} catch (Exception e) {
					System.out.println("ProductRankings 테이블 생성 중 오류: " + e.getMessage());
				}
			} else {
				try {
					String clearRankingsSQL = "DELETE FROM ProductRankings";
					pstmt = conn.prepareStatement(clearRankingsSQL);
					pstmt.executeUpdate();
				} catch (Exception e) {
					System.out.println("ProductRankings 테이블 비우기 중 오류: " + e.getMessage());
				}
			}

			ArrayList<Map.Entry<Integer, Double>> sortedProducts = new ArrayList<>(productScores.entrySet());
			sortedProducts.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

			String insertRankingSQL = "INSERT INTO ProductRankings "
					+ "(product_id, ranking, previous_ranking, total_score, view_count, bid_count, favorite_count, last_updated) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, SYSTIMESTAMP)";

			try {
				pstmt = conn.prepareStatement(insertRankingSQL);
				int rank = 1;
				for (Map.Entry<Integer, Double> entry : sortedProducts) {
					int productId = entry.getKey();
					double score = entry.getValue();

					String getProductDataSQL = "SELECT p.view_count, "
							+ "(SELECT COUNT(*) FROM Bids b WHERE b.product_id = ?) AS bid_count, "
							+ "(SELECT COUNT(*) FROM Favorites f WHERE f.product_id = ?) AS favorite_count "
							+ "FROM Products p WHERE p.product_id = ?";

					PreparedStatement detailPstmt = conn.prepareStatement(getProductDataSQL);
					detailPstmt.setInt(1, productId);
					detailPstmt.setInt(2, productId);
					detailPstmt.setInt(3, productId);
					ResultSet detailRs = detailPstmt.executeQuery();

					if (detailRs.next()) {
						pstmt.setInt(1, productId);
						pstmt.setInt(2, rank);
						pstmt.setInt(3, previousRankings.getOrDefault(productId, 0));
						pstmt.setDouble(4, score);
						pstmt.setInt(5, detailRs.getInt("view_count"));
						pstmt.setInt(6, detailRs.getInt("bid_count"));
						pstmt.setInt(7, detailRs.getInt("favorite_count"));
						pstmt.executeUpdate();
					}

					detailRs.close();
					detailPstmt.close();

					rank++;
				}
			} catch (Exception e) {
				System.out.println("랭킹 데이터 삽입 중 오류: " + e.getMessage());
			}

			conn.commit();
			success = true;

		} catch (Exception e) {
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			System.out.println("랭킹 계산 중 오류: " + e.getMessage());
			e.printStackTrace();
		} finally {
			closeResources();
		}
		return success;
	}

	// 상위 N개 랭킹 상품 조회
	public ArrayList<ProductDTO> getTopRankedProducts(int limit) {
		ArrayList<ProductDTO> rankedProducts = new ArrayList<>();

		try {
			conn = getConnection();
			String sql = "SELECT * FROM (" + "SELECT p.*, r.ranking, r.previous_ranking, r.total_score, "
					+ "r.view_count, r.bid_count, r.favorite_count, r.last_updated, " + "ROWNUM AS rnum "
					+ "FROM Products p " + "JOIN ProductRankings r ON p.product_id = r.product_id "
					+ "ORDER BY r.ranking ASC" + ") WHERE rnum <= ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, limit);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ProductDTO product = new ProductDTO();
				product.setProduct_id(rs.getInt("product_id"));
				product.setSeller_id(rs.getString("seller_id"));
				product.setTitle(rs.getString("title"));
				product.setDescription(rs.getString("description"));
				product.setPrice(rs.getInt("price"));
				product.setCategory_id(rs.getString("category_id"));
				product.setStatus(rs.getString("status"));
				product.setRegister_date(rs.getTimestamp("register_date"));
				product.setView_count(rs.getInt("view_count"));
				product.setMaxPrice(rs.getInt("maxPrice"));
				product.setMinPrice(rs.getInt("minPrice"));
				product.setAuction_end_time(rs.getTimestamp("auction_end_time"));
				product.setImage_path(rs.getString("image_path"));

				// 랭킹 정보 추가
				product.setRanking(rs.getInt("ranking"));
				product.setPrevious_ranking(rs.getInt("previous_ranking"));
				product.setRanking_score(rs.getDouble("total_score"));
				product.setBid_count(rs.getInt("bid_count"));
				product.setFavorite_count(rs.getInt("favorite_count"));
				product.setRanking_updated(rs.getTimestamp("last_updated"));

				rankedProducts.add(product);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}

		return rankedProducts;
	}

	// 특정 사용자를 위한 추천 상품 조회 (DTO 버전)
	public ArrayList<ProductDTO> getRecommendedProducts(String userId, int limit) {
		ArrayList<ProductDTO> recommendedProducts = new ArrayList<>();

		try {
			conn = getConnection();

			// 1. 사용자의 카테고리 선호도 계산
			String userPreferenceSQL = "SELECT c.category_id, COUNT(*) as interaction_count "
					+ "FROM UserProductInteractions u " + "JOIN Products p ON u.product_id = p.product_id "
					+ "JOIN Categories c ON p.category_id = c.category_id " + "WHERE u.user_id = ? "
					+ "GROUP BY c.category_id " + "ORDER BY interaction_count DESC";

			pstmt = conn.prepareStatement(userPreferenceSQL);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();

			ArrayList<String> preferredCategories = new ArrayList<>();
			while (rs.next()) {
				preferredCategories.add(rs.getString("category_id"));
			}

			// 2. 사용자가 이미 본/입찰한/찜한 상품 제외
			String viewedProductsSQL = "SELECT product_id FROM UserProductInteractions WHERE user_id = ?";
			pstmt = conn.prepareStatement(viewedProductsSQL);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();

			ArrayList<Integer> viewedProducts = new ArrayList<>();
			while (rs.next()) {
				viewedProducts.add(rs.getInt("product_id"));
			}

			// 3. 추천 상품 쿼리 구성
			StringBuilder recommendSQL = new StringBuilder();
			recommendSQL.append("SELECT * FROM (");
			recommendSQL.append("  SELECT inner_query.*, ROWNUM AS rnum FROM (");
			recommendSQL.append("    SELECT p.*, r.ranking, r.previous_ranking, r.total_score, ");
			recommendSQL.append("           r.view_count, r.bid_count, r.favorite_count, r.last_updated ");
			recommendSQL.append("    FROM Products p ");
			recommendSQL.append("    JOIN ProductRankings r ON p.product_id = r.product_id ");
			recommendSQL.append("    WHERE p.status = '판매중' ");

			// 3.1 이미 본 상품 제외
			if (!viewedProducts.isEmpty()) {
				recommendSQL.append("AND p.product_id NOT IN (");
				for (int i = 0; i < viewedProducts.size(); i++) {
					if (i > 0)
						recommendSQL.append(",");
					recommendSQL.append("?");
				}
				recommendSQL.append(") ");
			}

			// 3.2 선호 카테고리 우선 (있는 경우)
			if (!preferredCategories.isEmpty()) {
				recommendSQL.append("ORDER BY CASE p.category_id ");
				for (int i = 0; i < preferredCategories.size(); i++) {
					recommendSQL.append("WHEN ? THEN ").append(i).append(" ");
				}
				recommendSQL.append("ELSE 99 END, r.ranking ASC ");
			} else {
				recommendSQL.append("ORDER BY r.ranking ASC ");
			}

			recommendSQL.append("  ) inner_query WHERE ROWNUM <= ? ");
			recommendSQL.append(") WHERE rnum >= 1");

			pstmt = conn.prepareStatement(recommendSQL.toString());

			int paramIndex = 1;

			// 이미 본 상품 ID 바인딩
			for (Integer productId : viewedProducts) {
				pstmt.setInt(paramIndex++, productId);
			}

			// 선호 카테고리 바인딩
			for (String categoryId : preferredCategories) {
				pstmt.setString(paramIndex++, categoryId);
			}

			// 결과 제한 바인딩
			pstmt.setInt(paramIndex, limit);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ProductDTO product = new ProductDTO();
				product.setProduct_id(rs.getInt("product_id"));
				product.setSeller_id(rs.getString("seller_id"));
				product.setTitle(rs.getString("title"));
				product.setDescription(rs.getString("description"));
				product.setPrice(rs.getInt("price"));
				product.setCategory_id(rs.getString("category_id"));
				product.setStatus(rs.getString("status"));
				product.setRegister_date(rs.getTimestamp("register_date"));
				product.setView_count(rs.getInt("view_count"));
				product.setMaxPrice(rs.getInt("maxPrice"));
				product.setMinPrice(rs.getInt("minPrice"));
				product.setAuction_end_time(rs.getTimestamp("auction_end_time"));
				product.setImage_path(rs.getString("image_path"));

				product.setRanking(rs.getInt("ranking"));
				product.setPrevious_ranking(rs.getInt("previous_ranking"));
				product.setRanking_score(rs.getDouble("total_score"));
				product.setBid_count(rs.getInt("bid_count"));
				product.setFavorite_count(rs.getInt("favorite_count"));
				product.setRanking_updated(rs.getTimestamp("last_updated"));

				recommendedProducts.add(product);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}

		return recommendedProducts;
	}

	// 자원 해제
	private void closeResources() {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 인기 상품 랭킹 조회 (조회수 기준)
	@SuppressWarnings("unchecked")
	public JSONArray getPopularProductsByViews(int limit) {
		JSONArray productsArray = new JSONArray();

		try {
			conn = getConnection();

			// 먼저 status 값 확인 (디버깅 용도)
			String checkStatusSQL = "SELECT DISTINCT status FROM Products";
			PreparedStatement checkStmt = conn.prepareStatement(checkStatusSQL);
			ResultSet statusRs = checkStmt.executeQuery();
			System.out.println("데이터베이스에 있는 상품 상태 값들:");
			while (statusRs.next()) {
				System.out.println(" - " + statusRs.getString("status"));
			}
			statusRs.close();
			checkStmt.close();

			// Oracle에서는 LIMIT 대신 ROWNUM 사용
			String query = "SELECT * FROM (" + "SELECT p.*, "
					+ "(SELECT COUNT(*) FROM Bids b WHERE b.product_id = p.product_id) AS bid_count, "
					+ "(SELECT COUNT(*) FROM Favorites f WHERE f.product_id = p.product_id) AS favorite_count, "
					+ "(SELECT MAX(b.bid_amount) FROM Bids b WHERE b.product_id = p.product_id) AS current_bid "
					+ "FROM Products p " + "WHERE p.status = '판매중' " + "ORDER BY view_count DESC, register_date DESC"
					+ ") WHERE ROWNUM <= ?";

			System.out.println("인기 상품 조회 쿼리: " + query);

			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, limit);
			rs = pstmt.executeQuery();

			int ranking = 1; // 순위를 수동으로 계산

			while (rs.next()) {
				JSONObject product = new JSONObject();
				int productId = rs.getInt("product_id");
				String title = rs.getString("title");
				int price = rs.getInt("price");
				String status = rs.getString("status");

				// 현재 최고 입찰가 조회
				int currentBid = rs.getInt("current_bid");
				if (rs.wasNull()) {
					currentBid = 0; // 입찰이 없는 경우 0으로 설정
				}

				System.out.println(
						"인기 상품 ID: " + productId + ", 제목: " + title + ", 상태: " + status + ", 현재 입찰가: " + currentBid);

				product.put("product_id", productId);
				product.put("title", title);
				product.put("price", price);
				product.put("status", status);
				product.put("current_bid", currentBid);

				String imagePath = rs.getString("image_path");
				if (imagePath == null || imagePath.trim().isEmpty()) {
					imagePath = "images/default.jpg";
				}
				System.out.println("  - 이미지 경로: " + imagePath);
				product.put("image_path", imagePath);

				int viewCount = rs.getInt("view_count");
				int bidCount = rs.getInt("bid_count");
				int favoriteCount = rs.getInt("favorite_count");

				product.put("view_count", viewCount);
				product.put("bid_count", bidCount);
				product.put("favorite_count", favoriteCount);
				product.put("ranking", ranking++);

				int rankChange = (int) (Math.random() * 5) - 2;
				product.put("rank_change", rankChange);
				product.put("is_new", rs.getTimestamp("register_date").getTime() > System.currentTimeMillis()
						- (7L * 24 * 60 * 60 * 1000));

				productsArray.add(product);
			}

			System.out.println("총 " + productsArray.size() + "개의 인기 상품을 조회했습니다.");

			if (productsArray.isEmpty()) {
				System.out.println("조회된 인기 상품이 없습니다.");
			}
		} catch (SQLException e) {
			System.out.println("인기 상품 조회 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
		} finally {
			closeResources();
		}

		return productsArray;
	}

	// 인기 상품 랭킹 조회 (입찰 횟수 기준)
	@SuppressWarnings("unchecked")
	public JSONArray getPopularProductsByBids(int limit) {
		JSONArray productsArray = new JSONArray();

		try {
			System.out.println("입찰 기준 인기 상품 조회 시작");

			conn = getConnection();

			if (conn == null) {
				System.err.println("심각한 오류: 데이터베이스 연결 실패");
				return productsArray;
			}

			// Oracle에서는 LIMIT 대신 ROWNUM 사용
			String query = "SELECT * FROM (" + "SELECT p.*, "
					+ "(SELECT COUNT(*) FROM Bids b WHERE b.product_id = p.product_id) AS bid_count, "
					+ "(SELECT COUNT(*) FROM Favorites f WHERE f.product_id = p.product_id) AS favorite_count, "
					+ "(SELECT MAX(b.bid_amount) FROM Bids b WHERE b.product_id = p.product_id) AS current_bid "
					+ "FROM Products p " + "WHERE p.status = '판매중' " + "ORDER BY bid_count DESC, register_date DESC"
					+ ") WHERE ROWNUM <= ?";

			System.out.println("입찰 기준 인기 상품 조회 쿼리: " + query);

			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, limit);

			System.out.println("쿼리 실행: limit=" + limit);
			rs = pstmt.executeQuery();

			int ranking = 1;
			int itemCount = 0;

			while (rs.next()) {
				itemCount++;
				JSONObject product = new JSONObject();
				int productId = rs.getInt("product_id");
				String title = rs.getString("title");
				int price = rs.getInt("price");
				String status = rs.getString("status");

				int currentBid = rs.getInt("current_bid");
				if (rs.wasNull()) {
					currentBid = 0;
				}

				System.out.println(
						"입찰 인기 상품 ID: " + productId + ", 제목: " + title + ", 상태: " + status + ", 현재 입찰가: " + currentBid);

				product.put("product_id", productId);
				product.put("title", title);
				product.put("price", price);
				product.put("status", status);
				product.put("current_bid", currentBid);

				String imagePath = rs.getString("image_path");
				if (imagePath == null || imagePath.trim().isEmpty()) {
					imagePath = "images/default.jpg";
				}
				System.out.println("  - 이미지 경로: " + imagePath);
				product.put("image_path", imagePath);

				int viewCount = rs.getInt("view_count");
				int bidCount = rs.getInt("bid_count");
				int favoriteCount = rs.getInt("favorite_count");

				System.out.println("  - 조회수: " + viewCount + ", 입찰수: " + bidCount + ", 찜수: " + favoriteCount);

				product.put("view_count", viewCount);
				product.put("bid_count", bidCount);
				product.put("favorite_count", favoriteCount);
				product.put("ranking", ranking++);

				int rankChange = (int) (Math.random() * 5) - 2;
				product.put("rank_change", rankChange);
				product.put("is_new", rs.getTimestamp("register_date").getTime() > System.currentTimeMillis()
						- (7L * 24 * 60 * 60 * 1000));

				productsArray.add(product);
			}

			System.out.println("총 " + itemCount + "개의 입찰 인기 상품을 조회했습니다.");

			if (productsArray.isEmpty()) {
				System.out.println("조회된 입찰 인기 상품이 없습니다. Bids 테이블을 확인해주세요.");
				try {
					String checkBidsSQL = "SELECT COUNT(*) as bid_count FROM Bids";
					PreparedStatement checkStmt = conn.prepareStatement(checkBidsSQL);
					ResultSet bidsRs = checkStmt.executeQuery();
					if (bidsRs.next()) {
						int totalBids = bidsRs.getInt("bid_count");
						System.out.println("Bids 테이블에 총 " + totalBids + "개의 입찰 기록이 있습니다.");
					}
					bidsRs.close();
					checkStmt.close();
				} catch (Exception e) {
					System.err.println("Bids 테이블 확인 중 오류: " + e.getMessage());
				}
			}
		} catch (SQLException e) {
			System.err.println("입찰 기준 인기 상품 조회 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
		} finally {
			closeResources();
		}

		return productsArray;
	}

	// 인기 상품 랭킹 조회 (즐겨찾기 횟수 기준)
	@SuppressWarnings("unchecked")
	public JSONArray getPopularProductsByFavorites(int limit) {
		JSONArray productsArray = new JSONArray();

		try {
			conn = getConnection();

			String query = "SELECT * FROM (" + "SELECT p.*, "
					+ "(SELECT COUNT(*) FROM Bids b WHERE b.product_id = p.product_id) AS bid_count, "
					+ "(SELECT COUNT(*) FROM Favorites f WHERE f.product_id = p.product_id) AS favorite_count, "
					+ "(SELECT MAX(b.bid_amount) FROM Bids b WHERE b.product_id = p.product_id) AS current_bid "
					+ "FROM Products p " + "WHERE p.status = '판매중' "
					+ "ORDER BY favorite_count DESC, register_date DESC" + ") WHERE ROWNUM <= ?";

			System.out.println("즐겨찾기 기준 인기 상품 조회 쿼리: " + query);

			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, limit);
			rs = pstmt.executeQuery();

			int ranking = 1;

			while (rs.next()) {
				JSONObject product = new JSONObject();
				int productId = rs.getInt("product_id");
				String title = rs.getString("title");
				int price = rs.getInt("price");
				String status = rs.getString("status");

				int currentBid = rs.getInt("current_bid");
				if (rs.wasNull()) {
					currentBid = 0;
				}

				System.out.println("즐겨찾기 인기 상품 ID: " + productId + ", 제목: " + title + ", 상태: " + status + ", 현재 입찰가: "
						+ currentBid);

				product.put("product_id", productId);
				product.put("title", title);
				product.put("price", price);
				product.put("status", status);
				product.put("current_bid", currentBid);

				String imagePath = rs.getString("image_path");
				if (imagePath == null || imagePath.trim().isEmpty()) {
					imagePath = "images/default.jpg";
				}
				System.out.println("  - 이미지 경로: " + imagePath);
				product.put("image_path", imagePath);

				int viewCount = rs.getInt("view_count");
				int bidCount = rs.getInt("bid_count");
				int favoriteCount = rs.getInt("favorite_count");

				product.put("view_count", viewCount);
				product.put("bid_count", bidCount);
				product.put("favorite_count", favoriteCount);
				product.put("ranking", ranking++);

				int rankChange = (int) (Math.random() * 5) - 2;
				product.put("rank_change", rankChange);
				product.put("is_new", rs.getTimestamp("register_date").getTime() > System.currentTimeMillis()
						- (7L * 24 * 60 * 60 * 1000));

				productsArray.add(product);
			}

			System.out.println("총 " + productsArray.size() + "개의 즐겨찾기 인기 상품을 조회했습니다.");
		} catch (SQLException e) {
			System.out.println("즐겨찾기 인기 상품 조회 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
		} finally {
			closeResources();
		}

		return productsArray;
	}

	// 상품 조회수 증가
	public boolean incrementViewCount(int productId) {
		try {
			// dataFactory.getConnection() 대신 getConnection() 사용
			conn = getConnection();
			String query = "UPDATE Products SET view_count = view_count + 1 WHERE product_id = ?";

			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, productId);

			int result = pstmt.executeUpdate();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			closeResources();
		}
	}

	// 사용자 활동 기록 (조회, 입찰, 즐겨찾기)
	public boolean recordUserAction(String userId, int productId, String actionType) {
		try {
			// UserActions 테이블 존재 여부 확인
			boolean tableExists = false;
			conn = getConnection();

			try {
				String checkTableSQL = "SHOW TABLES LIKE 'UserActions'";
				pstmt = conn.prepareStatement(checkTableSQL);
				ResultSet rs = pstmt.executeQuery();
				tableExists = rs.next();
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				System.err.println("UserActions 테이블 확인 중 오류: " + e.getMessage());
			}

			// 테이블이 없으면 생성
			if (!tableExists) {
				try {
					System.out.println("UserActions 테이블이 존재하지 않아 생성합니다.");
					String createTableSQL = "CREATE TABLE UserActions (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
							+ "user_id VARCHAR(50) NOT NULL, " + "product_id INT NOT NULL, "
							+ "action_type VARCHAR(20) NOT NULL, " + "action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
					pstmt = conn.prepareStatement(createTableSQL);
					pstmt.executeUpdate();
					System.out.println("UserActions 테이블 생성 완료");
				} catch (SQLException e) {
					System.err.println("UserActions 테이블 생성 중 오류: " + e.getMessage());
					return false;
				}
			}

			// 사용자 활동 기록
			String query = "INSERT INTO UserActions (user_id, product_id, action_type, action_date) "
					+ "VALUES (?, ?, ?, NOW())";

			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, userId);
			pstmt.setInt(2, productId);
			pstmt.setString(3, actionType);

			int result = pstmt.executeUpdate();
			return result > 0;
		} catch (SQLException e) {
			System.err.println("사용자 활동 기록 중 오류: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			closeResources();
		}
	}

	// 추천 상품 목록 조회 (사용자 활동 기반, JSON 버전)
	@SuppressWarnings("unchecked")
	public JSONArray getJsonRecommendedProducts(String userId, int limit) {
		JSONArray productsArray = new JSONArray();

		try {
			// dataFactory.getConnection() 대신 getConnection() 사용
			conn = getConnection();
			// 사용자의 관심 카테고리 또는 태그 기반 추천 상품 조회 및 현재 최고 입찰가 함께 조회
			// MySQL에서는 ROWNUM 대신 별도의 변수 필요 없음
			String query = "SELECT p.*, "
					+ "(SELECT COUNT(*) FROM Bids b WHERE b.product_id = p.product_id) AS bid_count, "
					+ "(SELECT COUNT(*) FROM Favorites f WHERE f.product_id = p.product_id) AS favorite_count, "
					+ "(SELECT MAX(b.bid_amount) FROM Bids b WHERE b.product_id = p.product_id) AS current_bid "
					+ "FROM Products p WHERE p.status = '판매중' "
					+ "AND p.category_id IN (SELECT DISTINCT category_id FROM Products "
					+ "                      WHERE product_id IN (SELECT product_id FROM Favorites WHERE user_id = ?) "
					+ "                      OR product_id IN (SELECT product_id FROM Bids WHERE user_id = ?)) "
					+ "AND p.product_id NOT IN (SELECT product_id FROM Favorites WHERE user_id = ?) "
					+ "AND p.product_id NOT IN (SELECT product_id FROM Bids WHERE user_id = ?) "
					+ "ORDER BY view_count DESC, register_date DESC " + "LIMIT ?";

			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, userId);
			pstmt.setString(2, userId);
			pstmt.setString(3, userId);
			pstmt.setString(4, userId);
			pstmt.setInt(5, limit);

			rs = pstmt.executeQuery();

			int ranking = 1; // 랭킹 번호를 수동으로 설정

			while (rs.next()) {
				JSONObject product = new JSONObject();

				// 필수 데이터 추가
				int productId = rs.getInt("product_id");
				String title = rs.getString("title");
				int price = rs.getInt("price");

				// 현재 최고 입찰가 조회
				int currentBid = rs.getInt("current_bid");
				if (rs.wasNull()) {
					currentBid = 0; // 입찰이 없는 경우 0으로 설정
				}

				// 이미지 경로 관련 처리
				String imagePath = rs.getString("image_path");
				if (imagePath == null || imagePath.trim().isEmpty()) {
					imagePath = "images/default.jpg";
				}

				// 디버깅 정보 출력
				System.out.println("추천 상품 ID: " + productId + ", 제목: " + title + ", 이미지: " + imagePath + ", 현재 입찰가: "
						+ currentBid);

				product.put("product_id", productId);
				product.put("title", title);
				product.put("price", price);
				product.put("current_bid", currentBid); // 현재 최고 입찰가 추가
				product.put("image_path", imagePath);
				product.put("view_count", rs.getInt("view_count"));
				product.put("bid_count", rs.getInt("bid_count"));
				product.put("favorite_count", rs.getInt("favorite_count"));
				product.put("ranking", ranking++); // 랭킹 번호 할당 및 증가

				// 추천 신규 표시
				product.put("is_new", true);
				product.put("rank_change", 0);

				productsArray.add(product);
			}

			// 결과가 없으면 일반 인기 상품 목록 반환
			if (productsArray.isEmpty()) {
				System.out.println("추천 상품이 없어 인기 상품을 반환합니다.");
				return getPopularProductsByViews(limit);
			}

			System.out.println("총 " + productsArray.size() + "개의 추천 상품을 찾았습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("추천 상품 조회 중 오류 발생: " + e.getMessage());
		} finally {
			closeResources();
		}

		return productsArray;
	}
}