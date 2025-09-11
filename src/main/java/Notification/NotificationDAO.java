package Notification;

import java.sql.*;
import java.util.ArrayList;

public class NotificationDAO {

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

	// 새로운 알림 추가
	public void addNotification(String userId, String content, String notificationType, int productId) {
		String sql = "INSERT INTO Notifications (notification_id, user_id, content, notification_type, related_product_id, is_read, created_at) "
				+ "VALUES (notification_seq.NEXTVAL, ?, ?, ?, ?, 'N', SYSDATE)";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			pst.setString(2, content);
			pst.setString(3, notificationType);
			pst.setInt(4, productId);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 사용자의 미읽은 알림 개수 조회
	public int getUnreadNotificationCount(String userId) {
		String sql = "SELECT COUNT(*) FROM Notifications WHERE user_id = ? AND is_read = 'N'";
		int count = 0;

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

	// 사용자의 알림 목록 조회 (최신순)
	public ArrayList<NotificationDTO> getUserNotifications(String userId) {
		ArrayList<NotificationDTO> notifications = new ArrayList<>();
		String sql = "SELECT n.*, p.title as product_title FROM Notifications n "
				+ "LEFT JOIN Products p ON n.related_product_id = p.product_id "
				+ "WHERE n.user_id = ? ORDER BY n.created_at DESC";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				NotificationDTO notification = new NotificationDTO();
				notification.setNotification_id(rs.getInt("notification_id"));
				notification.setUser_id(rs.getString("user_id"));
				notification.setContent(rs.getString("content"));
				notification.setNotification_type(rs.getString("notification_type"));
				notification.setRelated_product_id(rs.getInt("related_product_id"));
				notification.setCreated_at(rs.getTimestamp("created_at"));
				notification.setIs_read(rs.getString("is_read"));
				notification.setProduct_title(rs.getString("product_title"));

				notifications.add(notification);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return notifications;
	}

	// 알림을 읽음 상태로 변경
	public void markAsRead(int notificationId) {
		String sql = "UPDATE Notifications SET is_read = 'Y' WHERE notification_id = ?";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, notificationId);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 사용자의 모든 알림을 읽음 상태로 변경
	public void markAllAsRead(String userId) {
		String sql = "UPDATE Notifications SET is_read = 'Y' WHERE user_id = ?";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, userId);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 특정 상품에 입찰한 모든 사용자의 ID 목록 조회
	public ArrayList<String> getProductBidders(int productId) {
		ArrayList<String> bidders = new ArrayList<>();
		String sql = "SELECT DISTINCT user_id FROM Bids WHERE product_id = ?";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, productId);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				bidders.add(rs.getString("user_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bidders;
	}

	// 특정 상품을 찜한 모든 사용자의 ID 목록 조회
	public ArrayList<String> getProductFavoriteUsers(int productId) {
		ArrayList<String> users = new ArrayList<>();
		String sql = "SELECT DISTINCT user_id FROM Favorites WHERE product_id = ?";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, productId);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				users.add(rs.getString("user_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return users;
	}
}