package com.qboxus.gograbdriver.activitiesandfragments.myjobs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qboxus.gograbdriver.adapters.OrdersAdapter;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.MyOrdersModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeF extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    View view;
    ArrayList<MyOrdersModel> dataList;
    RecyclerView recyclerView;
    OrdersAdapter adapter;
    MyOrdersModel myOrdersModel;
    String id;
    TextView noDataTxt;
    ProgressBar progressbar;
    NewBroadCast mReceiver;
    Handler handler;
    int pageCount = 0;
    ProgressBar loadMoreProgress;
    LinearLayoutManager linearLayoutManager;
    boolean ispostFinsh, fromBroadcast = false;
    Preferences preferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    String orderParam;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, null, false);
        preferences = new Preferences(view.getContext());
        noDataTxt = view.findViewById(R.id.no_data_txt);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        progressbar = view.findViewById(R.id.progressbar);
        loadMoreProgress = view.findViewById(R.id.load_more_progress);
        recyclerView = view.findViewById(R.id.rc_my_orders);
        swipeRefreshLayout.setOnRefreshListener(this);
        dataList = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new OrdersAdapter(getContext(), dataList, (postion, Model, view1) -> {

            myOrdersModel = (MyOrdersModel) Model;
            id = myOrdersModel.id;

            switch (view1.getId()) {

                case R.id.btn_yes:
                    if (preferences.getKeyUserActive().equals("1")) {
                        if(myOrdersModel.orderType.equals("food")){
                            orderParam = "food_order_id";
                        }else{
                            orderParam = "parcel_order_id";
                        }
                        callApiRiderOrderResponse(id, "1", postion , orderParam);
                    } else {
                        Functions.showAlert(getActivity(), getActivity().getResources().getString(R.string.alert), getString(R.string.online_first));
                    }
                    break;

                case R.id.btn_no:
                    if (preferences.getKeyUserActive().equals("1")) {

                        if(myOrdersModel.orderType.equals("food")){
                            orderParam = "food_order_id";
                        }else{
                            orderParam = "parcel_order_id";
                        }

                        callApiRiderOrderResponse(id, "2", postion, orderParam);
                    } else {
                        Functions.showAlert(getActivity(), getActivity().getResources().getString(R.string.alert), getString(R.string.online_first));
                    }

                    break;


                default:

                    Fragment fragment;
                    if(myOrdersModel.orderType.equals("food")){
                        fragment = new OrderDetailF();
                    }
                    else {
                        fragment = new ParcelOrderDetailF();
                    }

                    FragmentManager fragmentManagerOrder = getActivity().getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("dataModel", myOrdersModel);
                    bundle.putString("selected_pos","0");
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction_history = fragmentManagerOrder.beginTransaction();
                    fragmentTransaction_history.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                    fragmentTransaction_history.replace(R.id.job_container_detail, fragment, "order_detail_f").addToBackStack("order_detail_f").commit();

                    break;

            }
        });
        recyclerView.setAdapter(adapter);

        mReceiver = new NewBroadCast();
        getActivity().registerReceiver(mReceiver, new IntentFilter("Active"));
        callApiShowRiderOrders();

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

                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

                if (userScrolled && (scrollOutitems == dataList.size() - 1)) {
                    userScrolled = false;

                    if (loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApiShowRiderOrders();
                    }
                }


            }
        });


        return view;

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (view != null && menuVisible) {
            handler = new Handler();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageCount = 0;
                    callApiShowRiderOrders();
                }
            }, 500);
        }
    }

    private void callApiShowRiderOrders() {
        if (pageCount == 0 && fromBroadcast == true) {
            fromBroadcast = false;
        }
        JSONObject sendobj = new JSONObject();

        if (dataList.isEmpty() && !swipeRefreshLayout.isRefreshing()) {
            progressbar.setVisibility(View.VISIBLE);
        } else {
            progressbar.setVisibility(View.GONE);
            loadMoreProgress.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("starting_point", "" + pageCount);
            sendobj.put("type", "" + "pending");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        noDataTxt.setVisibility(View.GONE);

        ApiRequest.callApi(getContext(), ApisList.showRiderOrders, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                progressbar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);
                        if (respobj.getString("code").equals("200")) {
                            JSONArray jsonArrayPendingOrders = respobj.getJSONArray("msg");
                            Functions.orderParseData(jsonArrayPendingOrders, "Pending", arrayList -> {

                                if (pageCount == 0) {
                                    dataList.clear();
                                }

                                dataList.addAll(arrayList);
                                if (dataList.isEmpty()) {
                                    noDataTxt.setVisibility(View.VISIBLE);
                                } else {
                                    noDataTxt.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        loadMoreProgress.setVisibility(View.GONE);
                    }

                }
            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            default:
                break;
        }
    }

    /*
      This method will call the api "riderOrderResponse" on accept or cancel the order.
      if order is cancel then it will be removed from the list.
      if order is accepted then order detail screen will open
    */
    private void callApiRiderOrderResponse(String orderId, String riderResponse, int position, String orderParam) {

        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put(orderParam, orderId + "");
            sendobj.put("rider_response", riderResponse + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(), false, false);
        ApiRequest.callApi(getContext(), ApisList.riderOrderResponse, sendobj, resp -> {
            Functions.cancelLoader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);
                    if (respobj.getString("code").equals("200")) {

                        dataList.remove(position);
                        adapter.notifyDataSetChanged();

                        if (riderResponse.equals("1")) {
                            pageCount = 0;
                            callApiShowRiderOrders();
                            Fragment fragment;
                            if(myOrdersModel.orderType.equals("food")){
                                fragment = new OrderDetailF();
                            }
                            else {
                                fragment = new ParcelOrderDetailF();
                            }

                            FragmentManager fragmentManagerOrder = getActivity().getSupportFragmentManager();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("dataModel", myOrdersModel);
                            bundle.putString("selected_pos","1");
                            fragment.setArguments(bundle);
                            FragmentTransaction fragmentTransaction_history = fragmentManagerOrder.beginTransaction();
                            fragmentTransaction_history.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                            fragmentTransaction_history.replace(R.id.job_container_detail, fragment, "order_detail_f").addToBackStack("order_detail_f").commit();

                        } else if (riderResponse.equals("2")) {
                            pageCount = 0;
                            callApiShowRiderOrders();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onRefresh() {
        pageCount = 0;
        callApiShowRiderOrders();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private class NewBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            pageCount = 0;
            fromBroadcast = true;
            callApiShowRiderOrders();
        }
    }

}
