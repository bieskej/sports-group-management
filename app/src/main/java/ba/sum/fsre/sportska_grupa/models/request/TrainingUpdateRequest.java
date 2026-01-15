package ba.sum.fsre.sportska_grupa.models;

import com.google.gson.annotations.SerializedName;

public class TrainingUpdateRequest {

    @SerializedName("training_date")
    private String trainingDate;

    private String title;

    private String description;

    public TrainingUpdateRequest(String trainingDate, String title, String description) {
        this.trainingDate = trainingDate;
        this.title = title;
        this.description = description;
    }

    public String getTrainingDate() { return trainingDate; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public void setTrainingDate(String trainingDate) { this.trainingDate = trainingDate; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
}
