package main.java.FindIdPw;

public class FindIdDTO {
	String username;
	String email;
	String phone;
	
	public FindIdDTO() {
	}

	@Override
	public String toString() {
		return "FindIdDTO [username=" + username + ", email=" + email + ", phone=" + phone + "]";
	}

	public FindIdDTO(String username, String email, String phone) {
		super();
		this.username = username;
		this.email = email;
		this.phone = phone;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
