package ba.sum.fsre.sportska_grupa.models.request;

import com.google.gson.annotations.SerializedName;

public class CreateTrainingRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("training_date")
    private String trainingDate;

    @SerializedName("created_by")
    private String createdBy;

    public CreateTrainingRequest(String title, String description, String trainingDate, String createdBy) {
        this.title = title;
        this.description = description;
        this.trainingDate = trainingDate;
        this.createdBy = createdBy;
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
}
