package main.java.Mypage;

import User.UserDAO;
import User.UserDTO;

public class UserService {

	UserDAO dao = new UserDAO();

	// 조회
	public UserDTO getUserInfo(String user_id) {
		UserDTO user = dao.userInfo(user_id);
		return user;
	}

	// 변경
	public void updateUserInfo(String user_id, String username, String email, String password, String phone,
			String address) {
		dao.updateUser(user_id, username, email, password, phone, address);
	}

	// 이미지 변경 / 삭제
	public void updateProfileImg(String userId, String imgPath) {
		dao.ProfileImage(userId, imgPath);
	}

}
