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
import com.qboxus.gograbdriver.models.TripModel;
import com.qboxus.gograbdriver.models.TripPaymentModel;
import com.qboxus.gograbdriver.models.UserModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FoodOrderHistoryF extends RootFragment {

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


    public FoodOrderHistoryF() {
        // Required empty public constructor
    }

    public FoodOrderHistoryF(View.OnClickListener navClickListener) {
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

        ApiRequest.callApi(view.getContext(), ApisList.showCompletedFoodOrders, params, new CallbackResponce() {
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


                    JSONObject foodOrderObj = data.optJSONObject("FoodOrder");
                    JSONObject userObj = foodOrderObj.optJSONObject("User");
                    JSONObject userPlaceObj = foodOrderObj.optJSONObject("UserPlace");
                    JSONObject restaurantObj = foodOrderObj.optJSONObject("Restaurant");


                    JSONObject riderObj = data.optJSONObject("Rider");
                    JSONObject riderOrderObj = data.optJSONObject("RiderOrder");

                    MyJobHistoryModel myJobHistoryModel = new MyJobHistoryModel();

                    TripModel tripModel = new TripModel();
                    tripModel.setId(foodOrderObj.optString("id", ""));
                    tripModel.setUserId(foodOrderObj.optString("user_id", ""));

                    tripModel.setMap(foodOrderObj.optString("map", ""));

                    tripModel.setPickupLocation(restaurantObj.optString("location_string"));
                    tripModel.setDestinationLocation(userPlaceObj.optString("location_string"));

                    tripModel.setPickupLat(restaurantObj.optString("lat"));
                    tripModel.setPickupLong(restaurantObj.optString("long"));

                    tripModel.setDestinationLat(userPlaceObj.optString("lat"));
                    tripModel.setDestinationLong(userPlaceObj.optString("long"));


                    tripModel.setCreated(foodOrderObj.optString("created", ""));

                    tripModel.setPickupDatetime(riderOrderObj.optString("pickup_datetime", ""));
                    tripModel.setDestinationDatetime(riderOrderObj.optString("delivered", ""));
                    tripModel.setCompleted(riderOrderObj.optString("delivered", ""));

                    TripPaymentModel paymentModel = new TripPaymentModel();
                    paymentModel.setEstimatedFare(foodOrderObj.optString("total", "0"));
                    paymentModel.setFinalFare(foodOrderObj.optString("total", "0"));
                    paymentModel.setPaymentType(foodOrderObj.optString("payment_card_id", ""));

                    tripModel.setTripPaymentModel(paymentModel);

                    myJobHistoryModel.setTripModel(tripModel);


                    UserModel userModel = new UserModel();
                    userModel.setId(userObj.optString("id", ""));
                    userModel.setEmail(userObj.optString("email", ""));
                    userModel.setFirstName(userObj.optString("first_name", ""));
                    userModel.setLastName(userObj.optString("last_name", ""));
                    userModel.setUsername(userObj.optString("username", ""));
                    userModel.setPhoneNo(userObj.optString("phone_no", ""));
                    userModel.setDob(userObj.optString("dob", ""));
                    userModel.setGender(userObj.optString("gender", ""));
                    userModel.setImage(userObj.optString("image", ""));
                    userModel.setDeviceToken(userObj.optString("device_token", ""));
                    userModel.setRole(userObj.optString("role", ""));
                    userModel.setOnlineStatus(userObj.optString("online", "0"));
                    userModel.setLat(userObj.optString("lat", "0"));
                    userModel.setLng(userObj.optString("long", "0"));
                    userModel.setWallet(userObj.optString("wallet", "0"));
                    userModel.setAuthToken(userObj.optString("token", ""));
                    userModel.setCreated(userObj.optString("created", ""));

                    myJobHistoryModel.setUserModel(userModel);

                    UserModel driverModel = new UserModel();
                    driverModel.setId(riderObj.optString("id", ""));
                    driverModel.setEmail(riderObj.optString("email", ""));
                    driverModel.setFirstName(riderObj.optString("first_name", ""));
                    driverModel.setLastName(riderObj.optString("last_name", ""));
                    driverModel.setUsername(riderObj.optString("username", ""));
                    driverModel.setPhoneNo(riderObj.optString("phone_no", ""));
                    driverModel.setDob(riderObj.optString("dob", ""));
                    driverModel.setGender(riderObj.optString("gender", ""));
                    driverModel.setImage(riderObj.optString("image", ""));
                    driverModel.setDeviceToken(riderObj.optString("device_token", ""));
                    driverModel.setRole(riderObj.optString("role", ""));
                    driverModel.setOnlineStatus(riderObj.optString("online", "0"));
                    driverModel.setLat(riderObj.optString("lat", "0"));
                    driverModel.setLng(riderObj.optString("long", "0"));
                    driverModel.setWallet(riderObj.optString("wallet", "0"));
                    driverModel.setAuthToken(riderObj.optString("token", ""));
                    driverModel.setCreated(riderObj.optString("created", ""));

                    myJobHistoryModel.setDriverModel(driverModel);

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
