package com.upday.shutterdemo.pickyup.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.upday.shutterdemo.pickyup.PickyUpApp;
import com.upday.shutterdemo.pickyup.R;

import java.util.List;

public class FirebaseMLKitUtils {

    public static void generateLabelsFromBitmap(final ImageView imageView, SharedPreferences sharedPreferences) {
        if (!DeviceUtils.isEmulator()) {
            FirebaseVisionLabelDetectorOptions options =
                    new FirebaseVisionLabelDetectorOptions.Builder()
                            .setConfidenceThreshold(
                                    Float.parseFloat(SharedPreferencesUtils.loadConfidencePreference(PickyUpApp.getInstance().getApplicationContext(), sharedPreferences)))
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
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionLabel> firebaseVisionLabels) {
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
                                            .setTitle(PickyUpApp.getInstance().getString(R.string.labels_title))
                                            .setMessage(labelBuilder.toString() + "\n\n\n" + PickyUpApp.getInstance().getString(R.string.firebase_abide))
                                            .setPositiveButton(PickyUpApp.getInstance().getString(R.string.ok_action_text), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setCancelable(false)
                                            .create();

                                    dialog.show();
                                } else {
                                    Toast.makeText(imageView.getContext(), PickyUpApp.getInstance().getString(R.string.no_label_warning_text), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(imageView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(imageView.getContext(), PickyUpApp.getInstance().getString(R.string.image_not_fully_loaded), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(imageView.getContext(), PickyUpApp.getInstance().getString(R.string.emulator_detected), Toast.LENGTH_SHORT).show();
        }
    }
}