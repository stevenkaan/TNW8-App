package nl.sightguide.sightguide.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Type extends RealmObject {

    @PrimaryKey
    private int type;

    @Required
    private String name_nl;
    private String name_en;
    private String name_es;
    private String image;
    private Boolean display;

    public void setType(int type) { this.type = type; }
    public void setName_nl(String name_nl) { this.name_nl = name_nl; }
    public void setName_en(String name_en) { this.name_en = name_en; }
    public void setName_es(String name_es) { this.name_es = name_es; }
    public void setImage(String image) { this.image = image; }
    public void setDisplay(Boolean display) { this.display = display; }

    public int getType() { return type; }
    public String getName_nl() { return name_nl; }
    public String getName_en() { return name_en; }
    public String getName_es() { return name_es; }
    public String getImage() { return image; }
    public Boolean getDisplay() { return display; }

}

