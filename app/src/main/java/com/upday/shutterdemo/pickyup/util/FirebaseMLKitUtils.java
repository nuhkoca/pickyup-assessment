package com.upday.shutterdemo.pickyup.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.upday.shutterdemo.pickyup.R;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirebaseMLKitUtils {

    private SharedPreferences sharedPreferences;
    private Context context;

    @Inject
    public FirebaseMLKitUtils(SharedPreferences sharedPreferences, Context context) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;
    }

    public void generateLabelsFromBitmap(final ImageView imageView) {
        if (!DeviceUtils.isEmulator()) {
            FirebaseVisionLabelDetectorOptions options =
                    new FirebaseVisionLabelDetectorOptions.Builder()
                            .setConfidenceThreshold(
                                    Float.parseFloat(sharedPreferences.getString(context.getString(R.string.confidence_key), context.getString(R.string.confidence_0_7_value))))
                            .build();

            Bitmap bitmap;

            //skip placeholders
            if (!(imageView.getDrawable() instanceof VectorDrawable)) {
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                bitmap = drawable.getBitmap();

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

                FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                        .getVisionLabelDetector(options);

                final StringBuilder labelBuilder = new StringBuilder();

                detector.detectInImage(image)
                        .addOnSuccessListener(firebaseVisionLabels -> {
                            for (FirebaseVisionLabel label : firebaseVisionLabels) {
                                labelBuilder.append(label.getLabel())
                                        .append(" - Confidence: ")
                                        .append(FormatUtils.formatFloat(label.getConfidence()))
                                        .append("\n");
                            }

                            if (labelBuilder.length() > 0) {
                                //remove the last whitespace
                                labelBuilder.deleteCharAt(labelBuilder.length() - 1);

                                AlertDialog dialog = new AlertDialog.Builder(imageView.getContext())
                                        .setTitle(context.getString(R.string.labels_title))
                                        .setMessage(labelBuilder.toString() + "\n\n\n" + context.getString(R.string.firebase_abide))
                                        .setPositiveButton(context.getString(R.string.ok_action_text), (dialog1, which) -> dialog1.dismiss())
                                        .setCancelable(false)
                                        .create();

                                dialog.show();
                            } else {
                                Toast.makeText(imageView.getContext(), context.getString(R.string.no_label_warning_text), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(imageView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(imageView.getContext(), context.getString(R.string.image_not_fully_loaded), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(imageView.getContext(), context.getString(R.string.emulator_detected), Toast.LENGTH_SHORT).show();
        }
    }
}