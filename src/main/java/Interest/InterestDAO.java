package main.java.Interest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Product.ProductDTO;

public class InterestDAO {

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

	// 찜 상태 체크
	public boolean isInteresting(String userId, int productId) {

		Connection con = dbcon();

		String sql = "select 1 from favorites where user_id = ? and product_id = ?";

		try {
			PreparedStatement pst = con.prepareStatement(sql);

			pst.setString(1, userId);
			pst.setInt(2, productId);

			ResultSet rs = pst.executeQuery();

			return rs.next();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	// 찜 추가
	public void addInterest(String userId, int productId) {

		Connection con = dbcon();

		String sql = "insert into Favorites (favorite_id, user_id, product_id, favorite_date) values (favorite_seq.NEXTVAL, ?, ?, SYSDATE)";

		try {
			PreparedStatement pst = con.prepareStatement(sql);

			pst.setString(1, userId);
			pst.setInt(2, productId);

			pst.executeUpdate();

			pst.close();
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 찜 삭제
	public void deleteInterest(String userId, int productId) {

		Connection con = dbcon();

		String sql = "delete from Favorites where user_id = ? and product_id = ?";

		try {
			PreparedStatement pst = con.prepareStatement(sql);

			pst.setString(1, userId);
			pst.setInt(2, productId);

			pst.executeUpdate();

			pst.close();
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 찜 조회
	public ArrayList<ProductDTO> viewInterest(String userId) {

		ArrayList<ProductDTO> list = new ArrayList<>();

		Connection con = dbcon();

		String sql = "SELECT p.product_id, p.title, NVL(MAX(b.bid_amount), 0) AS current_price,\r\n"
				+ "       p.image_path, p.register_date, u.username AS seller_name\r\n" + "FROM Products p\r\n"
				+ "JOIN Favorites f ON p.product_id = f.product_id\r\n"
				+ "LEFT JOIN Bids b ON p.product_id = b.product_id\r\n" + "JOIN Users u ON p.seller_id = u.user_id\r\n"
				+ "WHERE f.user_id = ?\r\n"
				+ "GROUP BY p.product_id, p.title, p.image_path, p.register_date, u.username\r\n"
				+ "ORDER BY p.register_date DESC\r\n" + "";

		try {
			PreparedStatement pst = con.prepareStatement(sql);

			pst.setString(1, userId);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				ProductDTO dto = new ProductDTO();
				dto.setProduct_id(rs.getInt("product_id"));
				dto.setTitle(rs.getString("title"));
				dto.setPrice(rs.getInt("current_price"));
				dto.setImage_path(rs.getString("image_path"));
				dto.setSeller_id(rs.getString("seller_name"));

				list.add(dto);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}
}
