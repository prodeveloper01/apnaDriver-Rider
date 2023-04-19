package com.qboxus.gograbdriver.activitiesandfragments.rideandrequest;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.RideModel;
import com.qboxus.gograbdriver.R;
import com.willy.ratingbar.RotationRatingBar;
import org.json.JSONException;
import org.json.JSONObject;


public class RideCompleteFeedbackF extends Fragment implements View.OnClickListener{

    FragmentCallback callback;
    View view;
    ImageView ivClose;
    LinearLayout btnDone;
    EditText etFeedback;
    TextView tvHowWasYourRide;
    RotationRatingBar simpleRatingBar;
    SimpleDraweeView ivProfile;
    Preferences preferences;
    RideModel rideModel;

    public RideCompleteFeedbackF() {
    }

    public RideCompleteFeedbackF(FragmentCallback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_ride_complete_feedback_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivClose.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    private void initControl() {
        preferences=new Preferences(view.getContext());
        rideModel = (RideModel) getArguments().getSerializable("user_data");

        tvHowWasYourRide =view.findViewById(R.id.tv_how_was_your_ride);
        ivClose =view.findViewById(R.id.iv_close);
        btnDone =view.findViewById(R.id.btn_done);
        etFeedback =view.findViewById(R.id.et_feedback);
        simpleRatingBar=view.findViewById(R.id.simpleRatingBar);
        ivProfile =view.findViewById(R.id.iv_profile);


        setupScreenData();
    }

    private void setupScreenData() {
        tvHowWasYourRide.setText(view.getContext().getString(R.string.how_was_your_last_ride_with)+" "+ rideModel.getUserModel().getUsername());

        ivProfile.setController(Functions.frescoImageLoad(
                 rideModel.getUserModel().getImage(),
                R.drawable.ic_profile_gray,
                ivProfile,
                false
        ));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.iv_close:
            {

                giveRating("0",""+ etFeedback.getText().toString());

            }
            break;

            case R.id.btn_done:
            {

                giveRating(""+simpleRatingBar.getRating(),""+ etFeedback.getText().toString());
            }
            break;
        }
    }



    public void giveRating(String value, String comment){
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("request_id", rideModel.getRideRequestModel().getId());
            sendobj.put("user_id", rideModel.getUserModel().getId());
            sendobj.put("driver_id", preferences.getKeyUserId());
            sendobj.put("star", value);
            sendobj.put("comment",comment);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        ApiRequest.callApi(view.getContext(), ApisList.giveratingtouser, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                if (resp!=null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.optString("code").equals("200")){

                            Functions.showToast(view.getContext(), view.getContext().getString(R.string.successfully_give_rating));
                            Functions.clearFragment(getActivity().getSupportFragmentManager());
                            Intent intent=new Intent(view.getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }else {
                            Functions.showToast(view.getContext(), ""+respobj.optString("msg"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }
}