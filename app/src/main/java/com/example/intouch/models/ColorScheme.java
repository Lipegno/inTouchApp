package com.example.intouch.models;

public class ColorScheme {
    String colorSchemeID;
    String positiveColor;
    String neutralColor;
    String negativeColor;

    ColorScheme(String colorSchemeID, String positiveColor, String neutralColor, String negativeColor){
        this.colorSchemeID = colorSchemeID;
        this.positiveColor = positiveColor;
        this.neutralColor = neutralColor;
        this.negativeColor = negativeColor;
    }

    public String getColorSchemeID() {
        return colorSchemeID;
    }

    public void setColorSchemeID(String colorSchemeID) {
        this.colorSchemeID = colorSchemeID;
    }

    public String getPositiveColor() {
        return positiveColor;
    }

    public void setPositiveColor(String positiveColor) {
        this.positiveColor = positiveColor;
    }

    public String getNeutralColor() {
        return neutralColor;
    }

    public void setNeutralColor(String neutralColor) {
        this.neutralColor = neutralColor;
    }

    public String getNegativeColor() {
        return negativeColor;
    }

    public void setNegativeColor(String negativeColor) {
        this.negativeColor = negativeColor;
    }
}
