package com.example.textcapture;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.textcapture.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

public class OcrGraphic extends GraphicOverlay.Graphic {



    private int id ;
    private static final int TEXT_COLOR = Color.WHITE;
    private static final int REC_COLOR =Color.RED;
    private static Paint textPaint;
    private static Paint rectPaint;
    private final TextBlock text;
    public OcrGraphic(GraphicOverlay overlay,TextBlock text) {
        super(overlay);
        this.text=text;


        if(rectPaint != null ) {

            rectPaint = new Paint();
            rectPaint.setColor(REC_COLOR);
            rectPaint.setStyle(Paint.Style.STROKE);
            rectPaint.setStrokeWidth(4.0f);
        }

        if(textPaint != null ){

            textPaint = new Paint();
            textPaint.setColor(TEXT_COLOR);
            textPaint.setTextSize(35.0f);
        }

        postInvalidate();

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public void draw(Canvas canvas) {

        if(text == null ){
            return;
        }

        RectF rect = new RectF(text.getBoundingBox());
        rect=translateRect(rect);
        canvas.drawRect(rect,rectPaint);

        List<? extends Text> components=text.getComponents();

        for(Text currentText : components){
            float left = translateX(currentText.getBoundingBox().left);
            float bottom = translateY(currentText.getBoundingBox().bottom);
            canvas.drawText(text.getValue(),left,bottom,textPaint);

        }

    }

    @Override
    public boolean contains(float x, float y) {
        return false;
    }
}
