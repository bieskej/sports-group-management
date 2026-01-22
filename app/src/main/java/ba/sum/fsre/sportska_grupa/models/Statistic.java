package ba.sum.fsre.sportska_grupa.models;

import java.io.Serializable;

public class Statistic implements Serializable {

    private String id;
    private String playerId;
    private String trainingId;

    private boolean present;
    private int goals;
    private int assists;
    private int fouls;

    public Statistic() {
    }

    public Statistic(String playerId, String trainingId) {
        this.playerId = playerId;
        this.trainingId = trainingId;
        this.present = false;
        this.goals = 0;
        this.assists = 0;
        this.fouls = 0;
    }

    public String getId() {
        return id;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public boolean isPresent() {
        return present;
    }

    public int getGoals() {
        return goals;
    }

    public int getAssists() {
        return assists;
    }

    public int getFouls() {
        return fouls;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public void setFouls(int fouls) {
        this.fouls = fouls;
    }
}
