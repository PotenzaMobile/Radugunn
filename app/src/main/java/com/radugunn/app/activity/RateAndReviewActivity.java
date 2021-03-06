package com.radugunn.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.drawable.DrawableCompat;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.radugunn.app.R;
import com.radugunn.app.customview.edittext.EditTextRegular;
import com.radugunn.app.customview.textview.TextViewBold;
import com.radugunn.app.customview.textview.TextViewRegular;
import com.radugunn.app.model.Customer;
import com.radugunn.app.utils.BaseActivity;
import com.radugunn.app.utils.Constant;
import com.radugunn.app.utils.RequestParamUtils;
import com.radugunn.app.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RateAndReviewActivity extends BaseActivity implements OnResponseListner {


    String imge, pid;
    @BindView(R.id.ivProductImage)
    ImageView ivProductImage;

    @BindView(R.id.tvProductName)
    TextViewRegular tvProductName;

    @BindView(R.id.etUserName)
    EditTextRegular etUsername;

    @BindView(R.id.etEmail)
    EditTextRegular etEmail;

    @BindView(R.id.rating)
    SimpleRatingBar rating;

    @BindView(R.id.etComment)
    EditTextRegular etComment;

    @BindView(R.id.tvSubmit)
    TextViewBold tvSubmit;

    @BindView(R.id.llMain)
    LinearLayout llMain;

    @BindView(R.id.ivWhatsappDrag)
    ImageView ivWhatsappDrag;

    @BindView(R.id.crMain)
    CoordinatorLayout crMain;

    private String customerId;
    private Customer customer = new Customer();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_and_review);
        ButterKnife.bind(this);
        setToolbarTheme();
        settvTitle(getResources().getString(R.string.review));
        showBackButton();
        setColorTheme();
        setScreenLayoutDirection();
        hideSearchNotification();
        Intent i = getIntent();
        tvProductName.setText(i.getStringExtra(RequestParamUtils.PRODUCT_NAME));
        imge = i.getStringExtra(RequestParamUtils.IMAGEURL);
        Picasso.get().load(imge).into(ivProductImage);
//        try {
//            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imge).getContent());
//            ivProductImage.setImageBitmap(bitmap);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
        pid = i.getStringExtra(RequestParamUtils.PRODUCT_ID);
        checkLoggedin();
    }

    @OnClick(R.id.tvSubmit)
    public void submitClick() {
        if (customerId.equals("")) {
            if (etUsername.getText().toString().length() == 0) {
                Toast.makeText(this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (etEmail.getText().toString().length() == 0) {
                    Toast.makeText(this, R.string.enter_email_address, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (etComment.getText().length() == 0) {
                        Toast.makeText(this, R.string.enter_message, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (rating.getRating() == 0) {
                            Toast.makeText(this, getResources().getString(R.string.please_apply_rating), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            submitRate();
                        }
                    }
                }
            }
        } else {
            if (etComment.getText().length() == 0) {
                Toast.makeText(this, R.string.enter_message, Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (rating.getRating() == 0) {
                    Toast.makeText(this, getResources().getString(R.string.please_apply_rating), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    submitRate();
                }
            }
        }

    }

    private void setColorTheme() {
        // tvContactSeller.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        Drawable unwrappedDrawable = tvSubmit.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

    }

    public void submitRate() {
        if (Utils.isInternetConnected(this)) {

            showProgress("");
            PostApi postApi = new PostApi(this, RequestParamUtils.submitRate, this, getlanuage());
            JSONObject object = new JSONObject();
            try {

                object.put(RequestParamUtils.emailcustomer, etEmail.getText().toString());
                object.put(RequestParamUtils.namecustomer, etUsername.getText().toString());
                object.put(RequestParamUtils.product, pid);
                object.put(RequestParamUtils.comment, etComment.getText().toString());
                object.put(RequestParamUtils.ratestar, rating.getRating());

                if (!(customerId.equals(""))) {
                    object.put(RequestParamUtils.USER_ID, customerId);
                }

                postApi.callPostApi(new URLS().RATING, object.toString());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("error", e.getMessage());

            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    public void checkLoggedin() {
        customerId = getPreferences().getString(RequestParamUtils.ID, "");
        String cust = getPreferences().getString(RequestParamUtils.CUSTOMER, "");
        if (cust.equals("")) {
            if (!customerId.isEmpty()) {
                etEmail.setEnabled(true);
                etUsername.setEnabled(true);
            }
        } else {
            customer = new Gson().fromJson(
                    cust, new TypeToken<Customer>() {
                    }.getType());

            setCustomerData();
        }

    }

    public void setCustomerData() {


        etUsername.setText(customer.firstName + " " + customer.lastName);
        etEmail.setText(customer.email + "");
        if (etUsername.getText().toString().contains("null")) {
            etUsername.setEnabled(true);
        } else {
            etUsername.setEnabled(false);
        }
        etEmail.setEnabled(false);

    }

    @Override
    public void onResponse(String response, String methodName) {
        Log.e("Rating appi", response);
        if (methodName.equals(RequestParamUtils.submitRate)) {

            dismissProgress();
            if (response != null && response.length() > 0) {
                try {
                    JSONObject jsonObj = new JSONObject(response);

                    String status = jsonObj.getString("status");
                    if (status.equals("success")) {
//
                        Toast.makeText(this, getString(R.string.your_review_is_waiting_for_approval), Toast.LENGTH_SHORT).show();
                        finish();
                        //  onBackPressed();

                    } else {
                        Toast.makeText(this, jsonObj.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        }

    }
}
