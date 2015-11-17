package nl.sightguide.sightguide.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Marker extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String name;
    private String information;
    private double latitude;
    private double longitude;
    private String image;

    private City city;
    private int type_id;



    public void setId(int id) { this.id = id; }
    public void setCity(City city) { this.city = city; }
    public void setType_id(int type_id) { this.type_id = type_id; }
    public void setName(String name) { this.name = name; }
    public void setInformation(String information) { this.information = information; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setImage(String image) { this.image = image; }

    public int getId() { return id; }
    public City getCity() { return city; }
    public int getType_id() { return type_id; }
    public String getName() { return  name; }
    public String getInformation() { return information; }
    public double getLatitude() { return  latitude; }
    public double getLongitude() { return longitude; }
    public String getImage() { return image; }
}
