package beeprotect.de.beeprotect;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TestData implements Serializable {
    static TestData testdata;
    private List<Report> allreports = new ArrayList<>();
//    @com.google.gson.annotations.SerializedName("cancerProbability")
    public String cancerProbability = "0";

//    @com.google.gson.annotations.SerializedName("temperatureDifference")
    public String temperatureDifference = "0";

    public String duration = "";
    /*@com.google.gson.annotations.SerializedName("createdAt")
    String createdAt;*/
    /**
     * Item Id
     */
    @SerializedName("id")
    public String id;

//    @com.google.gson.annotations.SerializedName("painIntensity")
    public String painIntensity = "0";

//    @com.google.gson.annotations.SerializedName("response")
    public String response;

    public static TestData newInstance() {
        if (testdata == null )
            testdata = new TestData();
        return testdata;
    }

    public TestData(){

    }
    public TestData(String s, String test, String test1, String s1) {
        this.cancerProbability = cancerProbability;
        this.temperatureDifference = temperatureDifference;
        this.painIntensity = painIntensity;
        this.response = response;
    }

    public static TestData getTestdata() {
        return testdata;
    }

    public TestData(String cancerProbability, String temperatureDifference, String id, String painIntensity, String response) {
        this.cancerProbability = cancerProbability;
        this.temperatureDifference = temperatureDifference;
        this.id = id;
        this.painIntensity = painIntensity;
        this.response = response;
    }

    public void setTemperatureDifference(String temperatureDifference) {
        this.temperatureDifference = temperatureDifference;
    }

    public String getmId() {
        return id;
    }

    public void setmId(String mId) {
        this.id = mId;
    }

    public List<Report> getAllreports() {
        return allreports;
    }

    public void setAllreports(List<Report> allreports) {
        this.allreports = allreports;
    }

    public void addreport(Report testData){
        this.allreports.add(testData);
    }

    public static void setTestdata(TestData testdata) {
        TestData.testdata = testdata;
    }

    /*public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }*/

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
                "allreports=" + allreports +
                ", cancerProbability='" + cancerProbability + '\'' +
                ", temperatureDifference='" + temperatureDifference + '\'' +
                ", id='" + id + '\'' +
                ", painIntensity='" + painIntensity + '\'' +
                ", response='" + response + '\'' +
                '}';
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }
}