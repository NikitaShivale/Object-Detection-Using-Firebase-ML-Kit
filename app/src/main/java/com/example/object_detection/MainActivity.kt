package com.example.object_detection



import com.otaliastudios.cameraview.Frame


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.graphics.*
import android.util.Log.*
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObject.Category
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import com.google.firebase.ml.vision.objects.ObjectDetectorCreator
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer

import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Object as LangObject

import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel



class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main)
        camera.setLifecycleOwner(this)
        camera.addFrameProcessor {
        extractDataFromFrame(it) { cat ->
            tvDetectedItem.text = cat

        }

        }
    }

private fun extractDataFromFrame(frame: Frame, callback: (String) -> Unit) {

    val options = FirebaseVisionObjectDetectorOptions.Builder()
        .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
        .enableMultipleObjects()
        .enableClassification()
        .build()

    val objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options)

    objectDetector.processImage(getVisionImageFromFrame(frame))

        .addOnSuccessListener {

            it.forEach { item ->

                val LOG_MOD = "MLKit-ODT"
                for ((idx, obj) in it.withIndex()) {
                    val box = obj.boundingBox
                    d(LOG_MOD, "Detected object")
                    d(LOG_MOD, "  Category: ${obj.classificationCategory}")
                    d(LOG_MOD, "  trackingId: ${obj.trackingId}")
                    //d(LOG_MOD, "  entityId: ${obj.entityId}")
                    d(LOG_MOD, "  boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")



                    if (obj.classificationCategory == 0)

                        println("Object Belongs to Unknown Category")
                    else if (obj.classificationCategory == 1)
                        println("Object belongs to Home-Goods Category")
                    else if (obj.classificationCategory == 2)
                        println("Object belongs to Fashion-Goods Category")
                    else if (obj.classificationCategory == 3)
                        println("Object belongs to Food Category")
                    else if (obj.classificationCategory == 4)
                        println("Object belongs to Place Category")
                    else if (obj.classificationCategory == 5)
                        println("Object belongs to Plant Category")



                    val categoryNames: Map<Int, String> = mapOf(
                        FirebaseVisionObject.CATEGORY_UNKNOWN to "Unknown",
                        FirebaseVisionObject.CATEGORY_HOME_GOOD to "Home Goods",
                        FirebaseVisionObject.CATEGORY_FASHION_GOOD to "Fashion Goods",
                        FirebaseVisionObject.CATEGORY_FOOD to "Food",
                        FirebaseVisionObject.CATEGORY_PLACE to "Place",
                        FirebaseVisionObject.CATEGORY_PLANT to "Plant"
                    )

                    val tags: MutableList<String> = mutableListOf()
                    tags.add("Category: ${categoryNames[item.classificationCategory]}")
                    if (item.classificationCategory !=
                        FirebaseVisionObject.CATEGORY_UNKNOWN
                    ) {
                        tags.add("Confidence: ${item.classificationConfidence!!.times(100).toInt()}%")
                    }

                    if (obj.classificationCategory != FirebaseVisionObject.CATEGORY_UNKNOWN) {
                        val confidence: Int = obj.classificationConfidence!!.times(100).toInt()
                        d(LOG_MOD, "  Confidence: ${confidence}%")
                    }


                    //callback(entityId)
                    callback(item.classificationCategory.toString())

                }
                }
            }
                .addOnFailureListener {
                    callback("Unable to detect an object")
                }


        }


    private fun getVisionImageFromFrame(frame : Frame) : FirebaseVisionImage{

        val data = frame.data


    val imageMetaData = FirebaseVisionImageMetadata.Builder()
        .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
        .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
        .setHeight(frame.size.height)
        .setWidth(frame.size.width)
        .build()

    val image = FirebaseVisionImage.fromByteArray(data, imageMetaData)

    return image
    }
}






