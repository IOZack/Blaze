package com.example.zack.pismire;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
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


public class maxhot extends Activity implements Device.Delegate, Device.StreamDelegate, FrameProcessor.Delegate {

    private FrameProcessor processor;
    private ImageView thermalImageView;
    private Bitmap thermalBitmap;
    private TextView firefighter;
    int valuetemp = 0;
    int valuetempnew = 0;
    int valuetempold =0;
    int valli = 0;
    double vallx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maxhot);
        thermalImageView = (ImageView)findViewById(R.id.thermalimagemaxhot);

        firefighter = (TextView) findViewById(R.id.firefighterrr);

        RenderedImage.ImageType blendedType = RenderedImage.ImageType.BlendedMSXRGBA8888Image;
        RenderedImage.ImageType radiometryType = RenderedImage.ImageType.ThermalRadiometricKelvinImage;

        processor = new FrameProcessor(this, this, EnumSet.of(blendedType,radiometryType));
        processor.setImagePalette(RenderedImage.Palette.Iron);
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


            for (int i = 0; i < shortPixels.length; i++) {
                valuetemp = Math.max((int)shortPixels[i], valuetemp);
                /*if(valuetemp!=valuetempold){
                    valli = i;
                }
                valuetempold = valuetemp;*/
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    firefighter.setText("Temperature: " + (int)getcel(valuetemp));
                    //drawarrow((valuetemp-(valli*160+valuetemp)/160), valli*160+valuetemp);

                }
            });


        }

    }

    private void drawarrow(double x, double y){

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.thermalimagehot);
        ImageView arrow = new ImageView(this);
        arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(260, 260);
        params.leftMargin = (int)x + 130;
        params.topMargin = (int)y + 130;

        if(layout.getChildCount()>2){
            layout.removeViewAt(3);
        }

        layout.addView(arrow, params);
    }

    public double getcel(double value){
        return (value/100) - 273.15;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
