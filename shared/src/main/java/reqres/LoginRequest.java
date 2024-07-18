package reqres;

public record LoginRequest(String username, String password) implements ServiceResult{
}
