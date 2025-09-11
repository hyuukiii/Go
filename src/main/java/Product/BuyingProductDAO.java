package Product;

import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BuyingProductDAO {

	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:testdb";
	String user = "scott";
	String password = "tiger";

	public Connection dbcon() {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	// 상품 구매 폼에서 해당 물품의 상세정보를 보여주는 코드
	public ProductDTO selectProductById(int productId) {
		ProductDTO dto = null;

		String sql = "SELECT p.*, c.category_name " + "FROM Products p "
				+ "JOIN Categories c ON p.category_id = c.category_id " + "WHERE p.product_id = ?";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, productId);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				dto = new ProductDTO();
				dto.setProduct_id(rs.getInt("product_id"));
				dto.setSeller_id(rs.getString("seller_id"));
				dto.setTitle(rs.getString("title"));
				dto.setDescription(rs.getString("description"));
				dto.setCategory_id(rs.getString("category_id"));
				dto.setCategory_name(rs.getString("category_name"));
				dto.setPrice(rs.getInt("price"));
				dto.setStatus(rs.getString("status"));
				dto.setView_count(rs.getInt("view_count"));
				dto.setMaxPrice(rs.getInt("maxPrice"));
				dto.setMinPrice(rs.getInt("minPrice"));
				dto.setAuction_end_time(rs.getTimestamp("auction_end_time"));
				dto.setImage_path(rs.getString("image_path")); // ✅ 이미지 경로 추가
				dto.setRegister_date(rs.getTimestamp("register_date")); // 등록 날짜 추가
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dto;
	}

	// 메인에서 현재 판매 중인 상품들만 보여주는 코드
	public ArrayList<ProductDTO> selectAvailableProducts() {
		ArrayList<ProductDTO> list = new ArrayList<>();
		String sql = "SELECT p.product_id, p.title, NVL(MAX(b.bid_amount), 0) AS current_price, p.image_path "
				+ "FROM Products p LEFT JOIN Bids b ON p.product_id = b.product_id " + "WHERE p.status = '판매중' "
				+ "GROUP BY p.product_id, p.title, p.image_path, p.register_date " + "ORDER BY p.register_date DESC";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				ProductDTO dto = new ProductDTO();
				dto.setProduct_id(rs.getInt("product_id"));
				dto.setTitle(rs.getString("title"));
				dto.setPrice(rs.getInt("current_price"));
				dto.setImage_path(rs.getString("image_path"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 인기순(조회수 기준)으로 판매 중인 상품들을 보여주는 코드
	public ArrayList<ProductDTO> selectPopularProducts() {
		ArrayList<ProductDTO> list = new ArrayList<>();
		String sql = "SELECT p.product_id, p.title, NVL(MAX(b.bid_amount), 0) AS current_price, p.image_path, p.view_count\n"
				+ "FROM Products p\n" + "LEFT JOIN Bids b ON p.product_id = b.product_id\n" + "WHERE p.status = '판매중'\n"
				+ "GROUP BY p.product_id, p.title, p.image_path, p.view_count\n" + "ORDER BY p.view_count DESC\n" + "";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				ProductDTO dto = new ProductDTO();
				dto.setProduct_id(rs.getInt("product_id"));
				dto.setTitle(rs.getString("title"));
				dto.setPrice(rs.getInt("current_price"));
				dto.setImage_path(rs.getString("image_path"));
				dto.setView_count(rs.getInt("view_count"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 경매 종료된 상품들을 보여주는 코드
	public ArrayList<ProductDTO> selectCompletedAuctions() {
		ArrayList<ProductDTO> list = new ArrayList<>();
		String sql = "SELECT p.product_id, p.title, p.price, p.image_path, p.view_count, p.status " + "FROM Products p "
				+ "WHERE p.status = '경매종료' " + "ORDER BY p.register_date DESC";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				ProductDTO dto = new ProductDTO();
				dto.setProduct_id(rs.getInt("product_id"));
				dto.setTitle(rs.getString("title"));
				dto.setPrice(rs.getInt("price"));
				dto.setImage_path(rs.getString("image_path"));
				dto.setView_count(rs.getInt("view_count"));
				dto.setStatus(rs.getString("status"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 로그인된 사용자의 찜 목록 상품 불러오기 (Oracle 버전)
	public ArrayList<ProductDTO> selectFavoriteProducts(String userId) {
		ArrayList<ProductDTO> list = new ArrayList<>();
		String sql = "SELECT p.product_id, p.title, NVL(MAX(b.bid_amount), 0) AS current_price, p.image_path "
				+ "FROM Products p " + "JOIN Favorites f ON p.product_id = f.product_id "
				+ "LEFT JOIN Bids b ON p.product_id = b.product_id " + "WHERE f.user_id = ? "
				+ "GROUP BY p.product_id, p.title, p.image_path, p.register_date " + "ORDER BY p.register_date DESC";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				ProductDTO dto = new ProductDTO();
				dto.setProduct_id(rs.getInt("product_id"));
				dto.setTitle(rs.getString("title"));
				dto.setPrice(rs.getInt("current_price"));
				dto.setImage_path(rs.getString("image_path"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 입찰에 필요한 정보들만 가져오는 코드
	public ProductDTO getProductAuctionInfo(int productId) {
		ProductDTO dto = null;

		String sql = "SELECT seller_id, minPrice, maxPrice, auction_end_time, status FROM Products WHERE product_id = ?";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, productId);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				dto = new ProductDTO();
				dto.setSeller_id(rs.getString("seller_id"));
				dto.setMinPrice(rs.getInt("minPrice"));
				dto.setMaxPrice(rs.getInt("maxPrice"));
				dto.setAuction_end_time(rs.getTimestamp("auction_end_time"));
				dto.setStatus(rs.getString("status"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dto;
	}

	// 상품이 낙찰되면 상태(status)를 경매종료로 변경하고 낙찰된 가격을 반영해주는 코드
	public boolean markAsSold(int productId, int finalPrice) {
		String sql = "UPDATE Products SET status = '경매종료', price = ? WHERE product_id = ?";
		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, finalPrice);
			pst.setInt(2, productId);
			int result = pst.executeUpdate();

			// 경매 종료 시 찜 목록에서 삭제
			removeFavoritesByProductId(productId);

			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 상품이 판매완료되면 해당 상품을 찜한 기록 삭제
	public void removeFavoritesByProductId(int productId) {
		String sql = "DELETE FROM Favorites WHERE product_id = ?";
		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, productId);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 사용자들이 물품에 들어가서 보면 조회수 증가 코드
	public void incrementViewCount(int productId) {
		String sql = "UPDATE Products SET view_count = view_count + 1 WHERE product_id = ?";
		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, productId);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ProductDTO getProductById(int productId) {
		ProductDTO product = null;
		String sql = "SELECT * FROM Products WHERE product_id = ?";
		try (Connection conn = dbcon(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, productId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				product = new ProductDTO();
				product.setProduct_id(rs.getInt("product_id"));
				product.setTitle(rs.getString("title"));
				product.setDescription(rs.getString("description"));
				product.setSeller_id(rs.getString("seller_id"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return product;
	}

	// 사용자가 등록한 상품을 삭제하는 메소드
	public boolean deleteProduct(int productId, String sellerId) {
		// 먼저 해당 상품이 판매자의 것인지 확인
		String checkSql = "SELECT seller_id, status FROM Products WHERE product_id = ?";
		String deleteNotificationsSql = "DELETE FROM Notifications WHERE related_product_id = ?";
		String deleteReviewsSql = "DELETE FROM Reviews WHERE product_id = ?";
		String deleteTransactionsSql = "DELETE FROM Transactions WHERE product_id = ?";
		String deleteBidsSql = "DELETE FROM Bids WHERE product_id = ?";
		String deleteFavoritesSql = "DELETE FROM Favorites WHERE product_id = ?";
		String deleteProductSql = "DELETE FROM Products WHERE product_id = ? AND seller_id = ?";

		try (Connection con = dbcon()) {
			// 자동 커밋 비활성화로 트랜잭션 시작
			con.setAutoCommit(false);

			// 먼저 상품 소유자 확인
			try (PreparedStatement checkPst = con.prepareStatement(checkSql)) {
				checkPst.setInt(1, productId);
				ResultSet rs = checkPst.executeQuery();

				if (rs.next()) {
					String dbSellerId = rs.getString("seller_id");
					// 로그인한 사용자와 판매자가 일치하지 않으면 삭제 불가
					if (!dbSellerId.equals(sellerId)) {
						con.rollback();
						return false;
					}
				} else {
					// 상품이 존재하지 않음
					con.rollback();
					return false;
				}
			}

			// 관련 알림 삭제
			try (PreparedStatement deleteNotificationsPst = con.prepareStatement(deleteNotificationsSql)) {
				deleteNotificationsPst.setInt(1, productId);
				deleteNotificationsPst.executeUpdate();
			}

			// 관련 리뷰 삭제
			try (PreparedStatement deleteReviewsPst = con.prepareStatement(deleteReviewsSql)) {
				deleteReviewsPst.setInt(1, productId);
				deleteReviewsPst.executeUpdate();
			}

			// 관련 거래기록 삭제
			try (PreparedStatement deleteTransactionsPst = con.prepareStatement(deleteTransactionsSql)) {
				deleteTransactionsPst.setInt(1, productId);
				deleteTransactionsPst.executeUpdate();
			}

			// 관련 입찰 기록 삭제
			try (PreparedStatement deleteBidsPst = con.prepareStatement(deleteBidsSql)) {
				deleteBidsPst.setInt(1, productId);
				deleteBidsPst.executeUpdate();
			}

			// 관련 찜 기록 삭제
			try (PreparedStatement deleteFavoritesPst = con.prepareStatement(deleteFavoritesSql)) {
				deleteFavoritesPst.setInt(1, productId);
				deleteFavoritesPst.executeUpdate();
			}

			// 상품 삭제
			try (PreparedStatement deleteProductPst = con.prepareStatement(deleteProductSql)) {
				deleteProductPst.setInt(1, productId);
				deleteProductPst.setString(2, sellerId);
				int result = deleteProductPst.executeUpdate();

				// 모든 작업이 성공하면 커밋
				if (result > 0) {
					con.commit();
					return true;
				} else {
					con.rollback();
					return false;
				}
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 상품의 상태만 업데이트
	public boolean updateProductStatus(int productId, String status) {
		String sql = "UPDATE Products SET status = ? WHERE product_id = ?";
		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, status);
			pst.setInt(2, productId);
			int result = pst.executeUpdate();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
