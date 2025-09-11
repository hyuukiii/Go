package LoginSignupLogout;

import java.io.File;
import java.io.IOException;

import java.sql.*;
import java.util.UUID;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import User.UserDTO;

import javax.servlet.annotation.MultipartConfig;

@MultipartConfig
@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("WEB-INF/views/signup.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String user_id = request.getParameter("user_id");
		String password = request.getParameter("password");
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String rating = request.getParameter("rating");
		String sido = request.getParameter("sido");
		String gugun = request.getParameter("gugun");
		String address = sido + " " + gugun;

		String uploadPath = "C:/uploaded_images";
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists())
			uploadDir.mkdirs();

		Part imagePart = request.getPart("profile_image");
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

		UserDTO dto = new UserDTO(user_id, username, email, password, phone, address, null, imagePathToSave, 0.0);

		SignupService service = new SignupService();
		boolean success = service.registerUser(dto);

		if (success) {
			response.sendRedirect("index");
		} else {
			response.sendRedirect("signup?error=1");
		}
		System.out.println("주소: " + address);

	}
}