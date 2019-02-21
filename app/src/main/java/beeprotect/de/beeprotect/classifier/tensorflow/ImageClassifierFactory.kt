package beeprotect.de.beeprotect;

import android.content.res.AssetManager
import beeprotect.de.beeprotect.classifier.COLOR_CHANNELS
import beeprotect.de.beeprotect.classifier.Classifier
import beeprotect.de.beeprotect.classifier.tensorflow.ImageClassifier
import beeprotect.de.beeprotect.utils.getLabels
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

object ImageClassifierFactory {

    fun create(
            assetManager: AssetManager,
            graphFilePath: String,
            labelsFilePath: String,
            imageSize: Int,
            inputName: String,
            outputName: String
    ): Classifier {

        val labels = getLabels(assetManager, labelsFilePath)

        return ImageClassifier(
                inputName,
                outputName,
                imageSize.toLong(),
                labels,
                IntArray(imageSize * imageSize),
                FloatArray(imageSize * imageSize * COLOR_CHANNELS),
                FloatArray(labels.size),
                TensorFlowInferenceInterface(assetManager, graphFilePath)
        )
    }
}