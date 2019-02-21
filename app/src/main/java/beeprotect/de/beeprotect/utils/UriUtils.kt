package beeprotect.de.beeprotect.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File


fun getUriFromFilePath(context: Context, filePath: String): Uri {
    val file = File(filePath)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(
                context,
                "beeprotect.de.beeprotect.uri",
                file)
    } else {
        Uri.fromFile(file)
    }
}