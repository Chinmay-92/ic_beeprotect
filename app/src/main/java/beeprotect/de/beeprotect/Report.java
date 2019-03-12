package beeprotect.de.beeprotect;

import java.util.Objects;

public class Report {

    /**
     * Item text
     */
    //    @com.google.gson.annotations.SerializedName("cancerProbability")
    public String cancerProbability = "0";

    //    @com.google.gson.annotations.SerializedName("temperatureDifference")
    public String temperatureDifference = "0";
    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("text")
    private String mText = "report";
    /**
     * Item text
     */
//    @com.google.gson.annotations.SerializedName("painIntensity")
    public String painIntensity = "0";

    //    @com.google.gson.annotations.SerializedName("response")
    public String response = "empty";

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;

    /**
     * Report constructor
     */
    public Report() {

    }

    @Override
    public String toString() {
        return getText();
    }

    /**
     * Initializes a new Report
     *
     * @param text
     *            The item text
     * @param id
     *            The item id
     */
    public Report(String text, String id) {
        this.setText(text);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
    public String getText() {
        return mText;
    }

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public final void setText(String text) {
        mText = text;
    }

    /**
     * Returns the item id
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the item id
     *
     * @param id
     *            id to set
     */
    public final void setId(String id) {
        mId = id;
    }

    public String getCancerProbability() {
        return cancerProbability;
    }

    public void setCancerProbability(String cancerProbability) {
        this.cancerProbability = cancerProbability;
    }

    public String getTemperatureDifference() {
        return temperatureDifference;
    }

    public void setTemperatureDifference(String temperatureDifference) {
        this.temperatureDifference = temperatureDifference;
    }

    public String getPainIntensity() {
        return painIntensity;
    }

    public void setPainIntensity(String painIntensity) {
        this.painIntensity = painIntensity;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * Indicates if the item is marked as completed
     */
    public boolean isComplete() {
        return mComplete;
    }

    /**
     * Marks the item as completed or incompleted
     */
    public void setComplete(boolean complete) {
        mComplete = complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(cancerProbability, report.cancerProbability) &&
                Objects.equals(temperatureDifference, report.temperatureDifference) &&
                Objects.equals(painIntensity, report.painIntensity) &&
                Objects.equals(response, report.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cancerProbability, temperatureDifference, painIntensity, response);
    }
}