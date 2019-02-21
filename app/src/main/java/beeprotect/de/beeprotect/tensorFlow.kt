package beeprotect.de.beeprotect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import beeprotect.de.beeprotect.classifier.*
import beeprotect.de.beeprotect.utils.getCroppedBitmap
import beeprotect.de.beeprotect.utils.getUriFromFilePath
import kotlinx.android.synthetic.main.activity_tensor_flow.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

private const val REQUEST_PERMISSIONS = 1
private const val REQUEST_TAKE_PICTURE = 2

class tensorFlow : AppCompatActivity() {

    private val handler = Handler()
    private lateinit var classifier: Classifier
    private var photoFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tensor_flow)
        checkPermissions()
    }

    private fun checkPermissions() {
        if (arePermissionsAlreadyGranted()) {
            init()
        } else {
            requestPermissions()
        }
    }

    private fun arePermissionsAlreadyGranted() =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS && arePermissionGranted(grantResults)) {
            init()
        } else {
            requestPermissions()
        }
    }

    private fun arePermissionGranted(grantResults: IntArray) =
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

    private fun init() {
        createClassifier()
        takePhoto()
    }
    private fun createClassifier() {
        classifier = ImageClassifierFactory.create(
                assets,
                GRAPH_FILE_PATH,
                LABELS_FILE_PATH,
                IMAGE_SIZE,
                GRAPH_INPUT_NAME,
                GRAPH_OUTPUT_NAME
        )
    }

    private fun takePhoto() {
        photoFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/${System.currentTimeMillis()}.jpg"
        val currentPhotoUri = getUriFromFilePath(this, photoFilePath)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        takePictureIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.take_photo) {
            takePhoto()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val file = File(photoFilePath)
        if (requestCode == REQUEST_TAKE_PICTURE && file.exists()) {
            classifyPhoto(file)
        }
    }

    private fun classifyPhoto(file: File) {
        val photoBitmap = BitmapFactory.decodeFile(file.absolutePath)
        val croppedBitmap = getCroppedBitmap(photoBitmap)
        //classifyAndShowResult(croppedBitmap)
        pushToChat(croppedBitmap);
        imagePhoto.setImageBitmap(croppedBitmap)
    }

    fun pushToChat(message: Bitmap) {
        val serverURL: String = "https://southcentralus.api.cognitive.microsoft.com/customvision/v2.0/Prediction/2afe58df-39d2-414c-b9e7-689884095147/image"
        val url = URL(serverURL)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 300000
        connection.connectTimeout = 300000
        connection.doOutput = true

        val bitmap = message
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()

        val postData: ByteArray = image

        //connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Prediction-Key","3f8680d0ce2742fcbe3190a222dc6113");
        //connection.setRequestProperty("Content-lenght", postData.size.toString())
        connection.setRequestProperty("Content-Type", "application/octet-stream")
        val outputStream: DataOutputStream
        try {
            outputStream = DataOutputStream(connection.outputStream)
            outputStream.write(postData)
            outputStream.flush()
        } catch (exception: Exception) {

        }

        if (connection !=null && connection.responseCode != HttpURLConnection.HTTP_OK && connection.responseCode != HttpURLConnection.HTTP_CREATED) {
            try {
                var inputStream: InputStream = connection.inputStream
                println(connection.responseMessage)
                val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
                val output: String = reader.readLine()

                println("There was error while connecting the chat $output")
                System.exit(0)

            } catch (exception: Exception) {
                throw Exception("Exception while push the notification  $exception.message")
            }
        }

    }


    private fun classifyAndShowResult(croppedBitmap: Bitmap) {
        runInBackground(
                Runnable {
                    val result = classifier.recognizeImage(croppedBitmap)
                    showResult(result)
                })
    }

    @Synchronized
    private fun runInBackground(runnable: Runnable) {
        handler.post(runnable)
    }

    private fun showResult(result: Result) {
        Log.d("test",result.toString());
        cancer_type.text = result.result;
        textResult.text = result.confidence.toString();
        //layoutContainer.setBackgroundColor(getColorFromResult(result.result))
    }

    @Suppress("DEPRECATION")
    private fun getColorFromResult(result: String): Int {
        return if (result == getString(R.string.breasts)) {
            resources.getColor(R.color.breasts)
        } else if(result == getString(R.string.cancer)) {
           resources.getColor(R.color.cancer)
        } else if(result == getString(R.string.fibroadenoma)) {
            resources.getColor(R.color.fibroadenoma)
        } else if (result == getString(R.string.gynecomastia)) {
            resources.getColor(R.color.gynecomastia)
        } else if (result == getString(R.string.mastitis)) {
            resources.getColor(R.color.mastitis)
        } else if (result == getString(R.string.normal)) {
            resources.getColor(R.color.normal)
        } else
            resources.getColor(R.color.not)
    }


}
