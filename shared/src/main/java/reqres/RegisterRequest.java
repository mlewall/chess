package reqres;

//todo: address that this is the same thing as UserData.

public record RegisterRequest(String username, String password, String email) {
}
