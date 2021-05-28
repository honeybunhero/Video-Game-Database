package my.gamessqlite;

public class GameInformation {
    private String name, platform;

    public GameInformation(String name, String platform) {
        this.name = name;
        this.platform = platform;
    }
    

    @Override
    public String toString() {
        return name + " : " + platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
