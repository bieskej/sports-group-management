package ba.sum.fsre.sportska_grupa.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Training implements Serializable {
    private String id;

    @SerializedName("training_date")
    private String trainingDate;

    @SerializedName("training_time")
    private String trainingTime;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("created_at")
    private String createdAt;

    private String title;
    private String description;

    public Training() {}

    public Training(String trainingDate, String trainingTime, String createdBy, String title, String description) {
        this.trainingDate = trainingDate;
        this.trainingTime = trainingTime;
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
    }

    public String getId() { return id; }
    public String getTrainingDate() { return trainingDate; }
    public String getTrainingTime() { return trainingTime; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public void setId(String id) { this.id = id; }
    public void setTrainingDate(String trainingDate) { this.trainingDate = trainingDate; }
    public void setTrainingTime(String trainingTime) { this.trainingTime = trainingTime; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
}
