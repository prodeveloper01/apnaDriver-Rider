package com.qboxus.gograbdriver.activitiesandfragments.documentfragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.gograbdriver.adapters.PublishDocumentAdapter;
import com.qboxus.gograbdriver.appinterfaces.AdapterClickListener;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.PermissionUtils;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.helpingclasses.Variables;
import com.qboxus.gograbdriver.models.PublishDocumentModel;
import com.qboxus.gograbdriver.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class PublishDocumentF extends RootFragment implements View.OnClickListener {

    View view;
    ImageView ivBack;
    TextView tvTitle;
    LinearLayout btnAddFile, btnSubmit;
    RecyclerView rcUploadDocuments;
    PublishDocumentAdapter adapter;
    List<PublishDocumentModel> documentModels = new ArrayList<>();
    String imageFilePath = "";
    String title = "";
    String extension = ".jpg", doctype = "";
    Preferences preferences;
    FragmentCallback fragmentCallback;
    Uri selectedImage;

    PermissionUtils takePermissionUtils;
    Dialog dialog;


    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedImage = data.getData();
                        beginCrop(selectedImage);
                    }

                }
            });

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Matrix matrix = new Matrix();
                        try {
                            android.media.ExifInterface exif = new android.media.ExifInterface(imageFilePath);
                            int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                            switch (orientation) {
                                case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix.postRotate(90);
                                    break;
                                case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix.postRotate(180);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    matrix.postRotate(270);
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));
                        beginCrop(selectedImage);
                    }
                }
            });




    public PublishDocumentF() {
    }

    public PublishDocumentF(String title, FragmentCallback fragmentCallback) {
        this.title = title;
        this.fragmentCallback = fragmentCallback;
    }

    public static String random() {
        Random generator = new Random();
        String x = "" + (generator.nextInt(96) + 32);
        return x;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_publish_document_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        btnAddFile.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void initControl() {
        preferences = new Preferences(view.getContext());
        ivBack = view.findViewById(R.id.iv_back);
        tvTitle = view.findViewById(R.id.tv_title);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnAddFile = view.findViewById(R.id.btn_add_file);
        rcUploadDocuments = view.findViewById(R.id.rc_upload_documents);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 1);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rcUploadDocuments.setLayoutManager(layoutManager);
        adapter = new PublishDocumentAdapter(documentModels, new AdapterClickListener() {
            @Override
            public void OnItemClick(int postion, Object Model, View view) {
                switch (view.getId()) {

                    case R.id.iv_delete: {
                        documentModels.clear();
                        adapter.notifyDataSetChanged();
                    }
                    break;
                    default:
                    break;
                }
            }
        });
        rcUploadDocuments.setAdapter(adapter);

        setUpScreenData();
    }

    private void setUpScreenData() {
        tvTitle.setText("" + title);


        if (title.equals(view.getContext().getString(R.string.driving_license))) {
            doctype = "driving_license";
            return;
        }
        if (title.equals(view.getContext().getString(R.string.vehicle_insurance))) {
            doctype = "vehicle_insurance";
            return;
        }
        if (title.equals(view.getContext().getString(R.string.vehicle_registration))) {
            doctype = "vehicle_registration";
        }
        if (title.equals(view.getContext().getString(R.string.national_id_passport))) {
            doctype = "identification";
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_add_file: {
                selectfile();
            }
            break;
            case R.id.btn_submit: {
                if (documentModels.size() > 0) {
                    if (TextUtils.isEmpty(documentModels.get(0).getEncodedStr())) {
                        Functions.showToast(view.getContext(), view.getContext().getString(R.string.select_any_document_first));
                        return;
                    }
                    uploadDocument(documentModels.get(0).getEncodedStr());
                }
            }
            break;
        }
    }

    private void uploadDocument(String base64) {
        callApiAddUserDoc(view.getContext(), base64, doctype, extension, "2");
    }

    private void callApiAddUserDoc(Context context, String base64, String docType, String extension, String id) {

        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("extension", extension.toLowerCase() + "");
            sendobj.put("id", id + "");
            sendobj.put("type", docType);
            JSONObject fileData = new JSONObject();
            fileData.put("file_data", base64 + "");
            sendobj.put("attachment", fileData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(view.getContext(), false, false);
        ApiRequest.callApi(context, ApisList.addDocument, sendobj, resp -> {
            Functions.cancelLoader();
            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("code").equals("200")) {
                    Functions.showToast(view.getContext(), view.getContext().getString(R.string.sucessfully_uploaded));
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Data", true);
                    fragmentCallback.Responce(bundle);
                    getActivity().onBackPressed();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void beginCrop(Uri source) {
        CropImage.activity(source)
                .start(view.getContext(), this);

        Intent intent=CropImage.activity(source).setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1,1).getIntent(requireActivity());
        cropResultCallback.launch(intent);

    }

    ActivityResultLauncher<Intent> cropResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        CropImage.ActivityResult result1 = CropImage.getActivityResult(data);
                        Uri resultUri = result1.getUri();
                        handleCrop(resultUri);
                    }
                }
            });


    private void handleCrop(Uri userimageuri) {

        InputStream imageStream = null;
        try {
            imageStream = getActivity().getContentResolver().openInputStream(userimageuri);
        } catch (FileNotFoundException e) {
            Functions.logDMsg("Error : " + e);
        }
        final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

        String path = userimageuri.getPath();
        Matrix matrix = new Matrix();
        android.media.ExifInterface exif = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                exif = new android.media.ExifInterface(path);
                int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
            } catch (IOException e) {
                Functions.logDMsg("Error : " + e);
            }
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        Bitmap converetdImage = getResizedBitmap(rotatedBitmap, 500);
        Functions.logDMsg("" + encodetobase64(converetdImage));
        PublishDocumentModel model = new PublishDocumentModel();
        model.setName("" + random() + extension);
        model.setEncodedStr("" + encodetobase64(converetdImage));
        documentModels.clear();
        documentModels.add(model);
        adapter.notifyDataSetChanged();

    }

    String encodetobase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void selectfile() {

        dialog = new Dialog(view.getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.select_profile_picture_list_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txtTakePhoto = dialog.findViewById(R.id.txt_take_photo);
        TextView txtGallery = dialog.findViewById(R.id.txt_gallery);
        TextView txtCancel = dialog.findViewById(R.id.txt_cancel);
        txtTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                takePermissionUtils=new PermissionUtils(getActivity(), cameraPermissionCallback);
                if (takePermissionUtils.isCameraPermissionGranted())
                {
                    openCameraIntent();
                }
                else
                {
                    takePermissionUtils.showCameraPermissionDailog(getActivity().getString(R.string.to_upload_image_permission_string));
                }
            }
        });
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                takePermissionUtils=new PermissionUtils(getActivity(), galleryPermissionCallback);
                if (takePermissionUtils.isStoragePermissionGranted())
                {
                    openGalleryIntent();
                }
                else
                {
                    takePermissionUtils.showStoragePermissionDailog(getActivity().getString(R.string.to_upload_image_permission_string));
                }
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }



    private ActivityResultLauncher<String[]> cameraPermissionCallback = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(getActivity(),key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(getActivity(),"camera");
                    }
                    else
                    if (allPermissionClear)
                    {
                        openCameraIntent();
                    }

                }
            });

    private ActivityResultLauncher<String[]> galleryPermissionCallback = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(getActivity(),key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(getActivity(),"gallery");
                    }
                    else
                    if (allPermissionClear)
                    {
                        openGalleryIntent();
                    }

                }
            });




    private void openGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(view.getContext(), view.getContext().getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(pictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                extension,         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }
}