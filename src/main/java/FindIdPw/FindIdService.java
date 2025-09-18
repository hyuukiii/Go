package FindIdPw;

public class FindIdService {
	public String findId(FindIdDTO dto) {
		FindIdDAO dao = new FindIdDAO();
		return dao.findUserId(dto.getUsername(), dto.getEmail(), dto.getPhone());
	}
}
