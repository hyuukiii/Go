package Product;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Files;

@WebServlet("/images/*")
public class ImageServlet extends HttpServlet {
	private static final String IMAGE_DIR = "C:/uploaded_images";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String requestedFile = req.getPathInfo();
		if (requestedFile == null || requestedFile.equals("/")) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "파일명이 제공되지 않았습니다.");
			return;
		}

		File file = new File(IMAGE_DIR, requestedFile.substring(1));
		
		if (!file.exists() || !file.isFile()) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "이미지 파일이 존재하지 않습니다.");
			return;
		}

		String mimeType = getServletContext().getMimeType(file.getName());
		if (mimeType == null)
			mimeType = "application/octet-stream";

		resp.setContentType(mimeType);
		Files.copy(file.toPath(), resp.getOutputStream());
	}
}
