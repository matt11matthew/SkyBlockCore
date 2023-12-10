package net.cloudescape.skyblock.miscellaneous.minions.tag;

public enum MinionTagType {
    DEFAULT(""),
    COOLIO("Coolio"),
    SKYHEROES("Skyheroes"),
    BUGS("#BuGs"),
    MRMINION("MrMinion");

    private String tagText;

    MinionTagType(String tagText) {
        this.tagText = tagText;
    }

    public String getTagText() {
        return tagText;
    }
}
