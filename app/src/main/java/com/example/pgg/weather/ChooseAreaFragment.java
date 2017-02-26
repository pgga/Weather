package com.example.pgg.weather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pgg.weather.db.City;
import com.example.pgg.weather.db.County;
import com.example.pgg.weather.db.Province;
import com.example.pgg.weather.util.HttpUtil;
import com.example.pgg.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by PGG on 2017/2/26.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private TextView text_title;
    private ListView list_view;
    private Button back_button;
    private List<String> dataList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    /**
     * 选中的级别为省份
     */
    private Province secletedProvience;
    /**
     * 选中级别为城市
     */
    private City secletedCity;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        text_title = (TextView) view.findViewById(R.id.text_title);
        list_view = (ListView) view.findViewById(R.id.list_view);
        back_button = (Button) view.findViewById(R.id.back_button);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        list_view.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    secletedProvience = provinceList.get(position);
                    querryCity();
                } else if (currentLevel == LEVEL_CITY) {
                    secletedCity = cityList.get(position);
                    querryCounty();
                }
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    querryCity();
                } else if (currentLevel == LEVEL_CITY) {
                    querryProvince();
                }
            }
        });
    }

    /**
     * 查询全国的省份，优先数据库，没有再去服务器
     */
    private void querryProvince() {
        text_title.setText("中国");
        back_button.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            querryFromServe(address, "province");
        }
    }


    /**
     * 查询全国的县，优先数据库，没有再去服务器
     */
    private void querryCounty() {
        text_title.setText(secletedCity.getCityName());
        back_button.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(secletedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = secletedProvience.getProvinceCode();
            int cityCode = secletedCity.getCityCode();
            String address = "http://guolin.tech/api/china" + provinceCode + cityCode;
            querryFromServe(address, "county");
        }
    }

    /**
     * 查询全国的城市，优先数据库，没有再去服务器
     */
    private void querryCity() {

        text_title.setText(secletedProvience.getProvinceName());
        back_button.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("province = ?", String.valueOf(secletedProvience.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = secletedProvience.getProvinceCode();
            String address = "http://guolin.tech/api/china" + provinceCode;
            querryFromServe(address, "city");
        }

    }

    /**
     * 从服务器获取数据
     */
    private void querryFromServe(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOKHttpReuset(address, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseTest = response.body().toString();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponce(responseTest);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponce(responseTest,secletedProvience.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponce(responseTest,secletedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                querryProvince();
                            }else if ("city".equals(type)){
                                querryCity();
                            }else if("county".equals(type)){
                                querryCounty();
                            }
                        }
                    });
                }
            }
        });

    }

    private void closeProgressDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void showProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
