package com.qboxus.gograbdriver.activitiesandfragments.myjobhistory;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qboxus.gograbdriver.adapters.MyJobHistoryAdapter;
import com.qboxus.gograbdriver.appinterfaces.AdapterClickListener;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.MyJobHistoryModel;
import com.qboxus.gograbdriver.models.TripDriverRatingModel;
import com.qboxus.gograbdriver.models.TripModel;
import com.qboxus.gograbdriver.models.TripPaymentModel;
import com.qboxus.gograbdriver.models.UserModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyJobHistoryF extends RootFragment {

    View view;
    Preferences preferences;
    SwipeRefreshLayout refreshLayout;
    ArrayList<MyJobHistoryModel> dataList = new ArrayList<>();
    MyJobHistoryAdapter adapter;
    View.OnClickListener navClickListener;
    int pageCount = 0;
    boolean ispostFinsh = false;
    ProgressBar loadMoreProgress;
    private RecyclerView recyclerView;
    private TextView txtNoData;
    private ProgressBar progressBar;


    public MyJobHistoryF() {
        // Required empty public constructor
    }

    public MyJobHistoryF(View.OnClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_job_history, container, false);
        initControl();
        actionControl();

        return view;
    }

    private void actionControl() {
        callApiRideDetails(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                pageCount = 0 ;
                callApiRideDetails(true);
            }
        });

    }

    private void initControl() {
        preferences = new Preferences(view.getContext());
        progressBar = view.findViewById(R.id.progressbar);
        txtNoData = view.findViewById(R.id.tv_no_data);
        refreshLayout = view.findViewById(R.id.refreshlayout_job);
        recyclerView = view.findViewById(R.id.recyclerView_job);
        loadMoreProgress = view.findViewById(R.id.load_more_progress);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = layoutManager.findLastVisibleItemPosition();

                if (userScrolled && (scrollOutitems == dataList.size() - 1)) {
                    userScrolled = false;

                    if (loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApiRideDetails(false);
                    }
                }


            }
        });


        adapter = new MyJobHistoryAdapter(getContext(), dataList, new AdapterClickListener() {
            @Override
            public void OnItemClick(int postion, Object Model, View view) {
                MyJobHistoryModel item = (MyJobHistoryModel) Model;
                openTripDetails(item);
            }
        });
        recyclerView.setAdapter(adapter);

    }

    public void callApiRideDetails(boolean isRefresh) {
        JSONObject params = new JSONObject();

        try {
            params.put("driver_id", preferences.getKeyUserId());
            params.put("starting_point", ""+pageCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (dataList.isEmpty() && !refreshLayout.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            loadMoreProgress.setVisibility(View.GONE);
        }

        ApiRequest.callApi(view.getContext(), ApisList.showCompletedPastTrips, params, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                if (isRefresh) {
                    refreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                } else {
                    refreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                }
                parseData(resp);

            }
        });

    }


    public void parseData(String resp) {

        try {
            JSONObject jsonObject = new JSONObject(resp);

            String code = jsonObject.optString("code");

            if (code.equals("200")) {
                JSONArray msg = jsonObject.optJSONArray("msg");
                ArrayList<MyJobHistoryModel> tempList = new ArrayList<MyJobHistoryModel>();
                for (int i = 0; i < msg.length(); i++) {


                    JSONObject data = msg.getJSONObject(i);
                    JSONObject trip = data.optJSONObject("Trip");
                    JSONObject request = data.getJSONObject("Request");
                    JSONObject user = data.optJSONObject("User");
                    JSONObject driver = data.optJSONObject("Driver");
                    JSONArray driverRating = data.optJSONArray("DriverRating");

                    MyJobHistoryModel myJobHistoryModel = new MyJobHistoryModel();

                    TripModel tripModel = new TripModel();
                    tripModel.setId(trip.optString("id", ""));
                    tripModel.setUserId(trip.optString("user_id", ""));
                    tripModel.setDriverId(trip.optString("driver_id", ""));
                    tripModel.setVehicleId(trip.optString("vehicle_id", ""));

                    tripModel.setRequestId(trip.optString("request_id", ""));
                    tripModel.setMap(trip.optString("map", ""));
                    tripModel.setPickupLocation(trip.optString("pickup_location", ""));
                    tripModel.setDestinationLocation(trip.optString("dropoff_location", ""));
                    tripModel.setPickupLat(trip.optString("pickup_lat", "0"));
                    tripModel.setPickupLong(trip.optString("pickup_long", "0"));
                    tripModel.setDestinationLat(trip.optString("dropoff_lat", "0"));
                    tripModel.setDestinationLong(trip.optString("dropoff_long", "0"));
                    tripModel.setPickupDatetime(trip.optString("pickup_datetime", ""));
                    tripModel.setDestinationDatetime(trip.optString("dropoff_datetime", ""));
                    tripModel.setCompleted(trip.optString("completed", ""));
                    tripModel.setCreated(trip.optString("created", ""));

                    TripPaymentModel paymentModel = new TripPaymentModel();
                    paymentModel.setId(request.optString("id", ""));
                    paymentModel.setTripId("");
                    paymentModel.setUserId(request.optString("user_id", ""));
                    paymentModel.setDriverId(request.optString("driver_id", ""));
                    paymentModel.setEstimatedFare(request.optString("estimated_fare", "0"));
                    paymentModel.setFinalFare(request.optString("final_fare", "0"));
                    paymentModel.setPaymentType(request.optString("payment_type", ""));
                    paymentModel.setPaymentFromWallet("");
                    paymentModel.setPaymentCollectFromWallet("");
                    paymentModel.setPaymentCollectFromCard("");
                    paymentModel.setPayCollectFromCash("");
                    paymentModel.setPaymentMethodId("");
                    paymentModel.setStripe_charge("");
                    paymentModel.setCreated(request.optString("created", ""));
                    tripModel.setTripPaymentModel(paymentModel);

                    myJobHistoryModel.setTripModel(tripModel);

                    UserModel userModel = new UserModel();
                    userModel.setId(user.optString("id", ""));
                    userModel.setEmail(user.optString("email", ""));
                    userModel.setFirstName(user.optString("first_name", ""));
                    userModel.setLastName(user.optString("last_name", ""));
                    userModel.setUsername(user.optString("username", ""));
                    userModel.setPhoneNo(user.optString("phone_no", ""));
                    userModel.setDob(user.optString("dob", ""));
                    userModel.setGender(user.optString("gender", ""));
                    userModel.setImage(user.optString("image", ""));
                    userModel.setDeviceToken(user.optString("device_token", ""));
                    userModel.setRole(user.optString("role", ""));
                    userModel.setOnlineStatus(user.optString("online", "0"));
                    userModel.setLat(user.optString("lat", "0"));
                    userModel.setLng(user.optString("long", "0"));
                    userModel.setWallet(user.optString("wallet", "0"));
                    userModel.setAuthToken(user.optString("token", ""));
                    userModel.setCreated(user.optString("created", ""));

                    myJobHistoryModel.setUserModel(userModel);

                    UserModel driverModel = new UserModel();
                    driverModel.setId(driver.optString("id", ""));
                    driverModel.setEmail(driver.optString("email", ""));
                    driverModel.setFirstName(driver.optString("first_name", ""));
                    driverModel.setLastName(driver.optString("last_name", ""));
                    driverModel.setUsername(driver.optString("username", ""));
                    driverModel.setPhoneNo(driver.optString("phone_no", ""));
                    driverModel.setDob(driver.optString("dob", ""));
                    driverModel.setGender(driver.optString("gender", ""));
                    driverModel.setImage(driver.optString("image", ""));
                    driverModel.setDeviceToken(driver.optString("device_token", ""));
                    driverModel.setRole(driver.optString("role", ""));
                    driverModel.setOnlineStatus(driver.optString("online", "0"));
                    driverModel.setLat(driver.optString("lat", "0"));
                    driverModel.setLng(driver.optString("long", "0"));
                    driverModel.setWallet(driver.optString("wallet", "0"));
                    driverModel.setAuthToken(driver.optString("token", ""));
                    driverModel.setCreated(driver.optString("created", ""));

                    myJobHistoryModel.setDriverModel(driverModel);

                    ArrayList<TripDriverRatingModel> ratingModelArrayList = new ArrayList<>();

                    for (int ri = 0; ri < driverRating.length(); ri++) {
                        JSONObject inner_rating_obj = driverRating.optJSONObject(ri);

                        TripDriverRatingModel ratingModel = new TripDriverRatingModel();
                        ratingModel.setId(inner_rating_obj.optString("id", ""));
                        ratingModel.setTripId(inner_rating_obj.optString("trip_id", ""));
                        ratingModel.setDriverId(inner_rating_obj.optString("driver_id", ""));
                        ratingModel.setUserId(inner_rating_obj.optString("user_id", ""));
                        ratingModel.setStar(inner_rating_obj.optString("star", "0"));
                        ratingModel.setComment(inner_rating_obj.optString("comment", ""));
                        ratingModel.setCreated(inner_rating_obj.optString("created", ""));

                        ratingModelArrayList.add(ratingModel);

                    }

                    myJobHistoryModel.setDriverRatingList(ratingModelArrayList);

                    tempList.add(myJobHistoryModel);

                }


                if (pageCount == 0) {
                    dataList.clear();
                }

                dataList.addAll(tempList);

                if (pageCount == 0 && dataList.size() > 0) {
                    txtNoData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    txtNoData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            } else {
                if(pageCount == 0) {
                    if (dataList.size() > 0) {
                        txtNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

        } catch (Exception e) {
            Functions.logDMsg( "Error " + e);
        } finally {
            loadMoreProgress.setVisibility(View.GONE);
        }

    }


    public void openTripDetails(MyJobHistoryModel item) {

        JobDetailF jobDetailF = new JobDetailF();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", item);
        jobDetailF.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.history_main_container, jobDetailF, "Job_detail_F").addToBackStack("Job_detail_F").commit();
    }

}
