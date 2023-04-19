package com.qboxus.gograbdriver.activitiesandfragments.documentfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qboxus.gograbdriver.Constants;
import com.qboxus.gograbdriver.activitiesandfragments.chatmodule.SeeFullImageF;
import com.qboxus.gograbdriver.adapters.DocumentManageAdapter;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.DocumentManageModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DocumentManageF extends RootFragment implements View.OnClickListener {

    View view;
    ImageView btnDocuments;
    ImageView ivNavMenu;
    View.OnClickListener navClickListener;
    Preferences preferences;
    RecyclerView recyclerView;
    DocumentManageAdapter documentHomeAdapter;
    SwipeRefreshLayout refreshlayout;
    ArrayList<DocumentManageModel> documentList = new ArrayList<>();
    TextView tvNoData;
    ProgressBar progressBar;

    public DocumentManageF() {
    }

    public DocumentManageF(View.OnClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_document_manage_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        callApiDocuments(false);

        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApiDocuments(true);
            }
        });

        btnDocuments.setOnClickListener(this);
        ivNavMenu.setOnClickListener(navClickListener);
    }

    private void initControl() {
        preferences = new Preferences(view.getContext());
        btnDocuments = view.findViewById(R.id.btn_documents);
        ivNavMenu = view.findViewById(R.id.iv_nav_menu);
        tvNoData = view.findViewById(R.id.tv_no_data);
        progressBar = view.findViewById(R.id.progress_bar);
        refreshlayout = view.findViewById(R.id.refreshlayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        documentHomeAdapter = new DocumentManageAdapter(getActivity(), documentList, (postion, Model, view) -> {
            switch (view.getId()) {
                case R.id.tab_view: {
                    SeeFullImageF seeFullImageF = new SeeFullImageF();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    Bundle args = new Bundle();
                    args.putString("image_url", Constants.BASE_URL + documentList.get(postion).getLink());
                    args.putBoolean("isfromchat", false);
                    seeFullImageF.setArguments(args);
                    transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top);
                    transaction.replace(R.id.document_container, seeFullImageF, "SeeFullImage_F").addToBackStack("SeeFullImage_F").commit();
                }
                break;
                default: {
                }
            }
        });
        documentHomeAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(documentHomeAdapter);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_documents: {
                UploadDocumentF uploadDocumentF = new UploadDocumentF(new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            if (bundle.getBoolean("Data", false))
                                callApiDocuments(false);
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.document_container, uploadDocumentF, "UploadDocument_F").addToBackStack("UploadDocument_F").commit();
            }
            break;
        }

    }


    ////fetch all the documents url from database
    private void callApiDocuments(boolean isRefresh) {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (documentList.size() > 5) {
            refreshlayout.setRefreshing(true);
        } else {
            if (isRefresh) {
                refreshlayout.setRefreshing(true);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        ApiRequest.callApi(getActivity(), ApisList.showUserDocuments, sendobj, resp -> {

            if (documentList.size() > 5) {
                refreshlayout.setRefreshing(false);
            } else {
                if (isRefresh) {
                    refreshlayout.setRefreshing(false);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }


            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("code").equals("200")) {

                    JSONArray respobjJSONArray = respobj.getJSONArray("msg");
                    documentList.clear();
                    for (int i = 0; i < respobjJSONArray.length(); i++) {


                        JSONObject UserDocument = respobjJSONArray.getJSONObject(i).getJSONObject("UserDocument");

                        DocumentManageModel documentHomeModel = new DocumentManageModel();
                        documentHomeModel.setLink(UserDocument.optString("attachment"));
                        documentHomeModel.setType(UserDocument.optString("type"));
                        documentHomeModel.setStatus(UserDocument.optString("status"));
                        documentList.add(documentHomeModel);
                        documentHomeAdapter.notifyDataSetChanged();
                    }

                    if (documentList.size() > 0) {
                        tvNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Functions.logDMsg( "Error" + e);
            }


        });
    }


    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }
}