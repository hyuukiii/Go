package FindIdPw;

public class FindPwService {
	public String findPassword(FindPwDTO dto) {
		FindPwDAO dao = new FindPwDAO();
		return dao.findPassword(dto.getUserId(), dto.getUsername(), dto.getEmail(), dto.getPhone());
	}
}
