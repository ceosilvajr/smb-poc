package com.j6w.app.sanmiguelpoc.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Base64;

import com.j6w.app.sanmiguelpoc.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by ceosilvajr on 7/31/2014.
 */
public class CameraHelper {

    private static CameraHelper instance;

    private Context mContext;

    public CameraHelper(Context context) {
        this.mContext = context;
    }

    public static CameraHelper getInstance(Context mContext) {
        if (instance == null) {
            instance = new CameraHelper(mContext);
        }
        return instance;
    }

    public boolean checkCameraHardware() {
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            cameraError();
            return false;
        } else {
            return true;
        }
    }

    private void cameraError() {

        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(mContext.getString(R.string.app_name));
        alertDialog.setMessage("Error. Camera unavailable.");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    /**
     * Create a File for saving an image
     */
    public String BitMapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
