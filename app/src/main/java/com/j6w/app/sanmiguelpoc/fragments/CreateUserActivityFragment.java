package com.j6w.app.sanmiguelpoc.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.j6w.app.sanmiguelpoc.R;
import com.j6w.app.sanmiguelpoc.objects.User;
import com.j6w.app.sanmiguelpoc.utils.CameraHelper;
import com.j6w.app.sanmiguelpoc.utils.ImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateUserActivityFragment extends Fragment {

    private static final int CAPTURE_PROFILE_IMAGE = 111;
    private static final String TAG = "CreateUserFragment";

    private Activity mContext;
    private CameraHelper mCameraHelper;
    private File mPhotoFile;
    private Uri mPhotoUri;
    private boolean isGallerySelected;

    private String mName = "";
    private String mBirthDay = "";
    private String mGender = "Male";
    private String mPhotoPath = "";

    @Bind(R.id.tv_date)
    TextView mTVDate;
    @Bind(R.id.edt_name)
    TextView mEdtName;

    @Bind(R.id.iv_image)
    ImageView mImgCustomerPhoto;


    public CreateUserActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mCameraHelper = new CameraHelper(mContext);
        View view = inflater.inflate(R.layout.fragment_create_user, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveMenuButtonClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_set_date)
    void onBtnSetDateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DateListener(), now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMaxDate(now);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.btn_select_image)
    void onBtnSelectImageClicked() {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View convertView = inflater.inflate(R.layout.container_image_selector, null);

        TextView takePicture = (TextView) convertView.findViewById(R.id.take_photo);
        TextView selectPicture = (TextView) convertView.findViewById(R.id.choose_gallery);

        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setView(convertView);
        alertDialog.setCancelable(true);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
                alertDialog.dismiss();
            }
        });

        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFromGallery();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    /**
     * Select photo from gallery
     */
    private void chooseFromGallery() {
        isGallerySelected = true;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, CAPTURE_PROFILE_IMAGE);
    }

    /**
     * Launches camera
     */
    private void takePhoto() {
        isGallerySelected = false;
        if (mCameraHelper.checkCameraHardware()) {
            mPhotoUri = getOutputMediaFileUri();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
            startActivityForResult(takePictureIntent, CAPTURE_PROFILE_IMAGE);
        }
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), getString(R.string.app_name));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
        mPhotoPath = mediaFile.getAbsolutePath();
        return mediaFile;
    }

    private void saveMenuButtonClicked() {

        mName = mEdtName.getText().toString();

        if (mName.isEmpty()) {
            alertError("Please enter name");
            return;
        }
        if (mBirthDay.isEmpty()) {
            alertError("Please set the Birthday");
            return;
        }
        if (mPhotoPath.isEmpty()) {
            alertError("Please select image");
            return;
        }

        String message = "Are you sure you want to save this user?\n\nName : " + mName +
                "\nBirthday " + mBirthDay + "\nGender : " + mGender;

        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                saveUser();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void alertError(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    @OnClick({R.id.rbtn_male, R.id.rbtn_female})
    void onGenderRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        ((RadioButton) view).toggle();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rbtn_male:
                if (checked) {
                    mGender = "Male";
                }
                break;
            case R.id.rbtn_female:
                if (checked) {
                    mGender = "Female";
                }
                break;
        }
    }

    private class DateListener implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {

            final String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            mBirthDay = date;

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mTVDate.setText("" + date);
                }
            });
            Log.d(TAG, "Date" + date);
        }

        @Override
        public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
            String time = "You picked the following time: " + hourOfDay + "h" + minute;
            Log.d(TAG, "Time" + time);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_PROFILE_IMAGE) {
            if (resultCode == mContext.RESULT_OK) {
                if (!isGallerySelected) {
                    File imageFile = null;
                    ContentResolver cr = mContext.getContentResolver();
                    try {
                        cr.notifyChange(mPhotoUri, null);
                        imageFile = new File(mPhotoPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPhotoFile = imageFile;
                    if (mPhotoFile != null) {
                        mImgCustomerPhoto.setRotation(ImageUtil.neededRotation(mPhotoFile));
                        ImageLoader.getInstance().displayImage("file:///" + mPhotoFile.getAbsolutePath(),
                                mImgCustomerPhoto);
                    } else {
                        alertError("Failed saving the picture, please try again.");
                    }
                } else {
                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = mContext.getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();

                        mPhotoPath = picturePath;

                        mPhotoFile = new File(picturePath);
                        mImgCustomerPhoto.setRotation(ImageUtil.neededRotation(mPhotoFile));
                        ImageLoader.getInstance().displayImage("file:///" + mPhotoFile.getAbsolutePath(),
                                mImgCustomerPhoto);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        alertError("Saving picture failed.");
                    }
                }
            }

            if (resultCode == mContext.RESULT_CANCELED) {
                Toast.makeText(mContext, "Taking picture canceled.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveUser() {
        try {
            User user = new User(new Date().getTime(), mName, mBirthDay, mGender, mPhotoPath);
            user.save();
            mContext.finish();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Problem found. " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
