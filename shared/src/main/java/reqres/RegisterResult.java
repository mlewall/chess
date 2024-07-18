package reqres;

public record RegisterResult(String username, String password, String email) implements ServiceResult{
}
