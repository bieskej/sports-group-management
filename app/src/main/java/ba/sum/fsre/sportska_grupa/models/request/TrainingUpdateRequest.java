package ba.sum.fsre.sportska_grupa.models.request;

import com.google.gson.annotations.SerializedName;

public class TrainingUpdateRequest {

    @SerializedName("training_date")
    private String trainingDate;

    @SerializedName("training_time")
    private String trainingTime;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    public TrainingUpdateRequest(String trainingDate, String trainingTime, String title, String description) {
        this.trainingDate = trainingDate;
        this.trainingTime = trainingTime;
        this.title = title;
        this.description = description;
    }
}
