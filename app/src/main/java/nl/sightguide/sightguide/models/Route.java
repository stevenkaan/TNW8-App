package nl.sightguide.sightguide.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Route extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String name;
    private String infomation;
    private double distance;

    private RealmList<Marker> markers;
    private City city;

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setInfomation(String infomation) { this.infomation = infomation; }
    public void setDistance(double distance) { this.distance = distance; }
    public void setMarkers(RealmList<Marker> markers) { this.markers = markers; }
    public void setCity(City city) { this.city = city; }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getInfomation() { return infomation; }
    public double getDistance(){ return distance; }
    public RealmList<Marker> getMarkers() { return markers; }
    public City getCity() { return city; }
}
