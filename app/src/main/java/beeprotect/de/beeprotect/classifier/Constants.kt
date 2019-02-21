package beeprotect.de.beeprotect.classifier

const val GRAPH_FILE_PATH = "file:///android_asset/model.pb"
//const val GRAPH_FILE_PATH = "file:///android_asset/mnist_optimized.pb"
const val LABELS_FILE_PATH = "file:///android_asset/labels.txt"

const val GRAPH_INPUT_NAME = "Placeholder"
const val GRAPH_OUTPUT_NAME = "model_outputs"
//const val GRAPH_OUTPUT_NAME = "output"

const val IMAGE_SIZE = 224
const val COLOR_CHANNELS = 3

const val ASSETS_PATH = "file:///android_asset/"