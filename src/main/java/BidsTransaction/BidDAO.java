package BidsTransaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Product.ProductDTO;

public class BidDAO {

	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:testdb";
	String user = "scott";
	String password = "tiger";

	private Connection dbcon() {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	// 물품 구매창에서 가장 높은 입찰가(현재 입찰가)만을 보이게 하기 위한 코드
	public Integer getCurrentBid(int productId) {
		Integer bid = null;
		String sql = "SELECT MAX(bid_amount) AS max_bid FROM Bids WHERE product_id = ?";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setInt(1, productId);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				bid = rs.getInt("max_bid");
				if (rs.wasNull())
					bid = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bid;
	}

	// 입찰을 할 때마다 Bids테이블에 입찰 기록 추가하는 코드
	public void insertBid(String userId, int productId, int bidAmount) {
		String sql = "INSERT INTO Bids (bid_id, user_id, product_id, bid_amount, bid_time, is_winning) "
				+ "VALUES (bid_seq.NEXTVAL, ?, ?, ?, SYSDATE, 'N')";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			pst.setInt(2, productId);
			pst.setInt(3, bidAmount);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 사용자의 거래내역을 조회할 때 같은 물품코드라면 가장 후순위의 거래내역만 표시하는 코드
	public ArrayList<BidDTO> selectBidsByUser(String userId) {
		ArrayList<BidDTO> list = new ArrayList<>();
		String sql = "SELECT * FROM (\n" + "  SELECT b.*, p.title, p.seller_id,\n"
				+ "         ROW_NUMBER() OVER (PARTITION BY b.product_id ORDER BY b.bid_time DESC) AS rn\n"
				+ "  FROM Bids b\n" + "  JOIN Products p ON b.product_id = p.product_id\n" + "  WHERE b.user_id = ?\n"
				+ ") ranked WHERE rn = 1\n" + "";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				BidDTO bid = new BidDTO();
				bid.setBid_id(rs.getInt("bid_id"));
				bid.setUser_id(rs.getString("user_id"));
				bid.setProduct_id(rs.getInt("product_id"));
				bid.setBid_amount(rs.getInt("bid_amount"));
				bid.setBid_time(rs.getTimestamp("bid_time"));
				bid.setIs_winning(rs.getString("is_winning"));
				bid.setProduct_title(rs.getString("title"));
				bid.setSeller_id(rs.getString("seller_id"));

				list.add(bid);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 최대 가격으로 입찰하면 bids테이블에 is_winnig(입찰여부) 를 Y로 바꾸는 코드
	public void markWinningBid(int productId) {
		String sql = "UPDATE Bids SET is_winning = 'Y' " + "WHERE product_id = ? AND bid_amount = ("
				+ "  SELECT MAX(bid_amount) FROM Bids WHERE product_id = ?" + ")";

		String deleteFavoritesSql = "DELETE FROM Favorites WHERE product_id = ?";

		try (Connection con = dbcon()) {
			con.setAutoCommit(false);

			try (PreparedStatement pst = con.prepareStatement(sql);
					PreparedStatement del = con.prepareStatement(deleteFavoritesSql)) {

				pst.setInt(1, productId);
				pst.setInt(2, productId);
				pst.executeUpdate();

				del.setInt(1, productId);
				del.executeUpdate();

				con.commit();
			} catch (SQLException e) {
				con.rollback();
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 해당 유저가 낙찰한 상품 리스트를 조회하는 코드(리뷰쓰면 사라짐)
	public ArrayList<ProductDTO> selectWinningProducts(String userId) {
		ArrayList<ProductDTO> list = new ArrayList<>();

		// 개선된 쿼리: 각 상품의 최고 입찰가를 찾아 해당 사용자의 입찰이 최고 입찰가인 경우를 찾음
		String sql = "SELECT p.product_id, p.title, p.price, p.seller_id, p.image_path\n" + "FROM Products p\n"
				+ "JOIN (\n" + "    SELECT b.product_id, MAX(b.bid_amount) as max_bid\n" + "    FROM Bids b\n"
				+ "    GROUP BY b.product_id\n" + ") mb ON p.product_id = mb.product_id\n"
				+ "JOIN Bids b ON b.product_id = mb.product_id AND b.bid_amount = mb.max_bid AND b.user_id = ?\n"
				+ "LEFT JOIN Reviews r ON r.product_id = p.product_id AND r.reviewer_id = ?\n"
				+ "WHERE (p.status = '판매완료' OR p.status = '경매종료')\n" + "AND r.review_id IS NULL";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			pst.setString(2, userId);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				ProductDTO dto = new ProductDTO();
				dto.setProduct_id(rs.getInt("product_id"));
				dto.setTitle(rs.getString("title"));
				dto.setPrice(rs.getInt("price"));
				dto.setImage_path(rs.getString("image_path"));
				dto.setSeller_id(rs.getString("seller_id"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// 리뷰써도 안사라짐
	public ArrayList<ProductDTO> selectAllWinningProducts(String userId) {
		ArrayList<ProductDTO> list = new ArrayList<>();

		// 개선된 쿼리: is_winning 조건 제거 및 서브쿼리로 최고 입찰가를 찾음
		String sql = "SELECT p.product_id, p.title, p.price, p.image_path, p.seller_id " + "FROM Products p "
				+ "JOIN (\n" + "    SELECT b.product_id, MAX(b.bid_amount) as max_bid\n" + "    FROM Bids b\n"
				+ "    GROUP BY b.product_id\n" + ") mb ON p.product_id = mb.product_id\n"
				+ "JOIN Bids b ON b.product_id = mb.product_id AND b.bid_amount = mb.max_bid AND b.user_id = ?\n"
				+ "WHERE (p.status = '판매완료' OR p.status = '경매종료') " + "ORDER BY p.register_date DESC";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				ProductDTO dto = new ProductDTO();
				dto.setProduct_id(rs.getInt("product_id"));
				dto.setTitle(rs.getString("title"));
				dto.setPrice(rs.getInt("price"));
				dto.setImage_path(rs.getString("image_path"));
				dto.setSeller_id(rs.getString("seller_id"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// 특정 상품의 최고 입찰 정보를 가져오는 메소드
	public BidDTO getHighestBid(int productId) {
		BidDTO bid = null;
		String sql = "SELECT * FROM (SELECT * FROM Bids WHERE product_id = ? ORDER BY bid_amount DESC) WHERE ROWNUM = 1";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, productId);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				bid = new BidDTO();
				bid.setBid_id(rs.getInt("bid_id"));
				bid.setUser_id(rs.getString("user_id"));
				bid.setProduct_id(rs.getInt("product_id"));
				bid.setBid_amount(rs.getInt("bid_amount"));
				bid.setBid_time(rs.getTimestamp("bid_time"));
				bid.setIs_winning(rs.getString("is_winning"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bid;
	}

	// 특정 입찰을 낙찰로 표시하는 메소드
	public boolean markAsWinning(int bidId) {
		String sql = "UPDATE Bids SET is_winning = 'Y' WHERE bid_id = ?";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, bidId);
			int result = pst.executeUpdate();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 특정 상품의 입찰 내역 조회 (최신순으로 정렬)
	public ArrayList<BidDTO> getBidHistoryByProductId(int productId) {
		ArrayList<BidDTO> bidHistory = new ArrayList<>();

		String sql = "SELECT b.*, u.username " + "FROM Bids b " + "JOIN Users u ON b.user_id = u.user_id "
				+ "WHERE b.product_id = ? " + "ORDER BY b.bid_time DESC";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setInt(1, productId);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				BidDTO bid = new BidDTO();
				bid.setBid_id(rs.getInt("bid_id"));
				bid.setUser_id(rs.getString("user_id"));
				bid.setProduct_id(rs.getInt("product_id"));
				bid.setBid_amount(rs.getInt("bid_amount"));
				bid.setBid_time(rs.getTimestamp("bid_time"));
				bid.setIs_winning(rs.getString("is_winning"));
				bid.setUsername(rs.getString("username")); // 사용자 이름 설정

				bidHistory.add(bid);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bidHistory;
	}

	// 최고 입찰가를 가진 입찰을 is_winning = 'Y'로 설정하는 메소드 추가
	public boolean setWinningBid(int productId) {
		// 최고 입찰가 찾기
		String findMaxBidSql = "SELECT MAX(bid_amount) as max_bid FROM Bids WHERE product_id = ?";
		// 최고 입찰가 레코드 업데이트
		String updateWinningSql = "UPDATE Bids SET is_winning = 'Y' WHERE product_id = ? AND bid_amount = ?";

		try (Connection con = dbcon()) {
			// 자동 커밋 비활성화
			con.setAutoCommit(false);

			// 최고 입찰가 찾기
			try (PreparedStatement pstFind = con.prepareStatement(findMaxBidSql)) {
				pstFind.setInt(1, productId);
				ResultSet rs = pstFind.executeQuery();

				if (rs.next()) {
					int maxBid = rs.getInt("max_bid");

					// 최고 입찰가가 0보다 큰 경우에만 업데이트
					if (maxBid > 0) {
						try (PreparedStatement pstUpdate = con.prepareStatement(updateWinningSql)) {
							pstUpdate.setInt(1, productId);
							pstUpdate.setInt(2, maxBid);
							pstUpdate.executeUpdate();
						}
					}
				}
			}

			// 커밋
			con.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
