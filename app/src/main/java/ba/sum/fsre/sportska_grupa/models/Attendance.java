package ba.sum.fsre.sportska_grupa.models;

import com.google.gson.annotations.SerializedName;

public class Attendance {
    @SerializedName("id")
    private String id;

    @SerializedName("training_id")
    private String trainingId;

    @SerializedName("player_id")
    private String playerId;

    @SerializedName("present")
    private boolean present;

    @SerializedName("created_at")
    private String createdAt;

    // Getteri
    public String getId() {
        return id;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public boolean isPresent() {
        return present;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
