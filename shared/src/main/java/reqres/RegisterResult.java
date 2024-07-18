package reqres;

public record RegisterResult(String username, String authToken) implements ServiceResult{
}
