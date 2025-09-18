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

		// null이거나 빈 경로일 때 default.jpg 반환
		if (requestedFile == null || requestedFile.equals("/")) {
			requestedFile = "/default.jpg";
		}

		File file = new File(IMAGE_DIR, requestedFile.substring(1));

		// 파일이 없으면 default.jpg 반환
		if (!file.exists() || !file.isFile()) {
			file = new File(getServletContext().getRealPath("/images/default.jpg"));
			if (!file.exists()) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}

		String mimeType = getServletContext().getMimeType(file.getName());
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}

		resp.setContentType(mimeType);
		Files.copy(file.toPath(), resp.getOutputStream());
	}
}
