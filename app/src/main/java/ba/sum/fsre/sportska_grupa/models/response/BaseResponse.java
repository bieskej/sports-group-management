package ba.sum.fsre.sportska_grupa.models.response;

public abstract class BaseResponse {
    private String error;
    private String message;

    public boolean hasError() {
        return error != null;
    }

    public String getError() { return error; }
    public String getMessage() { return message; }
}
