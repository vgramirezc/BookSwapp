package co.edu.unal.bookswapp;

/**
 * Created by ivanc on 27/11/2017.
 */

public class Profile {
    String id;
    String email;
    String name;
    String interests;
    double score;
    double numberscores;

    public Profile() {
    }

    public Profile(String id, String email, String name, String interests, double score, double numberscores) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.interests = interests;
        this.score = score;
        this.numberscores = numberscores;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getNumberscores() {
        return numberscores;
    }

    public void setNumberscores(double numberscores) {
        this.numberscores = numberscores;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", interests='" + interests + '\'' +
                ", score=" + score +
                ", numberscores=" + numberscores +
                '}';
    }
}
