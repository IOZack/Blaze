package com.example.zack.pismire;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flir.flironesdk.Device;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;


public class thermalimage extends Activity implements Device.Delegate, Device.StreamDelegate, FrameProcessor.Delegate {

    private FrameProcessor processor;
    private ImageView thermalImageView;
    private Bitmap thermalBitmap;
    private TextView textdata;
    int xcord = 0;
    int ycord =0;
    double incelsius = 0;
    double averageTemp = 0;
    int valuetemp = 0;
    double lengthof = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermalimage);
        thermalImageView = (ImageView)findViewById(R.id.thermalimageview);
        textdata = (TextView) findViewById(R.id.textdata);
        thermalImageView.setOnTouchListener(
                new RelativeLayout.OnTouchListener() {
                    public boolean onTouch(View v,
                                           MotionEvent m) {
                        if (m.getAction() == MotionEvent.ACTION_UP){
                            handleTouch(m);
                        }
                        return true;
                    }
                }
        );
        RenderedImage.ImageType blendedType = RenderedImage.ImageType.BlendedMSXRGBA8888Image;
        RenderedImage.ImageType radiometryType = RenderedImage.ImageType.ThermalRadiometricKelvinImage;

        processor = new FrameProcessor(this, this, EnumSet.of(blendedType,radiometryType));
        processor.setImagePalette(RenderedImage.Palette.Iron);
    }

    private void handleTouch(MotionEvent m) {

        xcord = (int)m.getRawX();
        ycord = (int)m.getRawY();

        /*RelativeLayout layout = (RelativeLayout) findViewById(R.id.thermalimageddd);
        ImageView arrow = new ImageView(this);

        arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(260, 260);
        params.leftMargin = xcord - 60;
        params.topMargin = ycord - 270;

        if(layout.getChildCount()>1){
            layout.removeViewAt(1);
        }
        layout.addView(arrow, params);*/

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.thermalimageddd);
        ImageView arrow = new ImageView(this);
        arrow.setImageDrawable(getResources().getDrawable(R.drawable.farmer));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(250, 250);
        params.leftMargin = xcord;
        params.topMargin = ycord;

        if(layout.getChildCount()>2){
            layout.removeViewAt(3);
        }

        textdata.setText("Temperature: "+(int)getcel(averageTemp));

        layout.addView(arrow, params);

        //Intent myIntent = new Intent();
        //myIntent.setClass(thermalimage.this, resultsthermal.class);
        //myIntent.putExtra("foundcels", incelsius);
        //startActivity(myIntent);

        //Toast.makeText(this, incelsius + " Celsius", Toast.LENGTH_SHORT).show();

        //Toast.makeText(this, incelsius + " pixels", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume(){
        super.onResume();
        Device.startDiscovery(this, this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Device.stopDiscovery();
    }

    @Override
    public void onRestart(){
        try {
            Device.startDiscovery(this, this);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        super.onRestart();
    }

    @Override
    public void onStop() {
        // We must unregister our usb receiver, otherwise we will steal events from other apps
        Device.stopDiscovery();
        super.onStop();
    }

    @Override
    public void onTuningStateChanged(Device.TuningState tuningState) {

    }

    @Override
    public void onAutomaticTuningChanged(boolean b) {

    }

    @Override
    public void onDeviceConnected(Device device) {
        device.startFrameStream(this);
    }

    @Override
    public void onDeviceDisconnected(Device device) {
    }

    @Override
    public void onFrameReceived(Frame frame) {
        processor.processFrame(frame);
    }

    @Override
    public void onFrameProcessed(RenderedImage renderedImage) {
        if (renderedImage.imageType() == RenderedImage.ImageType.BlendedMSXRGBA8888Image) {
            if (this.thermalBitmap == null
                    || (renderedImage.width() != thermalBitmap.getWidth()
                    || renderedImage.height() != thermalBitmap.getHeight())) {
                thermalBitmap = Bitmap.createBitmap(renderedImage.width(),
                        renderedImage.height(),
                        Bitmap.Config.ARGB_8888);
            }
            thermalBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(renderedImage.pixelData()));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    thermalImageView.setImageBitmap(thermalBitmap);

                }
            });

        }else if(renderedImage.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage){

            short[] shortPixels = new short[renderedImage.pixelData().length / 2];
            ByteBuffer.wrap(renderedImage.pixelData()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortPixels);

            /*for (int i = ycord - 50; i < ycord+50; i++) {
                for(int k= xcord - 50; k < xcord+50; k++){
                    if(k==xcord+49){
                        valuetemp = (int)shortPixels[(k*120)+i];
                        averageTemp += (((int)shortPixels[(k*120)+i]) - averageTemp) / ((double) i+k + 1);
                    }else {
                        averageTemp += (((int)shortPixels[(k*120)+i]) - averageTemp) / ((double) i+k + 1);
                    }

                }
            }*/

            for (int i = shortPixels.length/2; i < shortPixels.length; i++) {
                averageTemp += (((int)shortPixels[i]) - averageTemp) / ((double) i + 1);
            }


        }

    }

    public double getcel(double value){

        incelsius = (value/100) - 273.15;
        return incelsius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
