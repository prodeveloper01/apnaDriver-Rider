package com.qboxus.gograbdriver.activitiesandfragments.chatmodule;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.helpingclasses.Variables;
import com.qboxus.gograbdriver.R;

import java.io.File;


public class SeeFullImageF extends RootFragment implements View.OnClickListener {


    // this is the third party library that will download the image
    Preferences preferences;
    private View view;
    private Context context;
    private SimpleDraweeView singleImage;
    private String imageUrl, chatId;
    private ProgressDialog progressDialog;
    private File direct;
    private File fullpath;
    private DownloadRequest prDownloader;

    public SeeFullImageF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_see_full_image, container, false);
        context = getContext();
        preferences = new Preferences(view.getContext());

        imageUrl = getArguments().getString("image_url");

        Log.d("isfromchatscreen", "" + imageUrl);

        ImageView closeGallery = view.findViewById(R.id.close_gallery);
        closeGallery.setOnClickListener(this);

        progressDialog = new ProgressDialog(context, R.style.MyDialogStyle);
        progressDialog.setMessage("Please Wait");

        PRDownloader.initialize(getActivity().getApplicationContext());


        //get the full path of image in database
        fullpath = new File(Variables.folderGoGrab + chatId + ".jpg");

        //if the image file is exits then we will hide the save btn
        ImageView savebtn = view.findViewById(R.id.savebtn);
        if (fullpath.exists()) {
            savebtn.setVisibility(View.GONE);
        }

        //get the directory inwhich we want to save the image
        direct = new File(Variables.folderGoGrab);

        //this code will download the image
        prDownloader = PRDownloader.download(imageUrl, direct.getPath(), chatId + ".jpg")
                .build();

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savepicture();
            }
        });

        singleImage = view.findViewById(R.id.single_image);

        // if the image is already save then we will show the image from directory otherwise
        // we will show the image by using picasso
        if (fullpath.exists()) {
            Uri uri = Uri.parse(fullpath.getAbsolutePath());
            singleImage.setController(Functions.frescoImageLoad(uri,false));
        } else {
            singleImage.setController(Functions.frescoImageLoad(imageUrl,R.drawable.image_placeholder,singleImage,false));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.close_gallery:
                getActivity().onBackPressed();
                break;
        }
    }

    // this funtion will save the picture but we have to give tht permision to right the storage
    private void savepicture() {
        final File direct = new File(Variables.folderDcimCareem);
        progressDialog.show();
        prDownloader.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.parse(direct.getPath() + chatId + ".jpg"));
                context.sendBroadcast(intent);
                progressDialog.dismiss();

                Functions.showAlert(getActivity(), view.getContext().getString(R.string.image_saved)
                        , "" + fullpath.getAbsolutePath(), view.getContext().getString(R.string.no), view.getContext().getString(R.string.yes), new CallbackResponce() {
                            @Override
                            public void responce(String resp) {

                            }
                        });
            }

            @Override
            public void onError(Error error) {
                progressDialog.dismiss();
                Functions.showToast(view.getContext(), view.getContext().getString(R.string.something_went_wrong));
            }

        });
    }
}


