package nl.sightguide.sightguide.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Types extends RealmObject {

    @PrimaryKey
    private int type;

    @Required
    private Boolean display;

    public void setType(int type) { this.type = type; }
    public void setDisplay(Boolean display) { this.display = display; }

    public int getType() { return type; }
    public Boolean getDisplay() { return display; }
}
