package nl.sightguide.sightguide.models;

import org.json.JSONArray;

public class LauncherCity {
    private int id;
    private int position;
    private String name;
    private String country;
    private JSONArray languages;

    public void setId(int id) { this.id = id; }
    public void setPosition(int id) { this.position = position; }
    public void setCountry(String country) { this.country = country; }
    public void setName(String name) { this.name = name; }
    public void setLanguages(JSONArray languages) { this.languages = languages; }

    public int getId() {return id; }
    public int getPosition() { return position; }
    public String getCountry() { return country; }
    public String getName() { return name; }
    public JSONArray getLanguages() { return languages; }
}
