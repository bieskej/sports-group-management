package ba.sum.fsre.sportska_grupa.models.request;

import com.google.gson.annotations.SerializedName;

public class AttendanceRequest {

    @SerializedName("training_id")
    private String trainingId;

    @SerializedName("player_id")
    private String playerId;

    @SerializedName("present")
    private boolean present;

    public AttendanceRequest(String trainingId, String playerId, boolean present) {
        this.trainingId = trainingId;
        this.playerId = playerId;
        this.present = present;
    }
}
