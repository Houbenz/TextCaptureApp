package com.example.textcapture;

import android.util.Log;
import android.util.SparseArray;

import com.example.textcapture.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;

public class OcrProcessorDetector implements Detector.Processor<TextBlock> {


    private GraphicOverlay<OcrGraphic> graphicOverlay;
    OcrProcessorDetector(GraphicOverlay<OcrGraphic> graphicOverlay){
        this.graphicOverlay=graphicOverlay;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {

        SparseArray<TextBlock> items=detections.getDetectedItems();

        for (int i = 0 ; i< items.size();i++){
            TextBlock item = items.valueAt(i);

            if (item != null && item.getValue() != null){

                Log.d("Processor","Text detected ! " + item.getValue());
                OcrGraphic graphic = new OcrGraphic(graphicOverlay,item);
                graphicOverlay.add(graphic);
            }

        }

    }


    @Override
    public void release() {

        graphicOverlay.clear();
    }


}
