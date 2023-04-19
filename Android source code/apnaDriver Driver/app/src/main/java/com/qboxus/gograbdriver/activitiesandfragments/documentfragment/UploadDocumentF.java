package com.qboxus.gograbdriver.activitiesandfragments.documentfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentTransaction;

import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.R;


public class UploadDocumentF extends RootFragment implements View.OnClickListener {


    View view;
    RelativeLayout tabDrivingLicense, tabVehicleInsurance, tabVehicleRegistration, tabCnic;
    ImageView ivBack;
    FragmentCallback fragmentCallback;

    public UploadDocumentF() {
    }

    public UploadDocumentF(FragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_upload_document_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        tabDrivingLicense.setOnClickListener(this);
        tabVehicleInsurance.setOnClickListener(this);
        tabVehicleRegistration.setOnClickListener(this);
        tabCnic.setOnClickListener(this);
    }

    private void initControl() {
        ivBack = view.findViewById(R.id.iv_back);
        tabDrivingLicense = view.findViewById(R.id.ll_driving_license);
        tabVehicleInsurance = view.findViewById(R.id.ll_vehicle_insurance);
        tabVehicleRegistration = view.findViewById(R.id.ll_vehicle_registration);
        tabCnic = view.findViewById(R.id.ll_cnic);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
            case R.id.ll_driving_license: {
                PublishDocumentF f = new PublishDocumentF(view.getContext().getString(R.string.driving_license), new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            bundle.putBoolean("Data", bundle.getBoolean("Data", false));
                            fragmentCallback.Responce(bundle);
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.upload_document_container, f, "PublishDocument_F").addToBackStack("PublishDocument_F").commit();
            }
            break;
            case R.id.ll_vehicle_insurance: {
                PublishDocumentF f = new PublishDocumentF(view.getContext().getString(R.string.vehicle_insurance), new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            bundle.putBoolean("Data", bundle.getBoolean("Data", false));
                            fragmentCallback.Responce(bundle);
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.upload_document_container, f, "PublishDocument_F").addToBackStack("PublishDocument_F").commit();
            }
            break;
            case R.id.ll_vehicle_registration: {
                PublishDocumentF f = new PublishDocumentF(view.getContext().getString(R.string.vehicle_registration), new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            bundle.putBoolean("Data", bundle.getBoolean("Data", false));
                            fragmentCallback.Responce(bundle);
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.upload_document_container, f, "PublishDocument_F").addToBackStack("PublishDocument_F").commit();
            }
            break;
            case R.id.ll_cnic: {
                PublishDocumentF f = new PublishDocumentF(view.getContext().getString(R.string.national_id_passport), new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            bundle.putBoolean("Data", bundle.getBoolean("Data", false));
                            fragmentCallback.Responce(bundle);
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.upload_document_container, f, "PublishDocument_F").addToBackStack("PublishDocument_F").commit();
            }
            break;
        }

    }

}