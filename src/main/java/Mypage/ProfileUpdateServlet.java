package Mypage;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/profileUpdate")
@MultipartConfig
public class ProfileUpdateServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		String userId = (String) session.getAttribute("user_id");

		String uploadPath = "C:/uploaded_images";
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists())
			uploadDir.mkdirs();

		Part imagePart = req.getPart("profileImage");
		String fileName = UUID.randomUUID() + "_" + imagePart.getSubmittedFileName();
		imagePart.write(uploadPath + File.separator + fileName);

		String imagePath = "images/" + fileName;
		
		UserService s = new UserService();
		s.updateProfileImg(userId, imagePath);

		resp.setContentType("text/plain; charset=UTF-8");
		resp.getWriter().write(imagePath); // 경로를 바로 응답으로 줌
	}
}
