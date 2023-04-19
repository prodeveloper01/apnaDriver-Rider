package com.qboxus.gograbdriver.activitiesandfragments.settingfragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.models.CityAndGenderModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CityAndGenderF extends RootFragment implements View.OnClickListener {

    View view;
    TextView tvTitle, tvNoCityFound;
    String title, selectedItem;
    ImageView ivBack;
    LinearLayout tabSearchBar;
    ProgressBar progressbar;
    EditText etSearch;
    Bundle bundle;
    private RecyclerView recyclerView;
    private List<CityAndGenderModel> dataList = new ArrayList<>();
    private FragmentCallback callBack;
    private CityAndGender_Adapter adapter;

    public CityAndGenderF(String title, String selectedItem, FragmentCallback callBack) {
        this.title = title;
        this.callBack = callBack;
        this.selectedItem = selectedItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_city_and_gender_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
    }

    private void initControl() {
        bundle = new Bundle();
        bundle.putBoolean("IsResponce", false);
        bundle.putString("Data", "");
        tvNoCityFound = view.findViewById(R.id.no_city_found);
        tvTitle = view.findViewById(R.id.tv_title);
        ivBack = view.findViewById(R.id.iv_back);
        tabSearchBar = view.findViewById(R.id.search_bar_tab);
        progressbar = view.findViewById(R.id.progressbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 1);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CityAndGender_Adapter(dataList);
        recyclerView.setAdapter(adapter);

        etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        setUpScreenData();
    }

    private void filterList(CharSequence s) {
        try {
            List<CityAndGenderModel> filter_list = new ArrayList<>();
            for (CityAndGenderModel model : dataList) {
                if (model.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                    filter_list.add(model);
                }
            }
            adapter.filter(filter_list);

        } catch (Exception e) {
            Functions.logDMsg("Error : " + e);
        }
    }

    private void setUpScreenData() {
        tvTitle.setText("" + title);
        progressbar.setVisibility(View.VISIBLE);
        if (view.getContext().getString(R.string.select_language).equalsIgnoreCase("" + title)) {

            dataList.clear();
            tabSearchBar.setVisibility(View.GONE);
            {
                CityAndGenderModel model = new CityAndGenderModel();
                model.setId("1");
                model.setName("English");
                model.setSelected(false);
                dataList.add(model);
                adapter.notifyDataSetChanged();
            }
            {
                CityAndGenderModel model = new CityAndGenderModel();
                model.setId("2");
                model.setName("عربى");
                model.setSelected(false);
                dataList.add(model);
                adapter.notifyDataSetChanged();
            }
            progressbar.setVisibility(View.GONE);
            if (dataList.size() > 0) {
                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).getName().equalsIgnoreCase(selectedItem)) {
                        CityAndGenderModel model = dataList.get(i);
                        model.setSelected(true);
                        dataList.set(i, model);
                        adapter.notifyDataSetChanged();
                    }
                }

                recyclerView.setVisibility(View.VISIBLE);
                tvNoCityFound.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvNoCityFound.setVisibility(View.VISIBLE);
            }
        } else if (view.getContext().getString(R.string.select_gender).equalsIgnoreCase("" + title)) {

            dataList.clear();
            tabSearchBar.setVisibility(View.GONE);
            {
                CityAndGenderModel model = new CityAndGenderModel();
                model.setId("1");
                model.setName("Male");
                dataList.add(model);
                adapter.notifyDataSetChanged();
            }
            {
                CityAndGenderModel model = new CityAndGenderModel();
                model.setId("2");
                model.setName("Female");
                dataList.add(model);
                adapter.notifyDataSetChanged();
            }
            {
                CityAndGenderModel model = new CityAndGenderModel();
                model.setId("3");
                model.setName("Unspecified");
                dataList.add(model);
                adapter.notifyDataSetChanged();
            }
            progressbar.setVisibility(View.GONE);
            if (dataList.size() > 0) {

                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).getName().equalsIgnoreCase(selectedItem)) {
                        CityAndGenderModel model = dataList.get(i);
                        model.setSelected(true);
                        dataList.set(i, model);
                        adapter.notifyDataSetChanged();
                    }
                }

                recyclerView.setVisibility(View.VISIBLE);
                tvNoCityFound.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvNoCityFound.setVisibility(View.VISIBLE);
            }
        } else if (view.getContext().getString(R.string.select_country).equalsIgnoreCase("" + title)) {

            tabSearchBar.setVisibility(View.VISIBLE);
            callApiShowCountries();

        } else if (view.getContext().getString(R.string.select_vehicle_type).equalsIgnoreCase("" + title)) {

            tabSearchBar.setVisibility(View.GONE);
            callApiShowVehicleTypes();

        }


    }

    private void callApiShowVehicleTypes() {

        dataList.clear();

        JSONObject sendobj = new JSONObject();

        progressbar.setVisibility(View.VISIBLE);
        ApiRequest.callApi(getContext(), ApisList.showRideTypes, sendobj, resp -> {

            progressbar.setVisibility(View.GONE);
            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("code").equals("200")) {

                    JSONArray arrayOfObj = respobj.getJSONArray("msg");

                    for (int i = 0; i < arrayOfObj.length(); i++) {

                        JSONArray ridetype = arrayOfObj.getJSONObject(i).getJSONArray("RideType");

                        for (int z = 0; z < ridetype.length(); z++) {
                            JSONObject rideObj = ridetype.optJSONObject(z);
                            CityAndGenderModel model = new CityAndGenderModel();
                            model.setId(rideObj.getString("id"));
                            model.setName(rideObj.getString("name"));
                            dataList.add(model);
                        }


                        adapter.notifyDataSetChanged();
                    }

                    if (dataList.size() > 0) {

                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).getId().equalsIgnoreCase(selectedItem)) {
                                CityAndGenderModel model = dataList.get(i);
                                model.setSelected(true);
                                dataList.set(i, model);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoCityFound.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        tvNoCityFound.setVisibility(View.VISIBLE);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        });

    }


    private void callApiShowCountries() {
        progressbar.setVisibility(View.VISIBLE);
        ApiRequest.callApi(getContext(), ApisList.showCountries, new JSONObject(), new CallbackResponce() {
            @Override
            public void responce(String resp) {
                progressbar.setVisibility(View.GONE);
                if (resp != null) {

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            JSONArray msgarray = respobj.getJSONArray("msg");

                            METHOD_gettingCountriesList(msgarray);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void METHOD_gettingCountriesList(JSONArray msgarray) {
        try {
            dataList.clear();
            for (int i = 0; i < msgarray.length(); i++) {
                JSONObject countriesobj = msgarray.getJSONObject(i).getJSONObject("Country");

                CityAndGenderModel model = new CityAndGenderModel();
                model.setId(countriesobj.optString("id"));
                model.setName(countriesobj.optString("name"));
                model.setPhonecode(countriesobj.optString("phonecode"));
                model.setIso(countriesobj.optString("iso"));
                model.setSelected(false);
                dataList.add(model);
                adapter.notifyDataSetChanged();
            }
            if (dataList.size() > 0) {

                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).getId().equalsIgnoreCase(selectedItem)) {
                        CityAndGenderModel model = dataList.get(i);
                        model.setSelected(true);
                        dataList.set(i, model);
                        adapter.notifyDataSetChanged();
                    }
                }


                recyclerView.setVisibility(View.VISIBLE);
                tvNoCityFound.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvNoCityFound.setVisibility(View.VISIBLE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
        }
    }

    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }

    public class CityAndGender_Adapter extends RecyclerView.Adapter<CityAndGender_Adapter.ViewHolder> {

        private List<CityAndGenderModel> list = new ArrayList<>();

        public CityAndGender_Adapter(List<CityAndGenderModel> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_city_and_gender_item_view, null);
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvTitle.setText(Functions.SetFirstLetterCapital(list.get(position).getName()));
            if (list.get(position).isSelected()) {
                holder.ivTick.setVisibility(View.VISIBLE);
            } else {
                holder.ivTick.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < list.size(); i++) {
                        CityAndGenderModel model = list.get(i);
                        model.setSelected(false);
                        list.set(i, model);
                        adapter.notifyDataSetChanged();
                    }
                    holder.ivTick.setVisibility(View.VISIBLE);
                    CityAndGenderModel model = list.get(position);
                    model.setSelected(true);
                    list.set(position, model);
                    adapter.notifyDataSetChanged();


                    if (view.getContext().getString(R.string.select_language).equalsIgnoreCase("" + title)) {
                        String str_data = "";
                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).isSelected()) {
                                str_data = dataList.get(i).getName();
                            }
                        }
                        if (TextUtils.isEmpty(str_data)) {
                            Functions.showAlert(view.getContext(), view.getContext().getString(R.string.Language_status), view.getContext().getString(R.string.select_anyone_for_proceed));
                            return;
                        }
                        bundle.putBoolean("IsResponce", true);
                        bundle.putString("Data", str_data);
                        callBack.Responce(bundle);
                        getActivity().onBackPressed();
                    }

                    else if (view.getContext().getString(R.string.select_gender).equalsIgnoreCase("" + title)) {
                        String str_data = "";
                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).isSelected()) {
                                str_data = dataList.get(i).getName();
                            }
                        }
                        if (TextUtils.isEmpty(str_data)) {
                            Functions.showAlert(view.getContext(), view.getContext().getString(R.string.gender_selection_status), view.getContext().getString(R.string.select_anyone_for_proceed));
                            return;
                        }
                        bundle.putBoolean("IsResponce", true);
                        bundle.putString("Data", str_data);
                        callBack.Responce(bundle);
                        getActivity().onBackPressed();
                    }

                    else if (view.getContext().getString(R.string.select_country).equalsIgnoreCase("" + title)) {
                        CityAndGenderModel cityAndGender_model = new CityAndGenderModel();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).isSelected()) {
                                cityAndGender_model = list.get(i);
                            }
                        }
                        if (cityAndGender_model.getId() == null) {
                            Functions.showAlert(view.getContext(), view.getContext().getString(R.string.country_selection_status), view.getContext().getString(R.string.select_anyone_for_proceed));
                            return;
                        }
                        bundle.putBoolean("IsResponce", true);
                        bundle.putSerializable("Data", cityAndGender_model);
                        Functions.logDMsg("selected short name"+cityAndGender_model.getIso());
                        callBack.Responce(bundle);
                        getActivity().onBackPressed();
                    }

                    else if (view.getContext().getString(R.string.select_vehicle_type).equalsIgnoreCase("" + title)) {
                        CityAndGenderModel cityAndGender_model = new CityAndGenderModel();
                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).isSelected()) {
                                cityAndGender_model = dataList.get(i);
                            }
                        }
                        if (cityAndGender_model.getId() == null) {
                            Functions.showAlert(view.getContext(), view.getContext().getString(R.string.vehicle_selection_status), view.getContext().getString(R.string.select_anyone_for_proceed));
                            return;
                        }
                        bundle.putBoolean("IsResponce", true);
                        bundle.putSerializable("Data", cityAndGender_model);
                        callBack.Responce(bundle);
                        getActivity().onBackPressed();
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        public void filter(List<CityAndGenderModel> filter_list) {
            list = filter_list;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle;
            ImageView ivTick;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivTick = itemView.findViewById(R.id.iv_tick);
                tvTitle = itemView.findViewById(R.id.tv_title);

            }


        }
    }

}