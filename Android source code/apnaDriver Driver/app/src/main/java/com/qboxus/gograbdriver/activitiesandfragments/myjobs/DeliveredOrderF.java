package com.qboxus.gograbdriver.activitiesandfragments.myjobs;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class DeliveredOrderF extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View view;
    ArrayList<MyOrdersModel> dataList;
    RecyclerView recyclerView;
    OrdersAdapter adapter;
    MyOrdersModel myOrdersModel;
    String id;
    TextView noDataTxt;
    ProgressBar progressbar;
    Handler handler;
    int pageCount = 0;
    ProgressBar loadMoreProgress;
    LinearLayoutManager linearLayoutManager;
    boolean ispostFinsh;
    Preferences preferences;
    private SwipeRefreshLayout swipeRefreshLayout;

    public DeliveredOrderF() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_delivered_order, container, false);
        preferences = new Preferences(view.getContext());
        noDataTxt = view.findViewById(R.id.no_data_txt);
        dataList = new ArrayList<>();
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        progressbar = view.findViewById(R.id.progressbar);
        recyclerView = view.findViewById(R.id.rc_my_orders);
        loadMoreProgress = view.findViewById(R.id.load_more_progress);
        swipeRefreshLayout.setOnRefreshListener(this);

        setAdapter();

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


        callApiShowRiderOrders();
        return view;
    }


    /*
      This method will call the api "showRiderOrders" to fetch the compeleted order of rider.
    */
    private void callApiShowRiderOrders() {

        JSONObject sendobj = new JSONObject();

        if (dataList.isEmpty() && !swipeRefreshLayout.isRefreshing()) {
            progressbar.setVisibility(View.VISIBLE);
        } else {
            progressbar.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();


        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("starting_point", "" + pageCount);
            sendobj.put("type", "" + "completed");
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
                            JSONArray jsonArray_PendingOrders = respobj.getJSONArray("msg");
                            Functions.orderParseData(jsonArray_PendingOrders, "Completed", arrayList -> {
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
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (view != null && menuVisible) {
            handler = new Handler();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    callApiShowRiderOrders();
                }
            }, 500);

        }

    }


    /*
     This method will set the data in recyclerview which will come from callApiShowRiderOrders.
   */
    public void setAdapter() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new OrdersAdapter(getContext(), dataList, (postion, Model, view1) -> {

            myOrdersModel = (MyOrdersModel) Model;
            id = myOrdersModel.id;

            switch (view1.getId()) {
                default:

                    Fragment fragment;
                    if(myOrdersModel.orderType.equals("food")){
                        fragment = new OrderDetailF(new CallbackResponce() {
                            @Override
                            public void responce(String resp) {
                                if (resp != null) {
                                    pageCount = 0;
                                    callApiShowRiderOrders();
                                }
                            }
                        });
                    }
                    else {
                        fragment = new ParcelOrderDetailF(new CallbackResponce() {
                            @Override
                            public void responce(String resp) {
                                if (resp != null) {
                                    pageCount = 0;
                                    callApiShowRiderOrders();
                                }
                            }
                        });
                    }
                    FragmentManager fragmentManagerOrder = getActivity().getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("dataModel", myOrdersModel);
                    bundle.putString("selected_pos", "3");
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction_history = fragmentManagerOrder.beginTransaction();
                    fragmentTransaction_history.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                    fragmentTransaction_history.replace(R.id.job_container_detail, fragment, "order_detail_f").addToBackStack("order_detail_f").commit();

                    break;

            }
        });
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onRefresh() {
        pageCount = 0;
        callApiShowRiderOrders();
    }
}