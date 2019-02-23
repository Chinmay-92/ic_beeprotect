package beeprotect.de.beeprotect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class TensorflowActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_SELECT_IMAGE = 0;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    TextView mTextView;
    String cancerResult="0";
    TextView mastitis,fibroadenoma,gynecomastia,normal,cancer;
    TextView mastitislbl,fibroadenomalbl,gynecomastialbl,normallbl,cancerlbl;
    byte[] byteArray;
    private TextView[] btns = new TextView[10];
    private FloatingActionButton bSelectImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tensorflow);

        mTextView = (TextView) findViewById(R.id.text);
        bSelectImage = (FloatingActionButton) findViewById(R.id.bSelectImage);
        mastitis = findViewById(R.id.mastitis_value);
        fibroadenoma = findViewById(R.id.fibroadenoma_value);
        gynecomastia = findViewById(R.id.gynecomastia_value);
        normal = findViewById(R.id.normal_value);
        cancer = findViewById(R.id.cancer_value);

        btns[0]=mastitis;
        btns[1]=fibroadenoma;
        btns[2]=gynecomastia;
        btns[3]=normal;
        btns[4]=cancer;


        mastitislbl = findViewById(R.id.mastitis);
        fibroadenomalbl = findViewById(R.id.fibroadenoma);
        gynecomastialbl = findViewById(R.id.gynecomastia);
        normallbl = findViewById(R.id.normal);
        cancerlbl = findViewById(R.id.cancer);

        btns[5]=mastitislbl;
        btns[6]=fibroadenomalbl;
        btns[7]=gynecomastialbl;
        btns[8]=normallbl;
        btns[9]=cancerlbl;


        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);

        bSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageInGallery();
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("testdata",cancerResult);
                /*if (cancerResult.getText() == null || btns[0].getText().toString().equalsIgnoreCase("")) {
                    TestData.newInstance().setCancerProbability("0");
                } else if (cancer.getText().toString().contains("%")){
                    TestData.newInstance().setCancerProbability(btns[0].getText().toString().split("%")[0]);
                }else {
                */
                    int probability = Integer.valueOf(cancerResult);
                    TestData.newInstance().setCancerProbability(probability+"");
                //}

                Intent intent=new Intent(getApplicationContext(), Pain_Dialouge_Activity.class);
                startActivity(intent);
            }
        });
    }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == MY_CAMERA_PERMISSION_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new
                            Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }

            }
        }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri imgUri = data.getData();
            Bitmap photo = null;
           try{
               photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
            imageView.setImageBitmap(photo);}
            catch(Exception e)
            {

            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byteArray = stream.toByteArray();
            //photo.recycle();

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://southcentralus.api.cognitive.microsoft.com/customvision/v2.0/Prediction/2afe58df-39d2-414c-b9e7-689884095147/image";
            String requestBody = photo.toString();
            JSONObject jsonBody = new JSONObject();


            StringRequest myReq = new StringRequest(Request.Method.POST,
                    url,createMyReqSuccessListener(),
                    createMyReqErrorListener())
            {
                @Override
                public byte[] getBody() throws com.android.volley.AuthFailureError {
                    //String str = "";
                    return byteArray;
                };

                public String getBodyContentType()
                {
                    return "application/json; charset=utf-8";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Prediction-Key","3f8680d0ce2742fcbe3190a222dc6113");
                    //connection.setRequestProperty("Content-lenght", postData.size.toString())
                    headers.put("Content-Type", "application/octet-stream");

                    //headers.put("Authorization", "Basic " + "c2FnYXJAa2FydHBheS5jb206cnMwM2UxQUp5RnQzNkQ5NDBxbjNmUDgzNVE3STAyNzI=");//put your token here
                    return headers;
                }
            };
            //VolleyApplication.getInstance().addToRequestQueue(jsonOblect);
            queue.add(myReq);

            // Add the request to the RequestQueue.
            //queue.add(stringRequest);

        }
    }
    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("data","Ski data from server - "+response);
                try {
                    double probability = 0f;
                    String diseasetype = "breasts";
                    JSONObject jsonresponse = new JSONObject(response);
                    JSONArray array = jsonresponse.getJSONArray("predictions");
                    for (int i = 1; i < array.length(); i++) {
                        String probabilityString = new DecimalFormat("##").format(Double.valueOf(array.getJSONObject(i).getDouble("probability")) * 100);
                        diseasetype = array.getJSONObject(i).getString("tagName");
                        Log.d("probability",probabilityString);
                        Log.d("type",diseasetype);
                        if (diseasetype.equalsIgnoreCase("cancer"))
                            cancerResult=probabilityString;
                        btns[i-1].setText(probabilityString + "%");
                        btns[i+4].setText(diseasetype);

                    }
                }catch(JSONException je){
                    je.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("data","Ski error connect - "+error);
            }
        };
    }

    public void selectImageInGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        }
    }
}
