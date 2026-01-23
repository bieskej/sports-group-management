package ba.sum.fsre.sportska_grupa.api;

import java.util.List;

import ba.sum.fsre.sportska_grupa.models.Attendance;
import ba.sum.fsre.sportska_grupa.models.Player;
import ba.sum.fsre.sportska_grupa.models.Profile;
import ba.sum.fsre.sportska_grupa.models.Training;
import ba.sum.fsre.sportska_grupa.models.request.AttendanceRequest;
import ba.sum.fsre.sportska_grupa.models.request.TrainingUpdateRequest;
import ba.sum.fsre.sportska_grupa.models.request.LoginRequest;
import ba.sum.fsre.sportska_grupa.models.request.RegisterRequest;
import ba.sum.fsre.sportska_grupa.models.response.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseAPI {

    @Headers("Content-Type: application/json")
    @POST("auth/v1/signup")
    Call<AuthResponse> signup(@Body RegisterRequest request);

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("rest/v1/profiles")
    Call<List<Profile>> getMyProfile(
            @Query("id") String userId,
            @Query("select") String select
    );

    @GET("rest/v1/trainings?select=*&order=training_date.desc,training_time.desc")
    Call<List<Training>> getTrainings();


    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/trainings")
    Call<List<Training>> createTraining(@Body Training training);

    @DELETE("rest/v1/trainings")
    Call<Void> deleteTraining(@Query("id") String id);

    // ✅ UPDATE TRAINING
    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @PATCH("rest/v1/trainings")
    Call<List<Training>> updateTraining(
            @Query("id") String idFilter,   // "eq.<training_id>"
            @Body TrainingUpdateRequest request
    );
    @GET("rest/v1/profiles")
    Call<List<Player>> getPlayerById(
            @Query("id") String idFilter
    );
    @GET("rest/v1/profiles")
    Call<List<Player>> getAllPlayers(
            @Query("role") String roleFilter
    );
    // Dohvati prisutnost za određeni trening
    @GET("rest/v1/attendance")
    Call<List<Attendance>> getAttendanceForTraining(
            @Query("training_id") String trainingIdFilter
    );
    // Kreiraj attendance
    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/attendance")
    Call<List<Attendance>> createAttendance(@Body AttendanceRequest request);

    // Ažuriraj attendance
    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @PATCH("rest/v1/attendance")
    Call<List<Attendance>> updateAttendance(
            @Query("id") String idFilter,
            @Body AttendanceRequest request
    );

    // Dohvati sve zapise prisutnosti za određenog igrača
    @GET("rest/v1/attendance")
    Call<List<Attendance>> getPlayerAttendance(
            @Query("player_id") String playerIdFilter
    );
}
