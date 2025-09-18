package FindIdPw;

public class FindPwDTO extends FindIdDTO{
	String userId;
	
	public FindPwDTO() {
		
	}

	public FindPwDTO(String userId, String username, String email, String phone) {
	    super(username, email, phone);
	    this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "FindPwDAO [userId=" + userId + "]";
	}
	
	
}
