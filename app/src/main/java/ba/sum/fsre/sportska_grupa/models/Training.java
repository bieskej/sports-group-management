package ba.sum.fsre.sportska_grupa.models;

import com.google.gson.annotations.SerializedName;

public class Training {

    @SerializedName("id")
    private String id;

    @SerializedName("training_date")
    private String trainingDate;

    @SerializedName("training_time")
    private String trainingTime;

    // ⛔ user_id MIČEMO
    // ✅ koristimo created_by (kako je u bazi + RLS policy)
    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("coach")
    private String coach;

    // Prazan konstruktor (Gson)
    public Training() {}

    // Konstruktor za CREATE
    public Training(String trainingDate, String trainingTime, String createdBy,
                    String title, String description) {
        this.trainingDate = trainingDate;
        this.trainingTime = trainingTime;
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
    }

    // Konstruktor za CREATE + coach
    public Training(String trainingDate, String trainingTime, String createdBy,
                    String title, String description, String coach) {
        this.trainingDate = trainingDate;
        this.trainingTime = trainingTime;
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.coach = coach;
    }

    // GETTERI
    public String getId() {
        return id;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

    public String getTrainingTime() {
        return trainingTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCoach() {
        return coach;
    }

    // SETTERI
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }

    public void setTrainingTime(String trainingTime) {
        this.trainingTime = trainingTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
