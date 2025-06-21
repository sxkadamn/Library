package net.lielibrary.gui.customize;

public enum AnimationType {
    FILL_GLASS,
    WAVE,
    RANDOM_FILL,
    SPIRAL;

    public static AnimationType fromString(String name) {
        try {
            return AnimationType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return FILL_GLASS;
        }
    }
}