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
    private String image_1;
    private String image_2;
    private String image_3;
    private String image_4;

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setInformation(String information) { this.information = information; }
    public void setCountry(String country) { this.country = country; }
    public void setImage_1(String image_1) { this.image_1 = image_1; }
    public void setImage_2(String image_2) { this.image_2 = image_2; }
    public void setImage_3(String image_3) { this.image_3 = image_3; }
    public void setImage_4(String image_4) { this.image_4 = image_4; }

    public int getId() { return id; }
    public String getName() { return  name; }
    public String getInformation() { return information; }
    public String getCountry() { return country; }
    public String getImage_1() { return image_1; }
    public String getImage_2() { return image_2; }
    public String getImage_3() { return image_3; }
    public String getImage_4() { return image_4; }
}
