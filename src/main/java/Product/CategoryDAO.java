package Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryDAO {
	
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

	// 모든 카테고리를 가져오는 메소드
	public ArrayList<CategoryDTO> getAllCategories() {
		ArrayList<CategoryDTO> list = new ArrayList<>();
		String sql = "SELECT * FROM Categories ORDER BY display_order";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				CategoryDTO dto = new CategoryDTO();
				dto.setCategoryId(rs.getString("category_id"));
				dto.setCategoryName(rs.getString("category_name"));
				dto.setDisplayOrder(rs.getInt("display_order"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// 카테고리 ID로 카테고리 정보를 가져오는 메소드
	public CategoryDTO getCategoryById(String categoryId) {
		CategoryDTO dto = null;
		String sql = "SELECT * FROM Categories WHERE category_id = ?";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setString(1, categoryId);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				dto = new CategoryDTO();
				dto.setCategoryId(rs.getString("category_id"));
				dto.setCategoryName(rs.getString("category_name"));
				dto.setDisplayOrder(rs.getInt("display_order"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dto;
	}
}