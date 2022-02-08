package org.tensorflow.lite.examples.boxdet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.tensorflow.lite.examples.boxdet.customview.OverlayView;
import org.tensorflow.lite.examples.boxdet.env.ImageUtils;
import org.tensorflow.lite.examples.boxdet.env.Logger;
import org.tensorflow.lite.examples.boxdet.env.Utils;
import org.tensorflow.lite.examples.boxdet.tflite.Classifier;
import org.tensorflow.lite.examples.boxdet.tflite.YoloV4Classifier;
import org.tensorflow.lite.examples.boxdet.tracking.MultiBoxTracker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;

public class MainActivity extends AppCompatActivity {

    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    public static final String EXTRA_MESSAGE = "org.tensorflow.lite.examples.boxdet.MESSAGE";

    private ArrayAdapter<CharSequence> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        cameraButton = findViewById(R.id.cameraButton);
        detectButton = findViewById(R.id.detectButton);
        imageView = findViewById(R.id.imageView);

        spinner = findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_string,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        model_idx = 0;
        spinner.setSelection(model_idx, false);
        spinner.setOnItemSelectedListener(spnOnItemSelected);

        cameraButton.setOnClickListener(v ->
        {
            /*startActivity(new Intent(MainActivity.this, DetectorActivity.class))*/
            Intent intent = new Intent(MainActivity.this, DetectorActivity.class);
            intent.putExtra(EXTRA_MESSAGE, model_idx);
            startActivity(intent);
        });

        detectButton.setOnClickListener(v -> {
            cropBitmap = Utils.processBitmap(sourceBitmap, TF_OD_API_INPUT_SIZE);
            initBox();
            Handler handler = new Handler();

            new Thread(() -> {
                final List<Classifier.Recognition> results = detector.recognizeImage(cropBitmap);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleResult(cropBitmap, results);
                    }
                });
            }).start();

        });
        //this.sourceBitmap = Utils.getBitmapFromAsset(MainActivity.this, "kite.jpg");
        this.sourceBitmap = Utils.getBitmapFromAsset(MainActivity.this, "box.jpg");

        this.cropBitmap = Utils.processBitmap(sourceBitmap, TF_OD_API_INPUT_SIZE);

        this.imageView.setImageBitmap(cropBitmap);

        //initBox();
    }

    private AdapterView.OnItemSelectedListener spnOnItemSelected
            = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            model_idx = pos;
        }
        public void onNothingSelected(AdapterView<?> parent) {
            //
        }
    };

    private static final Logger LOGGER = new Logger();

    public static final int TF_OD_API_INPUT_SIZE = 416;

    private static final boolean TF_OD_API_IS_QUANTIZED = false;

//    private static final String TF_OD_API_MODEL_FILE = "yolov4-416-fp32.tflite";
//    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco.txt";

    // "yolov4-tiny-416.tflite" or yolov4-tiny-416xBig.tflite
    private int model_idx;
    private static final String TF_OD_API_MODEL_FILE = "yolov4-tiny-416.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/custom_3_class.txt";

    // Minimum detection confidence to track a detection.
    private static final boolean MAINTAIN_ASPECT = false;
    private Integer sensorOrientation = 90;

    private Classifier detector;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private MultiBoxTracker tracker;
    private OverlayView trackingOverlay;

    protected int previewWidth = 0;
    protected int previewHeight = 0;

    private Bitmap sourceBitmap;
    private Bitmap cropBitmap;

    private Button cameraButton, detectButton;
    private ImageView imageView;
    private Spinner spinner;

    private int input_size;
    private void initBox() {
        previewHeight = TF_OD_API_INPUT_SIZE;
        previewWidth = TF_OD_API_INPUT_SIZE;
        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        tracker = new MultiBoxTracker(this);
        trackingOverlay = findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                canvas -> tracker.draw(canvas));

        tracker.setFrameConfiguration(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, sensorOrientation);

        try {
            String[] model_name = this.getResources().getStringArray(R.array.tflite_file);
            String[] size_str = this.getResources().getStringArray(R.array.image_size);
            String[] label_str = this.getResources().getStringArray(R.array.label_file);
            int image_size = Integer.valueOf(size_str[model_idx]);
            Log.e("Awei","file Name:" +  model_name[model_idx]);
            detector =
                    YoloV4Classifier.create(
                            getAssets(),
                            model_name[model_idx],//TF_OD_API_MODEL_FILE,
                            label_str[model_idx],//TF_OD_API_LABELS_FILE,
                            TF_OD_API_IS_QUANTIZED,
                            image_size);
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    private void handleResult(Bitmap bitmap, List<Classifier.Recognition> results) {
        final Canvas canvas = new Canvas(bitmap);
        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);

        Log.e("Awei","results:" + results.size());

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            Log.e("Awei","level:"+result.getConfidence());
            if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API) {
                canvas.drawRect(location, paint);
//                cropToFrameTransform.mapRect(location);
//
//                result.setLocation(location);
//                mappedRecognitions.add(result);
            }
        }
//        tracker.trackResults(mappedRecognitions, new Random().nextInt());
//        trackingOverlay.postInvalidate();
        imageView.setImageBitmap(bitmap);
    }
}
