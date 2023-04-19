package com.qboxus.gograbdriver.activitiesandfragments.vehiclemanagefragment;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.gograbdriver.activitiesandfragments.settingfragment.CityAndGenderF;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.CityAndGenderModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;


public class ManageVehicleF extends RootFragment implements View.OnClickListener {

    View view, llVehicleType;
    EditText etType, etManufacture, etModel, etYear, etLisencePlate, etColor;
    LinearLayout btnSubmit;
    ImageView ivNavMenu;
    View.OnClickListener navClickListener;
    Preferences preferences;
    String rideId ="";


    public ManageVehicleF() {
        // Required empty public constructor
    }

    public ManageVehicleF(View.OnClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_manage_vehicle, container, false);
        initControl();
        actioncontrol();
        return view;
    }

    private void actioncontrol() {
        callApiShowVehicle(view.getContext());


        btnSubmit.setOnClickListener(this);
        llVehicleType.setOnClickListener(this);
        ivNavMenu.setOnClickListener(navClickListener);

        etModel.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        etLisencePlate.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        etManufacture.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence cs, int start, int end, Spanned spanned, int dStart, int dEnd) {
                // TODO Auto-generated method stub
                if (cs.equals("")) { // for backspace
                    return cs;
                }
                if (cs.toString().matches("[a-zA-Z ]+")) {
                    return cs;
                }
                return cs;
            }
        }
        });
    }


    private void callApiShowVehicle(Context context) {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.showLoader(context, false, false);
        ApiRequest.callApi(context, ApisList.showVehicle, sendobj, resp -> {

            Functions.cancelLoader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);


                    if (respobj.getString("code").equals("200")) {
                        JSONObject characteristicsString = respobj.getJSONObject("msg").getJSONObject("Vehicle");

                        JSONObject vehicleTypeObj = respobj.getJSONObject("msg").getJSONObject("RideType");

                        rideId = vehicleTypeObj.getString("id");

                        String vehicleType = vehicleTypeObj.optString("name");
                        String cap = vehicleType.substring(0, 1).toUpperCase() + vehicleType.substring(1);
                        etType.setText(cap);

                        etManufacture.setText(characteristicsString.getString("make"));
                        etModel.setText(characteristicsString.getString("model"));
                        etYear.setText(characteristicsString.getString("year"));
                        etLisencePlate.setText(characteristicsString.getString("license_plate"));
                        etColor.setText(Functions.SetFirstLetterCapital(characteristicsString.getString("color")));

                        etManufacture.setSelection(etManufacture.getText().toString().length());
                        preferences.setKeyIsVehicleSet(true);
                        preferences.setKeyVehicleId(characteristicsString.optString("ride_type_id"));



                    }
                } catch (JSONException e) {
                    e.printStackTrace();


                }

            }

        });

    }

    private void initControl() {
        preferences=new Preferences(view.getContext());
        btnSubmit =view.findViewById(R.id.btn_submit);
        llVehicleType =view.findViewById(R.id.ll_vehicle_type);
        ivNavMenu =view.findViewById(R.id.iv_nav_menu);
        etType =view.findViewById(R.id.et_vehicle_type);
        etManufacture =view.findViewById(R.id.et_manufacture);
        etModel =view.findViewById(R.id.et_model);
        etYear =view.findViewById(R.id.et_year);
        etLisencePlate =view.findViewById(R.id.et_lisence_plate);
        etColor =view.findViewById(R.id.et_color);

    }


    private void callApiUpdateVehicle(Context context) {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("ride_type_id", ""+ rideId);
            sendobj.put("vehicle_type_id", "2");
            sendobj.put("make", (etManufacture.getText().toString()) + "");
            sendobj.put("model", (etModel.getText().toString()) + "");
            sendobj.put("year", (etYear.getText().toString()) + "");
            sendobj.put("license_plate", (etLisencePlate.getText().toString()) + "");
            sendobj.put("color", (etColor.getText().toString()) + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.showLoader(context, false, false);
        ApiRequest.callApi(context, ApisList.addVehicle, sendobj, resp -> {
            Functions.cancelLoader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {
                        JSONObject jsonObject = respobj.getJSONObject("msg").getJSONObject("Vehicle");
                        preferences.setKeyIsVehicleSet(true);
                        removeOldVehicleFromServer();
                        preferences.setKeyVehicleId(jsonObject.optString("ride_type_id"));
                        Functions.showToast(view.getContext(),view.getContext().getString(R.string.change_applied));

                        getActivity().onBackPressed();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Functions.logDMsg("JSONException:"+ e.toString());
                }

            }

        });

    }


    GeoFire geoFire_Ride;
    DatabaseReference ref_Ride;
    private void removeOldVehicleFromServer() {
        ref_Ride = FirebaseDatabase.getInstance().getReference().child("Drivers");
        geoFire_Ride = new GeoFire(ref_Ride);
        geoFire_Ride.removeLocation(preferences.getKeyUserId()+"_"+preferences.getKeyVehicleId());
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_submit:
            {
                if (TextUtils.isEmpty(etType.getText().toString())) {
                    Functions.showAlert(view.getContext(), view.getContext().getString(R.string.alert), view.getContext().getString(R.string.ride_type_empty));
                    return ;
                }
                if (TextUtils.isEmpty(etManufacture.getText().toString())) {
                    Functions.showAlert(view.getContext(), view.getContext().getString(R.string.alert), view.getContext().getString(R.string.manufacture_empty));

                    return ;
                }
                if (TextUtils.isEmpty(etModel.getText().toString())) {
                    Functions.showAlert(view.getContext(), view.getContext().getString(R.string.alert), view.getContext().getString(R.string.model_empty));
                    return ;
                }
                if (TextUtils.isEmpty(etYear.getText().toString())) {
                    Functions.showAlert(view.getContext(), view.getContext().getString(R.string.alert), view.getContext().getString(R.string.year_empty));
                    return ;
                }
                if (TextUtils.isEmpty( etLisencePlate.getText().toString())) {
                    Functions.showAlert(view.getContext(), view.getContext().getString(R.string.alert), view.getContext().getString(R.string.license_plate_empty));
                    return ;
                }
                if (TextUtils.isEmpty(etColor.getText().toString())) {
                    Functions.showAlert(view.getContext(), view.getContext().getString(R.string.alert), view.getContext().getString(R.string.color_empey));
                    return ;
                }


                if(true) {
                    callApiUpdateVehicle(getActivity());
                }else{
                    Functions.showAlert(getActivity(),getResources().getString(R.string.alert),getString(R.string.year_above));
                }
            }
                break;
            case R.id.ll_vehicle_type:
            {
                CityAndGenderF f = new CityAndGenderF(view.getContext().getString(R.string.select_vehicle_type), rideId, new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle!=null)
                        {
                            if (bundle.getBoolean("IsResponce",false))
                            {
                                CityAndGenderModel cityAndGender_model= (CityAndGenderModel) bundle.getSerializable("Data");
                                rideId =cityAndGender_model.getId();
                                etType.setText(Functions.SetFirstLetterCapital(cityAndGender_model.getName()));
                            }
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.manage_vehicle_container, f,"CitySelection_F").addToBackStack("CitySelection_F").commit();
            }
            break;
        }


    }

}
