package ba.sum.fsre.sportska_grupa.api;

import ba.sum.fsre.sportska_grupa.models.request.RegisterRequest;
import ba.sum.fsre.sportska_grupa.models.request.LoginRequest;
import ba.sum.fsre.sportska_grupa.models.response.AuthResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SupabaseAPI {

    @Headers("Content-Type: application/json")
    @POST("auth/v1/signup")
    Call<AuthResponse> signup(@Body RegisterRequest request);

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body LoginRequest request);

}
