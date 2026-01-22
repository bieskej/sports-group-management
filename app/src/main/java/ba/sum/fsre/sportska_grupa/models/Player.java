package ba.sum.fsre.sportska_grupa.models;

import com.google.gson.annotations.SerializedName;

public class Player {

    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("role")
    private String role;

    // Lokalna polja (nisu u bazi)
    private boolean present;
    private String attendanceId;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }
}
