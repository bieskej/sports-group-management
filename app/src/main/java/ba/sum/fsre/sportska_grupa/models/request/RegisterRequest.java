package ba.sum.fsre.sportska_grupa.models.request;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends BaseRequest{
    private String email;
    private String password;
    private Map<String, Object> data;

    public RegisterRequest(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.data = new HashMap<>();
        this.data.put("username", username);

    }
}
