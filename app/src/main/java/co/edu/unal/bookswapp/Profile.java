package co.edu.unal.bookswapp;

/**
 * Created by ivanc on 27/11/2017.
 */

public class Profile {
    String id;
    String email;
    String name;
    String interests;
    String phone;
    String city;
    double score;
    double scoresCounter;
    int offersCounter;
    int interchangesCounter;
    String urlImage;

    public Profile(String id, String email, String name, String interests, String phone, String city, double score, double scoresCounter, int offersCounter, int interchangesCounter, String urlImage) {
        if(name == null) name = "Nombre no disponible";
        if(phone == null) phone = "Telefono no disponible";
        if(city.equals("")) city = "Ciudad no disponible";
        this.id = id;
        this.email = email;
        this.name = name;
        this.interests = interests;
        this.phone = phone;
        this.city = city;
        this.score = score;
        this.scoresCounter = scoresCounter;
        this.offersCounter = offersCounter;
        this.interchangesCounter = interchangesCounter;
        this.urlImage = urlImage;
    }

    public Profile() {}

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getScoresCounter() {
        return scoresCounter;
    }

    public void setScoresCounter(double scoresCounter) {
        this.scoresCounter = scoresCounter;
    }

    public int getOffersCounter() {
        return offersCounter;
    }

    public void setOffersCounter(int offersCounter) {
        this.offersCounter = offersCounter;
    }

    public int getInterchangesCounter() {
        return interchangesCounter;
    }

    public void setInterchangesCounter(int interchangesCounter) {
        this.interchangesCounter = interchangesCounter;
    }

    public String getUrlImage() {
        return urlImage;
    }


    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", interests='" + interests + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", score=" + score +
                ", scoresCounter=" + scoresCounter +
                ", offersCounter=" + offersCounter +
                ", interchangesCounter=" + interchangesCounter +
                '}';
    }

}
