package Review;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReviewDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	public ReviewDAO() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@localhost:1521:testdb";
			String user = "scott";
			String password = "tiger";
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 리뷰 입력하면 DB에 전송
	public void insertReview(ReviewDTO review) {
		String sql = "INSERT INTO Reviews (review_id, reviewer_id, product_id, seller_id, description, rating, review_date) "
				+ "VALUES (review_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE)";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, review.getReviewerId());
			pstmt.setInt(2, review.getProductId());
			pstmt.setString(3, review.getSellerId());
			pstmt.setString(4, review.getDescription());
			pstmt.setDouble(5, review.getRating());

			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}
	}

	// 리뷰 추가 메소드
	public int addReview(String reviewerId, int productId, String sellerId, String description, double rating) {
		String sql = "INSERT INTO reviews (review_id, reviewer_id, product_id, seller_id, description, rating, review_date) "
				+ "VALUES (review_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE)";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, reviewerId);
			pstmt.setInt(2, productId);
			pstmt.setString(3, sellerId);
			pstmt.setString(4, description);
			pstmt.setDouble(5, rating);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}
		return -1; // 데이터베이스 오류
	}

	// 특정 상품의 리뷰 목록 조회
	public ArrayList<ReviewDTO> getReviewsByProductId(int productId) {
		ArrayList<ReviewDTO> reviewList = new ArrayList<>();
		String sql = "SELECT * FROM reviews WHERE product_id = ? ORDER BY review_date DESC";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, productId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ReviewDTO review = new ReviewDTO();
				review.setReviewId(rs.getInt("review_id"));
				review.setReviewerId(rs.getString("reviewer_id"));
				review.setProductId(rs.getInt("product_id"));
				review.setSellerId(rs.getString("seller_id"));
				review.setDescription(rs.getString("description"));
				review.setRating(rs.getDouble("rating"));
				review.setReviewDate(rs.getTimestamp("review_date"));
				reviewList.add(review);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}
		return reviewList;
	}

	// 특정 사용자의 리뷰 목록 조회
	public ArrayList<ReviewDTO> getReviewsByUser(String reviewerId) {
		ArrayList<ReviewDTO> reviewList = new ArrayList<>();
		String sql = "SELECT r.*, p.title as product_title FROM reviews r JOIN products p ON r.product_id = p.product_id WHERE r.reviewer_id = ? ORDER BY r.review_date DESC";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, reviewerId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ReviewDTO review = new ReviewDTO();
				review.setReviewId(rs.getInt("review_id"));
				review.setReviewerId(rs.getString("reviewer_id"));
				review.setProductId(rs.getInt("product_id"));
				review.setSellerId(rs.getString("seller_id"));
				review.setDescription(rs.getString("description"));
				review.setRating(rs.getDouble("rating"));
				review.setReviewDate(rs.getTimestamp("review_date"));
				review.setProductTitle(rs.getString("product_title"));
				reviewList.add(review);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}
		return reviewList;
	}

	// 리뷰 삭제 메소드
	public int deleteReview(int reviewId, String reviewerId) {
		String sql = "DELETE FROM reviews WHERE review_id = ? AND reviewer_id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, reviewId);
			pstmt.setString(2, reviewerId);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}
		return -1; // 데이터베이스 오류
	}

	// 상품 ID로 리뷰 삭제 메소드 (상품 삭제 시 호출)
	public int deleteReviewsByProductId(int productId) {
		String sql = "DELETE FROM reviews WHERE product_id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, productId);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}
		return -1; // 데이터베이스 오류
	}

	// 리소스 닫기
	private void closeResources() {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 특정 판매자가 받은 리뷰 목록 조회
	public ArrayList<ReviewDTO> getReceivedReviews(String sellerId) {
		ArrayList<ReviewDTO> reviewList = new ArrayList<>();
		String sql = "SELECT r.*, p.title as product_title FROM reviews r "
				+ "JOIN products p ON r.product_id = p.product_id "
				+ "WHERE r.seller_id = ? ORDER BY r.review_date DESC";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, sellerId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ReviewDTO review = new ReviewDTO();
				review.setReviewId(rs.getInt("review_id"));
				review.setReviewerId(rs.getString("reviewer_id"));
				review.setProductId(rs.getInt("product_id"));
				review.setSellerId(rs.getString("seller_id"));
				review.setDescription(rs.getString("description"));
				review.setRating(rs.getDouble("rating"));
				review.setReviewDate(rs.getTimestamp("review_date"));
				review.setProductTitle(rs.getString("product_title"));
				reviewList.add(review);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}
		return reviewList;
	}

	// 연결 종료
	public void close() {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
