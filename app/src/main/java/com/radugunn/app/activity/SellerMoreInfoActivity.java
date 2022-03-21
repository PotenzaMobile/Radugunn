package com.radugunn.app.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;

import com.radugunn.app.R;
import com.radugunn.app.customview.textview.TextViewLight;
import com.radugunn.app.utils.BaseActivity;
import com.radugunn.app.utils.RequestParamUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellerMoreInfoActivity extends BaseActivity {

    @BindView(R.id.tvContentDesc)
    TextViewLight tvContentDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_more_info);
        ButterKnife.bind(this);
        String dealername = getIntent().getExtras().getString(RequestParamUtils.Dealer);
        if (dealername != null) {
            settvTitle(dealername);
        } else {
            settvTitle(RequestParamUtils.Dealer);
        }
        setToolbarTheme();
        showBackButton();
        String data = getIntent().getExtras().getString(RequestParamUtils.data);
        if (data == null || data.equals("")) {
            tvContentDesc.setText(R.string.no_data_found);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvContentDesc.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvContentDesc.setText(Html.fromHtml(data));
            }
        }

    }
}
