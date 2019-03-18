package beeprotect.de.beeprotect;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class TensorflowActivity extends AppCompatActivity {
    //GraphView graph;
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
    PieChart chart;
    List<Float>graphValues = new ArrayList<>();
    List<String>graphLabels = new ArrayList<>();
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private Intent pictureActionIntent = null;
    Bitmap bitmap;
    String selectedImagePath;
    Context tensorflowcontext;
    private static DecimalFormat df2 = new DecimalFormat(".##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tensorflow);

        tensorflowcontext = this;
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);
        mTextView = (TextView) findViewById(R.id.text);
        bSelectImage = (FloatingActionButton) findViewById(R.id.bSelectImage);
        mastitis = findViewById(R.id.mastitis_value);
        fibroadenoma = findViewById(R.id.fibroadenoma_value);
        gynecomastia = findViewById(R.id.gynecomastia_value);
        normal = findViewById(R.id.normal_value);
        cancer = findViewById(R.id.cancer_value);


        /*//BAR GRAPH
        graph = findViewById(R.id.graph);

        // set the viewport wider than the data, to have a nice view
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);
        graph.getViewport().setXAxisBoundsManual(false);*/

        chart = findViewById(R.id.chart1);
        chart.setUsePercentValues(false);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(3, 7, 3, 2);

        chart.setDragDecelerationFrictionCoef(0.95f);

        //chart.setCenterTextTypeface(tfLight);
        chart.setCenterText("Results ( % )");
        chart.setCenterTextSize(20.0f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(38f);
        chart.setTransparentCircleRadius(31f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        /*// add a selection listener
        chart.setOnChartValueSelectedListener(this);*/

        chart.animateY(1400, Easing.EaseInOutQuad);
        Legend l = chart.getLegend();
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(8f);
        l.setYEntrySpace(1f);
        l.setYOffset(1f);

        // entry label styling
        chart.setEntryLabelColor(Color.BLACK);
        //chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(15f);

        /*graphLabels.add("cancer");
        graphLabels.add("mastitis");
        graphLabels.add("normal");
        graphValues.add(20.0f);
        graphValues.add(40.0f);
        graphValues.add(40.0f);
        setData(graphValues,graphLabels);*/

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
                startDialog();
                //selectImageInGallery();
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
                    //int probability = Integer.valueOf(cancerResult);
                    TestData.newInstance().setCancerProbability(cancerResult+"%");
                //}

                Intent intent=new Intent(getApplicationContext(), Pain_Dialouge_Activity.class);
                startActivity(intent);
            }
        });


        new SimpleTooltip.Builder(this)
                .anchorView(bSelectImage)
                .text("Click to select your breast picture")
                .gravity(Gravity.TOP).textColor(Color.WHITE)
                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                .arrowColor(getResources().getColor(R.color.colorPrimary))
                .animated(true)
                .transparentOverlay(false)
                .build()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tensor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setData(List<Float> value, List<String> name) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        if(!chart.isEmpty()) {
            //chart.getData().clearValues();
            chart.clearValues();
            chart.clear();
            chart.invalidate();
        }

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        //for (int i = 0; i < count ; i++) {
            /*entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
                    //parties[i % parties.length],
                    getResources().getDrawable(R.drawable.ic_bluetooth_)));*/
        for (int i=0;i<value.size();i++) {
            PieEntry e = new PieEntry(value.get(i), name.get(i));
            entries.add(e);
        }
        //}

        PieDataSet dataSet = new PieDataSet(entries, "Types");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(7f);
        //dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(15f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        //data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        //data.setValueTypeface(tfLight);
        chart.setData(data);


        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
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
            super.onActivityResult(requestCode, resultCode, data);

            bitmap = null;
            selectedImagePath = null;

            if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }

                if (!f.exists()) {

                    Toast.makeText(getBaseContext(),

                            "Error while capturing image", Toast.LENGTH_LONG)

                            .show();

                    return;

                }

                try {


                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                    //bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);

                    int rotate = 0;
                    try {
                        ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotate = 270;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotate = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotate = 90;
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotate);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);



                    imageView.setImageBitmap(bitmap);
                    getCloudData();

                    //storeImageTosdCard(bitmap);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
                if (data != null) {

                    Uri selectedImage = data.getData();
                    String[] filePath = { MediaStore.Images.Media.DATA };
                    Cursor c = getContentResolver().query(selectedImage, filePath,
                            null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    selectedImagePath = c.getString(columnIndex);
                    c.close();

                    if (selectedImagePath != null) {
                        //txt_image_path.setText(selectedImagePath);
                    }

                    bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                    // preview image
                    //bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);

                    imageView.setImageBitmap(bitmap);

                    getCloudData();

                } else {
                    Toast.makeText(getApplicationContext(), "Cancelled",
                            Toast.LENGTH_SHORT).show();
                }
            }

        /*if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
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
                    headers.put("Prediction-Key","e2803d8e0ff54f1cb5715f33522ef3a4");
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

        }*/
    }

    private void getCloudData() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byteArray = stream.toByteArray();
        //photo.recycle();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://southcentralus.api.cognitive.microsoft.com/customvision/v2.0/Prediction/2afe58df-39d2-414c-b9e7-689884095147/image";
        //String url = "https://southcentralus.api.cognitive.microsoft.com/customvision/v2.0/Prediction/2afe58df-39d2-414c-b9e7-689884095147/url?iterationId=618516c8-383f-428a-b9ca-122a57560de9/image";
        String requestBody = bitmap.toString();
        JSONObject jsonBody = new JSONObject();


        StringRequest myReq = new StringRequest(Request.Method.POST,
                url,createMyReqSuccessListener(),
                createMyReqErrorListener())
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
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
                headers.put("Prediction-Key","e2803d8e0ff54f1cb5715f33522ef3a4");
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

    private void storeImageTosdCard(Bitmap processedBitmap) {
        try {
            // TODO Auto-generated method stub

            OutputStream output;
            // Find the SD Card path
            File filepath = Environment.getExternalStorageDirectory();
            // Create a new folder in SD Card
            File dir = new File(filepath.getAbsolutePath() + "/appName/");
            dir.mkdirs();

            String imge_name = "appName" + System.currentTimeMillis()
                    + ".jpg";
            // Create a name for the saved image
            File file = new File(dir, imge_name);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else {
                file.createNewFile();

            }

            try {

                output = new FileOutputStream(file);

                // Compress into png format image from 0% - 100%
                processedBitmap
                        .compress(Bitmap.CompressFormat.PNG, 100, output);
                output.flush();
                output.close();

                int file_size = Integer
                        .parseInt(String.valueOf(file.length() / 1024));
                System.out.println("size ===>>> " + file_size);
                System.out.println("file.length() ===>>> " + file.length());

                selectedImagePath = file.getAbsolutePath();



            }

            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                    /*BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                            new DataPoint(2, 3),
                            new DataPoint(3, 2)
                    });*/
                    DataPoint[] points = new DataPoint[array.length()];

                    List<Integer> list = new ArrayList<>();
                    if(!graphValues.isEmpty()) {
                        graphValues.clear();
                        graphLabels.clear();
                    }
                    boolean isBreast = false,isNormal = true;
                    for (int i = 1; i < array.length(); i++) {
                        String probabilityString = new DecimalFormat("##").format(Double.valueOf(array.getJSONObject(i).getDouble("probability")*100) );
                        diseasetype = array.getJSONObject(i).getString("tagName");
                        Log.d("probability",probabilityString);
                        Log.d("type",diseasetype);
                        if (diseasetype.equalsIgnoreCase("cancer"))
                            cancerResult=probabilityString;
                        //btns[i-1].setText(probabilityString + "%");
                        //btns[i+4].setText(diseasetype);
                        double d = Double.valueOf(df2.format(array.getJSONObject(i).getDouble("probability") * 100 ));
                        if( diseasetype.equalsIgnoreCase("notbreasts") && d > 10d ||  diseasetype.equalsIgnoreCase("breasts") && d < 30d ){
                            isBreast = false;
                            break;
                            //Toast.makeText(tensorflowcontext, "BREAST NOT FOUND", Toast.LENGTH_SHORT).show();
                            //break;
                        }/*else if( diseasetype.equalsIgnoreCase("breasts") && d < 30d  ){
                            isBreast = false;
                            break;
                        }*/
                        else {
                            isBreast = true;
                        }
                        if ( diseasetype.equalsIgnoreCase("normal") && d < 30d ){
                            isNormal = false;
                            break;
                        }
                        if (d < 10d && d > 2d) {
                            //graphLabels.add(diseasetype.substring(0,2));
                            graphLabels.add(diseasetype);
                            graphValues.add((float) d);
                        }else if (d >= 10d) {
                            graphLabels.add(diseasetype);
                            graphValues.add((float) d);
                        }else {
                            //break;
                        }

                        list.add(Integer.valueOf(probabilityString));
                        //points[i-1] = new DataPoint(i+2, Double.valueOf(array.getJSONObject(i).getDouble("probability")));
                        //series.appendData(point,true,100);
                    }

/*                  graphLabels.add("cancer");
                    graphLabels.add("mastitis");
                    graphLabels.add("normal");
                    graphValues.add(20.0f);
                    graphValues.add(40.0f);
                    graphValues.add(40.0f);         */
                    if(!chart.isEmpty()) {
                        //chart.getData().clearValues();
                        chart.clearValues();
                        chart.clear();
                        chart.invalidate();
                    }
                    if (isBreast) {
//                    if(true){    //if (!isNormal)
                        setData(graphValues, graphLabels);
                    }
                    else
                        Toast.makeText(tensorflowcontext, "Breast is not detected", Toast.LENGTH_SHORT).show();
                    //populateGraph();

                    DataPoint [] statsArray;
                    if(list.size() > 0) {
                        // Code for list longer than 0, query return something
                        statsArray = new DataPoint[list.size()]; // so this is not null now
                        for (int i = 0; i < statsArray.length; i++) {
                            statsArray[i] = new DataPoint(i+1, list.get(i));
                            // i+1  to start from x = 1
                        }
                    }else{
                        // Query return nothing, so we add some fake point
                        // IT WON'T BE VISIBLE cus we starts graph from 1
                        statsArray = new DataPoint[] {new DataPoint(0, 0)};
                    }
                    /*BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>();*/

                    /*int dataptr = 0;
                    for (dataptr=0;dataptr<=points.length;dataptr++){
                        series.appendData(points[dataptr],false,points.length);
                    }*/
                    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(statsArray);

                    series.setSpacing(40); // 50% spacing between bars
                    series.setAnimated(true);
                    //graph.addSeries(series);

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


    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                tensorflowcontext);
        myAlertDialog.setTitle("Select breast picture");
        myAlertDialog.setMessage("How do you want to select the picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;
                        if (ActivityCompat.checkSelfPermission(TensorflowActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(TensorflowActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_PICTURE);
                        } else {
                            pictureActionIntent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(
                                    pictureActionIntent,
                                    GALLERY_PICTURE);
                        }

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        if (ActivityCompat.checkSelfPermission(TensorflowActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(TensorflowActivity.this, new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST );
                        } else {
                            Intent intent = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(android.os.Environment
                                    .getExternalStorageDirectory(), "temp.jpg");
                            /*intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(f));*/
                            //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(f));
                            //Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile());

                            startActivityForResult(intent,
                                    CAMERA_REQUEST);
                        }

                    }
                });

        AlertDialog myAlertDiag=myAlertDialog.create();

        myAlertDiag.show();

        Button bq = myAlertDiag.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setBackgroundColor(Color.YELLOW);
        bq.setTextColor(Color.WHITE);
        Button bp = myAlertDiag.getButton(DialogInterface.BUTTON_POSITIVE);
        bp.setBackgroundColor(Color.GREEN);
        bp.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20,0,0,0);
        bp.setLayoutParams(params);

        /*myAlertDiag.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button negativeButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                Button positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getResources().getColor(R.color.colorSlide_1));
                negativeButton.setTextColor(getResources().getColor(R.color.colorSlide_1));
                // this not working because multiplying white background (e.g. Holo Light) has no effect
                //negativeButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);

                final Drawable negativeButtonDrawable = getResources().getDrawable(R.drawable.alert_dialog_button_light_red);
                final Drawable positiveButtonDrawable = getResources().getDrawable(R.drawable.alert_dialog_button_light_red);
                if (Build.VERSION.SDK_INT >= 16) {
                    negativeButton.setBackground(negativeButtonDrawable);
                    positiveButton.setBackground(positiveButtonDrawable);
                } else {
                    negativeButton.setBackgroundDrawable(negativeButtonDrawable);
                    positiveButton.setBackgroundDrawable(positiveButtonDrawable);
                }

                negativeButton.invalidate();
                positiveButton.invalidate();
            }
        });
        myAlertDialog.show();*/

    }

    public void selectImageInGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        }
    }
}
