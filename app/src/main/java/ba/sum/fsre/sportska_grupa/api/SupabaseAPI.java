package ba.sum.fsre.sportska_grupa.api;

import java.util.List;

import ba.sum.fsre.sportska_grupa.models.Training;
import ba.sum.fsre.sportska_grupa.models.Attendance;
import ba.sum.fsre.sportska_grupa.models.request.RegisterRequest;
import ba.sum.fsre.sportska_grupa.models.request.LoginRequest;
import ba.sum.fsre.sportska_grupa.models.request.CreateTrainingRequest;
import ba.sum.fsre.sportska_grupa.models.response.AuthResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseAPI {

    @Headers("Content-Type: application/json")
    @POST("auth/v1/signup")
    Call<AuthResponse> signup(@Body RegisterRequest request);

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body LoginRequest request);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @GET("rest/v1/trainings?select=*&order=training_date.desc")
    Call<List<Training>> getTrainings();

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/trainings?select=*")
    Call<List<Training>> createTraining(@Body CreateTrainingRequest request);

    @Headers("Content-Type: application/json")
    @DELETE("rest/v1/trainings")
    Call<Void> deleteTraining(@Query("id") String id);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @GET("rest/v1/attendance?select=*")
    Call<List<Attendance>> getAttendance();

}
