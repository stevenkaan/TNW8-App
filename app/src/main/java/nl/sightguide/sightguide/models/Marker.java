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
    private String image_1;
    private String image_2;
    private String image_3;
    private String image_4;
    private String audio;

    private City city;
    private int type;


    public void setId(int id) { this.id = id; }
    public void setCity(City city) { this.city = city; }
    public void setType(int type) { this.type = type; }
    public void setName(String name) { this.name = name; }
    public void setInformation(String information) { this.information = information; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setImage_1(String image_1) { this.image_1 = image_1; }
    public void setImage_2(String image_2) { this.image_2 = image_2; }
    public void setImage_3(String image_3) { this.image_3 = image_3; }
    public void setImage_4(String image_4) { this.image_4 = image_4; }
    public void setAudio(String audio) { this.audio = audio; }

    public int getId() { return id; }
    public City getCity() { return city; }
    public int getType() { return type; }
    public String getName() { return  name; }
    public String getInformation() { return information; }
    public double getLatitude() { return  latitude; }
    public double getLongitude() { return longitude; }
    public String getImage_1() { return image_1; }
    public String getImage_2() { return image_2; }
    public String getImage_3() { return image_3; }
    public String getImage_4() { return image_4; }
    public String getAudio() { return audio; }
}
