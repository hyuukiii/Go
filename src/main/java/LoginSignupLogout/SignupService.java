package main.java.LoginSignupLogout;

import User.UserDTO;

public class SignupService {
	SignupDAO dao = new SignupDAO();
	
	public boolean registerUser(UserDTO dto) {
		int result = dao.insertUser(dto);
		return result == 1;
	}
}