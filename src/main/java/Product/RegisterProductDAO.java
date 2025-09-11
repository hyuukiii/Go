package Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RegisterProductDAO {

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

	// 상품 추가
	public void insertProduct(ProductDTO product) {
		String sql = "INSERT INTO Products (product_id, seller_id, title, description, price, category_id, status, register_date, view_count, maxprice, minprice, auction_end_time, image_path) "
				+ "VALUES (product_seq.NEXTVAL, ?, ?, ?, 0, ?, '판매중', SYSDATE, 0, ?, ?, ?, ?)";

		try (Connection conn = dbcon(); PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setString(1, product.getSeller_id());
			pst.setString(2, product.getTitle());
			pst.setString(3, product.getDescription());
			pst.setString(4, product.getCategory_id());
			pst.setInt(5, product.getMaxPrice());
			pst.setInt(6, product.getMinPrice());
			pst.setTimestamp(7, new Timestamp(product.getAuction_end_time().getTime()));
			pst.setString(8, product.getImage_path());

			pst.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}