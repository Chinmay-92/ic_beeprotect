package beeprotect.de.beeprotect;

import android.os.Bundle;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TestData implements Serializable {
    static TestData testdata;
    private List<TestData> allreports = new ArrayList<>();
    @com.google.gson.annotations.SerializedName("cancerProbability")
    String cancerProbability = "0";

    @com.google.gson.annotations.SerializedName("temperatureDifference")
    String temperatureDifference = "0";

    @com.google.gson.annotations.SerializedName("createdAt")
    String createdAt;
    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("painIntensity")
    String painIntensity = "0";

    @com.google.gson.annotations.SerializedName("response")
    String response;

    public static TestData newInstance() {
        if (testdata == null )
            testdata = new TestData();
        return testdata;
    }

    public List<TestData> getAllreports() {
        return allreports;
    }

    public void setAllreports(List<TestData> allreports) {
        this.allreports = allreports;
    }

    public void addreport(TestData testData){
        this.allreports.add(testData);
    }

    public static void setTestdata(TestData testdata) {
        TestData.testdata = testdata;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public void setTemperatureDifference(double temperatureDifference) {
        String tempdiff = new DecimalFormat("#.###").format(Double.valueOf(temperatureDifference));
        this.temperatureDifference = tempdiff;
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

    @Override
    public String toString() {
        return "TestData{" +
                "cancerProbability='" + cancerProbability + '\'' +
                ", temperatureDifference='" + temperatureDifference + '\'' +
                ", painIntensity='" + painIntensity + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}