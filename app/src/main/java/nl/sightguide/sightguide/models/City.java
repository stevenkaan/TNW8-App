package nl.sightguide.sightguide.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class City extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String name;
    private String information;
    private String country;
    private double latitude;
    private double longitude;
    private int population;

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setInformation(String information) { this.information = information; }
    public void setCountry(String country) { this.country = country; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setPopulation(int population) { this.population = population; }

    public int getId() { return id; }
    public String getName() { return  name; }
    public String getInformation() { return information; }
    public String getCountry() { return country; }
    public double getLatitude() { return  latitude; }
    public double getLongitude() { return longitude; }
    public int getPopulation() { return population; }
}
