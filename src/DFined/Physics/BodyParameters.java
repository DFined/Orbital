package DFined.Physics;

public class BodyParameters {
    private double mass;
    private double radius;
    private String texture;
    private String registryName;
    private String displayName;

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public String getTexture() {
        return texture;
    }

    public String getDisplayName() {
        return displayName;
    }
}
