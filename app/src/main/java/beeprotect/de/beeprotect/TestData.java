package beeprotect.de.beeprotect;

import android.os.Bundle;

import java.io.Serializable;
import java.text.DecimalFormat;

public class TestData implements Serializable {
    static TestData testdata;
    String cancerProbability = "0";
    String temperatureDifference = "0";
    String painIntensity = "0";
    String response;

    public static TestData newInstance() {
        if (testdata == null )
            testdata = new TestData();
        return testdata;
    }

    public static void setTestdata(TestData testdata) {
        TestData.testdata = testdata;
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