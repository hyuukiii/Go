package Product;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/registerproduct")
@MultipartConfig
public class RegisterProductServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 카테고리 목록을 가져옴
		CategoryDAO categoryDAO = new CategoryDAO();
		ArrayList<CategoryDTO> categories = categoryDAO.getAllCategories();

		// 카테고리 목록을 request에 저장
		req.setAttribute("categories", categories);

		req.getRequestDispatcher("WEB-INF/views/registerproduct.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		HttpSession session = req.getSession();
		String sellerid = (String) session.getAttribute("user_id");

		if (sellerid == null) {
			resp.sendRedirect("login");
			return;
		}

		String title = req.getParameter("title");
		String description = req.getParameter("description");
		int maxprice = Integer.parseInt(req.getParameter("maxprice"));
		int minprice = Integer.parseInt(req.getParameter("minprice"));
		String categoryId = req.getParameter("category");

		if (minprice > maxprice) {
			req.setAttribute("errorMessage", "하한가는 상한가보다 클 수 없습니다.");

			// 카테고리 목록 다시 불러오기
			CategoryDAO categoryDAO = new CategoryDAO();
			ArrayList<CategoryDTO> categories = categoryDAO.getAllCategories();
			req.setAttribute("categories", categories);

			// 입력 값 유지
			req.setAttribute("title", title);
			req.setAttribute("description", description);
			req.setAttribute("maxprice", maxprice);
			req.setAttribute("minprice", minprice);
			req.setAttribute("category", categoryId);

			req.getRequestDispatcher("WEB-INF/views/registerproduct.jsp").forward(req, resp);
			return;
		}

		// 경매 지속 시간 파라미터 가져오기
		int auctionHours = Integer.parseInt(req.getParameter("auction_hours"));
		int auctionMinutes = Integer.parseInt(req.getParameter("auction_minutes"));
		int auctionSeconds = Integer.parseInt(req.getParameter("auction_seconds"));

		// 경매 종료 시간 계산 (현재 시간 + 지정된 지속 시간)
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, auctionHours);
		calendar.add(Calendar.MINUTE, auctionMinutes);
		calendar.add(Calendar.SECOND, auctionSeconds);
		Date auctionEndTime = calendar.getTime();

		Timestamp sqlAuctionEndTime = new Timestamp(auctionEndTime.getTime());
		
		// 경매 지속 시간이 0인지 확인
		if (auctionHours == 0 && auctionMinutes == 0 && auctionSeconds == 0) {
			req.setAttribute("errorMessage", "경매 시간을 설정해주세요.");

			// 카테고리 목록을 다시 가져옴
			CategoryDAO categoryDAO = new CategoryDAO();
			ArrayList<CategoryDTO> categories = categoryDAO.getAllCategories();
			req.setAttribute("categories", categories);

			// 입력했던 값들 유지
			req.setAttribute("title", title);
			req.setAttribute("description", description);
			req.setAttribute("maxprice", maxprice);
			req.setAttribute("minprice", minprice);
			req.setAttribute("category", categoryId);

			req.getRequestDispatcher("WEB-INF/views/registerproduct.jsp").forward(req, resp);
			return;
		}

		String uploadPath = "C:/uploaded_images";
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists())
			uploadDir.mkdirs();

		Part imagePart = req.getPart("image");
		String imagePathToSave;

		if (imagePart != null && imagePart.getSize() > 0) {
			String originalFileName = imagePart.getSubmittedFileName();
			String fileExtension = "";
			if (originalFileName.lastIndexOf(".") > 0) {
				fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
			}

			String imageFileName = UUID.randomUUID().toString() + fileExtension;
			File saveFile = new File(uploadPath, imageFileName);
			imagePart.write(saveFile.getAbsolutePath());

			imagePathToSave = "images/" + imageFileName;
		} else {
			imagePathToSave = "images/default.jpg";
		}

		ProductDTO product = new ProductDTO();
		product.setTitle(title);
		product.setDescription(description);
		product.setMaxPrice(maxprice);
		product.setMinPrice(minprice);
		product.setSeller_id(sellerid);
		product.setImage_path(imagePathToSave);
		product.setCategory_id(categoryId); // 카테고리 ID 설정
		product.setAuction_end_time(sqlAuctionEndTime); // 경매 종료 시간 설정

		RegisterProductDAO redao = new RegisterProductDAO();
		redao.insertProduct(product);

		resp.sendRedirect("index");
	}
}
