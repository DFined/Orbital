package DFined.Physics;

import com.google.gson.Gson;

import java.util.HashMap;

public class BodyPresets {
    public static final String SUN = "sun";
    public static final String MERCURY = "mercury";
    public static final String VENUS = "venus";
    public static final String EARTH = "earth";
    public static final String MOON = "moon";
    public static final String MARS = "mars";
    public static final String PHOBOS = "phobos";
    public static final String DEIMOS = "deimos";
    public static final String JUPITER = "jupiter";
    public static final String EUROPA = "europa";
    public static final String GANYMEDE = "ganymede";
    public static final String IO = "io";
    public static final String CALLISTO = "callisto";
    public static final String SATURN = "saturn";
    public static final String TITAN = "titan";
    public static final String ENCELADUS = "enceladus";
    public static final String MIMAS = "mimas";
    public static final String URANUS = "uranus";
    public static final String NEPTUNE = "neptune";
    public static final String PLUTO =  "pluto";
    private static Gson gson = new Gson();

    private static final HashMap<String, BodyParameters> presets = new HashMap<>();

    public static void addPreset(String json){
        BodyParameters preset = gson.fromJson(json,BodyParameters.class);
        presets.put(preset.getRegistryName(),preset);
    }

    public static BodyParameters getPreset(String key){
        return presets.get(key);
    }
}
