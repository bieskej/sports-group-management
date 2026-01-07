package ba.sum.fsre.sportska_grupa.models;

import com.google.gson.annotations.SerializedName;

public class Training {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("training_date")
    private String trainingDate;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("created_at")
    private String createdAt;

    // Getteri
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
