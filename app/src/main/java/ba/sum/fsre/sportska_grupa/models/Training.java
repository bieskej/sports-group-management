// Training.java
package ba.sum.fsre.sportska_grupa.models;

import com.google.gson.annotations.SerializedName;

public class Training {

    @SerializedName("id")
    private String id;

    @SerializedName("training_date")
    private String trainingDate;

    @SerializedName("training_time")
    private String trainingTime;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("coach")
    private String coach;

    // Prazan konstruktor (korisno za Gson/Retrofit)
    public Training() {}

    // Konstruktor za CREATE (ako hoćeš i coach odmah slati, koristi ovaj ispod)
    public Training(String trainingDate, String trainingTime, String userId, String title, String description) {
        this.trainingDate = trainingDate;
        this.trainingTime = trainingTime;
        this.userId = userId;
        this.title = title;
        this.description = description;
    }

    // Konstruktor za CREATE + coach (preporučeno ako unosiš trenera)
    public Training(String trainingDate, String trainingTime, String userId, String title, String description, String coach) {
        this.trainingDate = trainingDate;
        this.trainingTime = trainingTime;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
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
    public void setCoach(String coach) {
        this.coach = coach;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }

    public void setTrainingTime(String trainingTime) {
        this.trainingTime = trainingTime;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
