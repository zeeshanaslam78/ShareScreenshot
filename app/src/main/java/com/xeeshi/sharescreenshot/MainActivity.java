package com.xeeshi.sharescreenshot;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.File;
import java.text.DecimalFormat;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    ImageView imgSelectedFromGallery;
    Button btnOpenGallery, btnSendEmail;
    ProgressBar progressBar;
    File selectedFile=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgSelectedFromGallery = findViewById(R.id.img_selected_from_gallery);
        btnOpenGallery = findViewById(R.id.btn_open_gallery);
        btnSendEmail = findViewById(R.id.btn_send_email);
        progressBar = findViewById(R.id.progressBar);

        btnSendEmail.setOnClickListener(view -> {

            if (null != selectedFile) {

                if (selectedFile.length() == 0) {
                    enableViews(true);
                    Toast.makeText(MainActivity.this, "Invalid File", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    enableViews(false);

                    String phoneInfo = getPhoneSpecs();

                    BackgroundMail.newBuilder(this)
                        // You can change gmail username and password and enable less secure apps before
                        // using this username and password
                        .withUsername("zeeshan.outfit7@gmail.com")
                        .withPassword("outfit7.ekipa2")

                        .withSenderName("Zeeshan Aslam")
                        // Change this email to whom you want to send email
                        .withMailTo("android.zeeshan@gmail.com")
                        .withType(BackgroundMail.TYPE_PLAIN)
                        .withSubject("Sending bugs")
                        .withBody(phoneInfo)
                        .withAttachments(selectedFile.getPath())
                        .withProcessVisibility(false)
                        .withOnSuccessCallback(() -> {

                            enableViews(true);

                            Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_LONG).show();
                            deleteAllFilesFromPicturesDir();
                            changeDefaultImage();
                            selectedFile = null;
                        })

                        .withOnFailCallback(() -> {
                            enableViews(true);
                            Toast.makeText(MainActivity.this, "onFail", Toast.LENGTH_LONG).show();

                        })
                        .send();
                } catch (Exception e) {
                    enableViews(true);
                    e.printStackTrace();
                }
            } else {
                enableViews(true);
                Toast.makeText(MainActivity.this, "Please select a file from gallery", Toast.LENGTH_LONG).show();
            }
        });

        btnOpenGallery.setOnClickListener(v -> pickImageFromSource(Sources.GALLERY));
    }

    private void changeDefaultImage() {
        Glide.with(MainActivity.this)
                .load(R.drawable.android_fix)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgSelectedFromGallery);
    }

    private void enableViews(boolean flag) {
        if (flag) {
            btnOpenGallery.setEnabled(true);
            btnSendEmail.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        } else {
            btnOpenGallery.setEnabled(false);
            btnSendEmail.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    private String getPhoneSpecs() {
        int[] lengthInPx = Utils.getScreenSizeInPX(MainActivity.this);
        int[] lengthInDp = Utils.getScreenSizeInDP(MainActivity.this);

        String phoneInfo = "Manufacturer: " + Build.MANUFACTURER +
                "\nModel: " + Build.MODEL +
                "\nBrand: " + Build.BRAND +
                "\nDensity: " + getResources().getDisplayMetrics().densityDpi +
                "\nScreenSizeInches: " + new DecimalFormat("##.#").format(Utils.getScreenSizeInches(MainActivity.this)) +
                "\nSize in px: Width " + lengthInPx[0] + " Height " + lengthInPx[1] +
                "\nSize in dp: Width " + lengthInDp[0] + " Height " + lengthInDp[1];

        if (BuildConfig.DEBUG) Log.d(TAG, "PhoneInfo: " + phoneInfo);
        return phoneInfo;
    }

    private void deleteAllFilesFromPicturesDir() {
        File picturesDir = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
        if (picturesDir.isDirectory()) {
            String[] files = picturesDir.list();
            if (null!= files && files.length > 0) {
                for (String file : files) {
                    new File(picturesDir, file).delete();
                }
            }
        }
    }


    private void pickImageFromSource(Sources sources) {
        RxImagePicker.with(this).requestImage(sources)
                .flatMap((Function<Uri, ObservableSource<?>>) uri -> {
                    deleteAllFilesFromPicturesDir();
                    selectedFile = null;
                    return RxImageConverters.uriToFile(MainActivity.this, uri, MainActivity.this.createTempFile());
                })
                .subscribe(MainActivity.this::onImagePicked,
                        throwable -> { Toast.makeText(MainActivity.this, String.format("Error: %s", throwable), Toast.LENGTH_LONG).show();
                                        throwable.printStackTrace();
                                     });
    }

    private void onImagePicked(Object result) {
        if (BuildConfig.DEBUG) Log.d(TAG, String.format("Result: %s", result));
        selectedFile = (File) result;
        Glide.with(this)
                .load(result)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgSelectedFromGallery);
    }


    private File createTempFile() {
        return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "_image.jpeg");
    }

}
