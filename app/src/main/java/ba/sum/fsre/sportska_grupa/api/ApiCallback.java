package ba.sum.fsre.sportska_grupa.api;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            // Za Void tipove, body može biti null
            if (response.body() != null) {
                onSuccess(response.body());
            } else {
                // Ako je body null ali je response uspješan, proslijedi null (za Void tipove)
                onSuccess(null);
            }
        } else {
            String details = null;
            if (response.errorBody() != null) {
                try {
                    details = response.errorBody().string();
                } catch (IOException ignored) {
                }
            }

            // Pokušaj izvući "message"/"code" iz Supabase (PostgREST) error JSON-a
            if (details != null && details.trim().startsWith("{")) {
                try {
                    JSONObject obj = new JSONObject(details);
                    String msg = obj.optString("message", null);
                    String code = obj.optString("code", null);
                    String hint = obj.optString("hint", null);

                    StringBuilder sb = new StringBuilder();
                    sb.append("Greška: ").append(response.code());
                    if (code != null && !code.isEmpty()) sb.append(" (").append(code).append(")");
                    if (msg != null && !msg.isEmpty()) sb.append(" - ").append(msg);
                    if (hint != null && !hint.isEmpty()) sb.append(" | Hint: ").append(hint);

                    onError(sb.toString());
                    return;
                } catch (Exception ignored) {
                    // fallback ispod
                }
            }

            if (details != null && !details.isEmpty()) {
                onError("Greška: " + response.code() + " - " + details);
            } else {
                onError("Greška: " + response.code());
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onError("Greška mreže: " + t.getMessage());
    }

    public abstract void onSuccess(T response);
    public abstract void onError(String errorMessage);
}
