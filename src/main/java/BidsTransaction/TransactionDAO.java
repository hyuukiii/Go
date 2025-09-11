package BidsTransaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDAO {

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

	public void insertTransaction(String buyerId, String sellerId, int productId, int price) {
		String sql = "INSERT INTO Transactions (trans_id, seller_id, buyer_id, product_id, trans_date, status, price, payment_method) "
				+ "VALUES (trans_seq.NEXTVAL, ?, ?, ?, SYSDATE, '입찰낙찰', ?, '입찰')";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, sellerId);
			pst.setString(2, buyerId);
			pst.setInt(3, productId);
			pst.setInt(4, price);
			pst.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 경매 종료 시 거래 내역 추가를 위한 메소드
	public boolean addTransaction(String sellerId, String buyerId, int productId, int price) {
		String sql = "INSERT INTO Transactions (trans_id, seller_id, buyer_id, product_id, trans_date, status, price, payment_method) "
				+ "VALUES (trans_seq.NEXTVAL, ?, ?, ?, SYSDATE, '경매낙찰', ?, '경매')";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, sellerId);
			pst.setString(2, buyerId);
			pst.setInt(3, productId);
			pst.setInt(4, price);

			int result = pst.executeUpdate();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
