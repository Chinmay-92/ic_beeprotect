package beeprotect.de.beeprotect.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AzureMLUtil {

    public static String endPointURL = "https://ussouthcentral.services.azureml.net/workspaces/9190c4122e7e43319d3be5a196d998cd/services/7ade2ab91b98473bb0d7470c162d62d9/execute?api-version=2.0&details=true"; //Azure ML Endpoint
    public static String key = "3qTecAXhtsygGHEhHR6tz1Lbuqg7OxKUJdUQ4cAFC0Nh528MZpHLWRzorjkua+jCtOdHWWzjasfQZdDzVxbMvw=="; //API KEY
    public static String response = null;

    public AzureMLUtil(String endPointURL,String key)
    {
        this.endPointURL= endPointURL;
        this.key= key;
    }
    /*
     Takes an Azure ML Request Body then Returns the Response String Which Contains Scored Lables etc
    */
    public static String requestResponse( String requestBody ) throws Exception
    {
        URL u = new URL(endPointURL);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();

        conn.setRequestProperty("Authorization","Bearer "+ key);
        conn.setRequestProperty("Content-Type","application/json");
        conn.setRequestMethod("POST");

        String body= new String(requestBody);
        Log.d("MLUtil req body",body);
        conn.setDoOutput(true);
        OutputStreamWriter wr=new OutputStreamWriter(conn.getOutputStream());

        wr.write(body);
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String decodedString;
        //String responseString="";

        while ((decodedString = in.readLine()) != null)
        {
            response = decodedString;
        }
        return response;
    }

}
