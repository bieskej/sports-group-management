package ba.sum.fsre.sportska_grupa.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Training implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("training_date")
    private String trainingDate;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("created_at")
    private String createdAt;

    private String title;

    private String description;

    // Constructors
    public Training() {}

    public Training(String trainingDate, String createdBy, String title, String description) {
        this.trainingDate = trainingDate;
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
    }

    // Getters
    public String getId() { return id; }
    public String getTrainingDate() { return trainingDate; }
    public String getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTrainingDate(String trainingDate) { this.trainingDate = trainingDate; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
}
