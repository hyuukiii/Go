package main.java.Search;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Product.ProductDTO;

public class SearchDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	public Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String user = "scott";
			String password = "tiger";
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public ArrayList<main.java.Product.ProductDTO> searchProducts(String keyword, String category, String price, String status,
																  String sort, int page, int itemsPerPage) {
		ArrayList<ProductDTO> searchResults = new ArrayList<>();

		try {
			conn = getConnection();
			StringBuilder sql = new StringBuilder();

			sql.append("SELECT * FROM (");
			sql.append("  SELECT inner_query.*, ROWNUM AS rnum FROM (");
			sql.append(
					"    SELECT p.product_id, p.seller_id, p.title, p.price, p.category_id, p.status, p.register_date, ");
			sql.append(
					"           p.view_count, p.maxPrice, p.minPrice, p.auction_end_time, p.image_path, c.category_name, ");
			sql.append(
					"           COALESCE(MAX(b.bid_amount), p.price) as current_price, COUNT(DISTINCT b.bid_id) as bid_count ");
			sql.append("    FROM Products p ");
			sql.append("    LEFT JOIN Categories c ON p.category_id = c.category_id ");
			sql.append("    LEFT JOIN Bids b ON p.product_id = b.product_id ");
			sql.append("    WHERE 1=1 ");

			if (keyword != null && !keyword.trim().isEmpty()) {
				sql.append("AND (p.title LIKE ? OR p.description LIKE ?) ");
			}

			if (category != null && !category.equals("all") && !category.trim().isEmpty()) {
				sql.append("AND c.category_name = ? ");
			}

			if (status != null && !status.trim().isEmpty()) {
				if (status.equals("onsale")) {
					sql.append("AND p.status = '판매중' ");
				} else if (status.equals("closed")) {
					sql.append("AND p.status = '경매종료' ");
				}
			}

			sql.append("GROUP BY p.product_id, p.seller_id, p.title, p.price, p.category_id, p.status, ");
			sql.append(
					"p.register_date, p.view_count, p.maxPrice, p.minPrice, p.auction_end_time, p.image_path, c.category_name ");

			if (sort != null && !sort.trim().isEmpty()) {
				switch (sort) {
				case "priceAsc":
					sql.append("ORDER BY current_price ASC ");
					break;
				case "priceDesc":
					sql.append("ORDER BY current_price DESC ");
					break;
				case "newest":
					sql.append("ORDER BY p.register_date DESC ");
					break;
				case "endingSoon":
					sql.append("ORDER BY p.auction_end_time ASC ");
					break;
				default:
					sql.append("ORDER BY p.register_date DESC ");
				}
			} else {
				sql.append("ORDER BY p.register_date DESC ");
			}

			sql.append("  ) inner_query WHERE ROWNUM <= ? ");
			sql.append(") WHERE rnum >= ?");

			pstmt = conn.prepareStatement(sql.toString());
			int parameterIndex = 1;

			if (keyword != null && !keyword.trim().isEmpty()) {
				pstmt.setString(parameterIndex++, "%" + keyword + "%");
				pstmt.setString(parameterIndex++, "%" + keyword + "%");
			}

			if (category != null && !category.equals("all") && !category.trim().isEmpty()) {
				String categoryName = category;
				if (category.equals("electronics"))
					categoryName = "전자기기";
				else if (category.equals("fashion"))
					categoryName = "패션";
				else if (category.equals("books"))
					categoryName = "도서";
				else if (category.equals("beauty"))
					categoryName = "뷰티/미용";
				else if (category.equals("sports"))
					categoryName = "스포츠/레저";
				else if (category.equals("appliances"))
					categoryName = "가전제품";

				pstmt.setString(parameterIndex++, categoryName);
			}

			int endRow = page * itemsPerPage;
			int startRow = (page - 1) * itemsPerPage + 1;

			pstmt.setInt(parameterIndex++, endRow);
			pstmt.setInt(parameterIndex, startRow);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				ProductDTO product = new ProductDTO();
				product.setProduct_id(rs.getInt("product_id"));
				product.setSeller_id(rs.getString("seller_id"));
				product.setTitle(rs.getString("title"));
				product.setDescription("(상세설명 생략됨 - CLOB 타입은 별도 조회 요망)");
				product.setCategory_id(rs.getString("category_id"));
				product.setCategory_name(rs.getString("category_name"));
				product.setPrice(rs.getInt("current_price"));
				product.setStatus(rs.getString("status"));
				product.setView_count(rs.getInt("view_count"));
				product.setMaxPrice(rs.getInt("maxPrice"));
				product.setMinPrice(rs.getInt("minPrice"));
				product.setAuction_end_time(rs.getTimestamp("auction_end_time"));
				product.setImage_path(rs.getString("image_path"));
				product.setRegister_date(rs.getTimestamp("register_date"));
				searchResults.add(product);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}

		return searchResults;
	}

	public int countSearchResults(String keyword, String category, String price, String status) {
		int count = 0;

		try {
			conn = getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(*) FROM (");
			sql.append("SELECT p.product_id ");
			sql.append("FROM Products p ");
			sql.append("LEFT JOIN Categories c ON p.category_id = c.category_id ");
			sql.append("LEFT JOIN Bids b ON p.product_id = b.product_id ");
			sql.append("WHERE 1=1 ");

			if (keyword != null && !keyword.trim().isEmpty()) {
				sql.append("AND (p.title LIKE ? OR p.description LIKE ?) ");
			}

			if (category != null && !category.equals("all") && !category.trim().isEmpty()) {
				sql.append("AND c.category_name = ? ");
			}

			if (status != null && !status.trim().isEmpty()) {
				if (status.equals("onsale")) {
					sql.append("AND p.status = '판매중' ");
				} else if (status.equals("closed")) {
					sql.append("AND p.status = '경매종료' ");
				}
			}

			sql.append("GROUP BY p.product_id");
			sql.append(") countTable");

			pstmt = conn.prepareStatement(sql.toString());
			int parameterIndex = 1;

			if (keyword != null && !keyword.trim().isEmpty()) {
				pstmt.setString(parameterIndex++, "%" + keyword + "%");
				pstmt.setString(parameterIndex++, "%" + keyword + "%");
			}

			if (category != null && !category.equals("all") && !category.trim().isEmpty()) {
				String categoryName = category;
				if (category.equals("electronics"))
					categoryName = "전자기기";
				else if (category.equals("fashion"))
					categoryName = "패션";
				else if (category.equals("books"))
					categoryName = "도서";
				else if (category.equals("beauty"))
					categoryName = "뷰티/미용";
				else if (category.equals("sports"))
					categoryName = "스포츠/레저";
				else if (category.equals("appliances"))
					categoryName = "가전제품";

				pstmt.setString(parameterIndex++, categoryName);
			}

			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResources();
		}

		return count;
	}

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
}
