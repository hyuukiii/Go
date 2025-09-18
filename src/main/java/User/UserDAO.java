package main.java.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Product.ProductDTO;

public class UserDAO {

	// 연결 정보
	String driver = "oracle.jdbc.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String user = "scott";
	String password = "tiger";

	public Connection dbcon() {
		Connection con = null;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	// 유저 정보 (조회) 매서드
	public UserDTO userInfo(String user_id) {
		Connection con = dbcon();
		PreparedStatement pst;
		String sql = "select * from Users where user_id = ?";
		UserDTO user = null;
		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, user_id);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				String id = rs.getString(1);
				String user_name = rs.getString(2);
				String email = rs.getString(3);
				String user_pw = rs.getString(4);
				String phone = rs.getString(5);
				String address = rs.getString(6);
				Date joinDate = rs.getDate(7);
				String profile_img = rs.getString(8);
				double rating = rs.getDouble(9);

				user = new UserDTO(id, user_name, email, user_pw, phone, address, joinDate, profile_img, rating);
			}
			rs.close();
			pst.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	// 유저 정보 변경
	public void updateUser(String user_id, String username, String email, String password, String phone,
			String address) {
		Connection con = dbcon();
		String sql = "UPDATE Users SET username = ?, email = ?, password = ?, phone = ?, address = ? WHERE user_id = ?";
		try {
			PreparedStatement pst = con.prepareStatement(sql);

			pst.setString(1, username);
			pst.setString(2, email);
			pst.setString(3, password);
			pst.setString(4, phone);
			pst.setString(5, address);
			pst.setString(6, user_id);

			pst.executeUpdate();

			pst.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 프로필 이미지 업뎃 / 삭제
	public void ProfileImage(String userId, String imgPath) {
		Connection con = dbcon();
		String sql = "update Users set profile_image = ? where user_id = ?";
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, imgPath);
			pst.setString(2, userId);

			pst.executeUpdate();

			pst.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 리뷰가 업데이트 되면 해당 seller_id 를 대상으로 평점 계산
	public void updateSellerRatings() {
		String sql = "UPDATE users u " + "SET u.rating = ( " + "    SELECT ROUND(AVG(r.rating), 1) "
				+ "    FROM reviews r " + "    WHERE r.seller_id = u.user_id " + ") " + "WHERE EXISTS ( "
				+ "    SELECT 1 FROM reviews r WHERE r.seller_id = u.user_id " + ")";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			int updatedRows = pst.executeUpdate();
			System.out.println("[updateSellerRatings] 업데이트된 사용자 수: " + updatedRows);
		} catch (SQLException e) {
			System.out.println("[updateSellerRatings] 오류 발생");
			e.printStackTrace();
		}
	}

	// -----------------------------------------마이페이지-----------------------------------------------

	private ProductDTO extractProductDTO(ResultSet rs) throws SQLException {
		ProductDTO dto = new ProductDTO();
		dto.setProduct_id(rs.getInt("product_id"));
		dto.setTitle(rs.getString("title"));
		dto.setDescription(rs.getString("description"));
		dto.setPrice(rs.getInt("price"));
		dto.setCategory_id(rs.getString("category_id"));
		dto.setSeller_id(rs.getString("seller_id"));
		dto.setStatus(rs.getString("status"));
		dto.setRegister_date(rs.getTimestamp("register_date"));
		dto.setView_count(rs.getInt("view_count"));
		dto.setMaxPrice(rs.getInt("maxprice"));
		dto.setMinPrice(rs.getInt("minprice"));
		dto.setAuction_end_time(rs.getTimestamp("auction_end_time"));
		dto.setImage_path(rs.getString("image_path"));
		return dto;
	}

	public ArrayList<ProductDTO> getBiddingProducts(String userId) {
		ArrayList<ProductDTO> list = new ArrayList<>();
		String sql = "SELECT p.* FROM Bids b " + "JOIN Products p ON b.product_id = p.product_id "
				+ "WHERE b.user_id = ? AND b.is_winning = 'N' AND p.status = '판매중' " + "ORDER BY p.register_date DESC";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				ProductDTO dto = extractProductDTO(rs);
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<ProductDTO> getWonProducts(String userId) {
		ArrayList<ProductDTO> list = new ArrayList<>();
		String sql = "SELECT p.* FROM Bids b " + "JOIN Products p ON b.product_id = p.product_id "
				+ "WHERE b.user_id = ? AND b.is_winning = 'Y' " + "AND p.status IN ('판매완료', '경매종료') "
				+ "ORDER BY p.register_date DESC";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				ProductDTO dto = extractProductDTO(rs);
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<ProductDTO> getSellingProducts(String userId) {
		ArrayList<ProductDTO> list = new ArrayList<>();
		String sql = "SELECT * FROM Products WHERE seller_id = ? AND status = '판매중' ORDER BY register_date DESC";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				ProductDTO dto = extractProductDTO(rs);
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<ProductDTO> getSoldProducts(String userId) {
		ArrayList<ProductDTO> list = new ArrayList<>();
		String sql = "SELECT p.* FROM Transactions t " + "JOIN Products p ON t.product_id = p.product_id "
				+ "WHERE t.seller_id = ? ORDER BY t.trans_date DESC";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				ProductDTO dto = extractProductDTO(rs);
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

}
