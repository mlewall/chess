package reqres;

import com.sun.net.httpserver.Request;

public record LogoutRequest(String authToken) {
}
