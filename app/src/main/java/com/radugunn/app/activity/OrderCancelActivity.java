package com.radugunn.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.radugunn.app.R;
import com.radugunn.app.customview.textview.TextViewBold;
import com.radugunn.app.customview.textview.TextViewRegular;
import com.radugunn.app.helper.DatabaseHelper;
import com.radugunn.app.utils.BaseActivity;
import com.radugunn.app.utils.Constant;
import com.radugunn.app.utils.RequestParamUtils;
import com.radugunn.app.utils.Utils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderCancelActivity extends BaseActivity implements OnResponseListner {

    private DatabaseHelper databaseHelper;



    @BindView(R.id.tvOrderPlaceSuccess)
    TextViewBold tvOrderPlaceSuccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cancel);
        databaseHelper = new DatabaseHelper(this);
        ButterKnife.bind(this);
        setThem();
        databaseHelper.clearCart();
        logout();
    }

    public void logout() {
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            PostApi postApi = new PostApi(this, RequestParamUtils.logout, this,getlanuage());
            postApi.callPostApi(new URLS().LOGOUT, "");

        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }


    public void setThem() {
        TextViewRegular tvContinueShopping = findViewById(R.id.tvContinueShopping);
        ImageView ivGo = findViewById(R.id.ivGo);
        tvContinueShopping.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvOrderPlaceSuccess.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        ivGo.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setStroke(5, Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
            tvContinueShopping.setBackground(gradientDrawable);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        }


    }

    @Override
    public void onResponse(final String response, String methodName) {

        if (methodName.equals(RequestParamUtils.logout)) {
            dismissProgress();
            if (response != null && response.length() > 0) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    String status = jsonObj.getString("status");
                    if (status.equals("success")) {

                    } else {

                    }
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(OrderCancelActivity.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @OnClick(R.id.tvContinueShopping)
    public void tvContinueShoppingClick() {
        clearCustomer();
        Intent i = new Intent(OrderCancelActivity.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
