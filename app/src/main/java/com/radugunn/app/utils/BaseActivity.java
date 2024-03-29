package com.radugunn.app.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.PostApiWithoutOAuth;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.radugunn.app.R;
import com.radugunn.app.activity.AccountActivity;
import com.radugunn.app.activity.CartActivity;
import com.radugunn.app.activity.HomeActivity;
import com.radugunn.app.activity.InfiniteScrollActivity;
import com.radugunn.app.activity.NewsActivity;
import com.radugunn.app.activity.NotificationActivity;
import com.radugunn.app.activity.RewardsActivity;
import com.radugunn.app.activity.SearchCategoryListActivity;
import com.radugunn.app.activity.SearchFromHomeActivity;
import com.radugunn.app.activity.WishListActivity;
import com.radugunn.app.customview.textview.TextViewBold;
import com.radugunn.app.customview.textview.TextViewLight;
import com.radugunn.app.customview.textview.TextViewRegular;
import com.radugunn.app.helper.DatabaseHelper;
import com.radugunn.app.model.Cart;
import com.radugunn.app.model.CategoryList;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnResponseListner {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    private static final String TAG = BaseActivity.class.getSimpleName();
    public ImageView ivNotification, ivSearch, ivNews;
    public SharedPreferences sharedpreferences;
    public CustomProgressDialog progressDialog;
    public String lat, lon;
    public ImageView ivBack, ivLogo;
    public ImageView ivWhatsappDrag;
    AsyncProgressDialog ad;
    Location mLastLocation;
    String language;
    int lastAction;
    private TextViewBold tvTitle;
    private FrameLayout flCart;
    private TextViewRegular tvToolCart;
    private CoordinatorLayout crMain;
    private DatabaseHelper databaseHelper;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private float dX;
    private float screenHight, screenWidth;
    private float dY;

    public LinearLayout llHome, llSearchFromBottom, llCart, llAccount, llWishList, llBottomBar, llBottmLine;
    private View views;

    public double calculatedDiscountedPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            public void uncaughtException(Thread thread, Throwable ex) {
//                // get the crash info
//                //log it into the file
//                if (defaultHandler != null && ex != null && ex.getMessage() != null) {
//                    FirebaseCrash.report(new Exception(ex));
//                    Crashlytics.getInstance().crash();
//                    defaultHandler.uncaughtException(thread, ex);
//                }
//            }
//        });

        //Check abandoned cart
        getabandonedDetails();

    }


    //ToDO: check infinite scroll enable from backend or not .
    public boolean isInfiniteScrollEnable() {
        return getPreferences().getBoolean(RequestParamUtils.INFINITESCROLL, false);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setWhatsAppButton() {


        ivWhatsappDrag = (ImageView) findViewById(R.id.ivWhatsappDrag);


        if (Constant.WHATSAPPENABLE.equals("enable") && !Constant.WHATSAPP.isEmpty()) {
            ivWhatsappDrag.setVisibility(View.VISIBLE);
        } else {
            ivWhatsappDrag.setVisibility(View.GONE);
        }

        ivWhatsappDrag.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureTap());
        ivWhatsappDrag.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        gestureDetector.onTouchEvent(motionEvent);
                        DisplayMetrics displaymetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                        screenHight = displaymetrics.heightPixels - 230;
                        screenWidth = displaymetrics.widthPixels;

                        switch (motionEvent.getActionMasked()) {
                            case MotionEvent.ACTION_DOWN:
                                dX = view.getX() - motionEvent.getRawX();
                                dY = view.getY() - motionEvent.getRawY();
                                lastAction = MotionEvent.ACTION_DOWN;
                                break;
                            case MotionEvent.ACTION_MOVE:

                                float newX = motionEvent.getRawX() + dX;
                                float newY = motionEvent.getRawY() + dY;

                                // check if the view out of screen
                                if ((newX <= 0 || newX >= screenWidth - view.getWidth()) || (newY <= 0 || newY >= screenHight - view.getHeight())) {
                                    lastAction = MotionEvent.ACTION_MOVE;
                                    break;
                                }

                                view.setX(newX);
                                view.setY(newY);
                                lastAction = MotionEvent.ACTION_MOVE;
                                view.animate().x(newX).y(newY).setDuration(0).start();
                                break;

                            case MotionEvent.ACTION_UP:
                                if (lastAction == MotionEvent.ACTION_DOWN)
                                    break;

                            default:
                                return false;
                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (URLS.isUrlBlank() == null) {
            new APIS();
        }
        FirebaseUserActions.getInstance().start(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (URLS.isUrlBlank() == null) {
            new APIS();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (URLS.isUrlBlank() == null) {
            new APIS();
        }
    }

    public void setTextViewDrawableColor(EditText textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void setTextViewDrawableColors(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void settvTitle(String title) {
        tvTitle = (TextViewBold) findViewById(R.id.tvTitle);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        ivLogo.setVisibility(View.GONE);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
    }

    public void setBottomBar(final String activity, final NestedScrollView view) {
        llHome = findViewById(R.id.llHome);
        llSearchFromBottom = findViewById(R.id.llSearchFromBottom);
        llCart = findViewById(R.id.llCart);
        llAccount = findViewById(R.id.llMyAccount);
        llWishList = findViewById(R.id.llWishList);
        llBottomBar = findViewById(R.id.llBottomBar);
        llBottmLine = findViewById(R.id.llBottmLine);

        views = findViewById(R.id.view);
        final ImageView ivBottomHome = findViewById(R.id.ivBottomHome);
        ImageView ivBottomSearch = findViewById(R.id.ivBottomSearch);
        ImageView ivBottomCart = findViewById(R.id.ivBottomCart);
        ImageView ivBottomAccount = findViewById(R.id.ivBottomAccount);
        ImageView ivBottomWishList = findViewById(R.id.ivBottomWishList);
        ImageView ivCenterBg = findViewById(R.id.ivCenterBg);

        TextViewRegular tvBottomSearch = findViewById(R.id.tvBottomSearch);
        TextViewRegular tvBottomCart = findViewById(R.id.tvBottomCart);
        TextViewRegular tvBottomAccount = findViewById(R.id.tvBottomAccount);
        TextViewRegular tvBottomWishList = findViewById(R.id.tvBottomWishList);
        TextViewRegular tvBottomCartCount = findViewById(R.id.tvBottomCartCount);


        tvBottomSearch.setText(getResources().getString(R.string.searchs));
        tvBottomCart.setText(getResources().getString(R.string.cart));
        tvBottomAccount.setText(getResources().getString(R.string.account));
        tvBottomWishList.setText(getResources().getString(R.string.my_wish_list));

        if (Config.IS_CATALOG_MODE_OPTION) {
            llCart.setVisibility(View.VISIBLE);
            tvBottomCartCount.setVisibility(View.GONE);
            ivBottomCart.setImageResource(R.drawable.ic_coupon);
            tvBottomCart.setText(getResources().getString(R.string.my_reward));
        } else {
            llCart.setVisibility(View.VISIBLE);
            if (new DatabaseHelper(this).getFromCart(0).size() > 0) {
                tvBottomCartCount.setText(new DatabaseHelper(this).getFromCart(0).size() + "");
                tvBottomCartCount.setVisibility(View.VISIBLE);
            } else {
                tvBottomCartCount.setVisibility(View.GONE);
            }

            ivBottomCart.setImageResource(R.drawable.ic_cart_gray);
            tvBottomCart.setText(getResources().getString(R.string.cart));
        }
        views.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        ivBottomHome.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        Drawable unwrappedDrawable = ivCenterBg.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor((getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)))));

        unwrappedDrawable = ivBottomHome.getBackground();
        wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor((getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)))));

        unwrappedDrawable = tvBottomCartCount.getBackground();
        wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvBottomCartCount.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        llBottmLine.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        if (activity.equals("home") || equals("list")) {
            ivBottomHome.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        } else if (activity.equals("search")) {
            ivBottomSearch.setColorFilter(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            tvBottomSearch.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        } else if (activity.equals("cart")) {
            ivBottomCart.setColorFilter(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            tvBottomCart.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        } else if (activity.equals("account")) {
            ivBottomAccount.setColorFilter(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            tvBottomAccount.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        } else if (activity.equals("wishList")) {
            ivBottomWishList.setColorFilter(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            tvBottomWishList.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        }
//        tvBottomCartCount.getBackground().setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)), PorterDuff.Mode.SRC_IN);
        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activity.equals("home")) {
                    Intent intent = new Intent(BaseActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        llSearchFromBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activity.equals("search")) {
                    Intent intent = new Intent(BaseActivity.this, SearchCategoryListActivity.class);
                    startActivity(intent);
                    if (!activity.equals("home")) {
                        finish();
                    }
                }
            }
        });
        llCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Config.IS_CATALOG_MODE_OPTION) {
                    if (!activity.equals("cart")) {
                        Intent intent = new Intent(BaseActivity.this, CartActivity.class);
                        startActivity(intent);
                        if (!activity.equals("home")) {
                            finish();
                        }
                    }
                } else {
                    Intent intent = new Intent(BaseActivity.this, RewardsActivity.class);
                    startActivity(intent);
                }
            }

        });
        llAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activity.equals("account")) {
                    Intent intent = new Intent(BaseActivity.this, AccountActivity.class);
                    startActivity(intent);
                    if (!activity.equals("home")) {
                        finish();
                    }
                }
            }
        });

        llWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activity.equals("wishList")) {
                    Intent intent = new Intent(BaseActivity.this, WishListActivity.class);
                    startActivity(intent);
                    if (!activity.equals("home")) {
                        finish();
                    }
                }
            }
        });

        ViewTreeObserver vto = llBottomBar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    llBottomBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    llBottomBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                if (view != null) {
                    view.setPadding(0, 0, 0, llBottomBar.getMeasuredHeight());
                }
            }
        });
    }


    public void setHomecolorTheme(String color) {
        LinearLayout llDrawer = findViewById(R.id.llDrawer);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(color));

        if (llDrawer != null) {
            llDrawer.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
        TextView tvCategory = findViewById(R.id.tvCategory);
        if (tvCategory != null) {
            tvCategory.setBackgroundColor(Color.parseColor(color));
        }

        ImageView ivdrwer = findViewById(R.id.ivBack);
        if (ivdrwer != null) {
            ivdrwer.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        }

        ImageView ivSearch = findViewById(R.id.ivSearch);
        if (ivSearch != null) {
            ivSearch.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        }

        ImageView ivNotification = findViewById(R.id.ivNotification);
        if (ivNotification != null) {
            ivNotification.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        }

        ImageView ivNews = findViewById(R.id.ivNews);
        if (ivNews != null) {
            ivNews.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        }

    }

    public void setLocaleByLanguageChange(String lang) {

        String languageToLoad = lang; // your language
        if (lang.contains("-")) {
            String[] array = lang.split("-");
            if (array.length > 0) {
                languageToLoad = array[0];
            } else {
                languageToLoad = lang;
            }
        } else {
            languageToLoad = lang;

        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        recreate();
        Intent intent = new Intent(BaseActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void setEmptyColor() {

        TextViewRegular tvContinueShopping = findViewById(R.id.tvContinueShopping);
        TextViewBold tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        TextViewLight tvEmptyDesc = findViewById(R.id.tvEmptyDesc);
        //ImageView ivGo = findViewById(R.id.ivGo);
        Drawable unwrappedDrawable = tvContinueShopping.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        tvEmptyTitle.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvEmptyDesc.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

    }


    public void setToolbarTheme() {

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        }
    }


    public void settvImage() {
        tvTitle = findViewById(R.id.tvTitle);
        ivLogo = findViewById(R.id.ivLogo);
        ivBack = findViewById(R.id.ivBack);

        if (tvTitle != null) {
            tvTitle.setVisibility(View.GONE);

        }
        ivLogo.setVisibility(View.VISIBLE);
        if (Constant.APPLOGO_LIGHT != null && !Constant.APPLOGO_LIGHT.equals("")) {
            Picasso.get().load(Constant.APPLOGO_LIGHT).error(R.drawable.logo).into(ivLogo);
        }
    }


    public SharedPreferences getPreferences() {
        sharedpreferences = getSharedPreferences(
                Constant.MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences;
    }

    public void setScreenLayoutDirection() {
        crMain = findViewById(R.id.crMain);
        crMain.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));

        if (Config.IS_RTL) {
            if (Build.VERSION.SDK_INT >= 17) {
                crMain.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                // Do something for lollipop and above versions
            } else {
                ViewCompat.setLayoutDirection(crMain, View.LAYOUT_DIRECTION_RTL);
                // do something for phones running an SDK before lollipop
            }
        } else {
            if (Build.VERSION.SDK_INT >= 17) {
                crMain.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                // Do something for lollipop and above versions
            } else {
                ViewCompat.setLayoutDirection(crMain, View.LAYOUT_DIRECTION_LTR);
                // do something for phones running an SDK before lollipop
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), null, Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            setTaskDescription(taskDescription);
        }


      /*  whatsappContact = (DraggbleGroup) findViewById(R.id.ivWhatsappDrag);
        if (Constant.WHATAPPENABLE.equals("enable") && !Constant.WHATSAPP.isEmpty()) {
            whatsappContact.setVisibility(View.VISIBLE);
            whatsappContact.setClickListner(BaseActivity.this);
        } else {
            whatsappContact.setVisibility(View.GONE);
        }*/
        if (findViewById(R.id.ivWhatsappDrag) != null) {
            setWhatsAppButton();
        }
    }

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void showSearch() {
        ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setVisibility(View.VISIBLE);
        ivSearch.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, SearchFromHomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        } else {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(100); // Update location every second

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    lat = String.valueOf(mLastLocation.getLatitude());
                    lon = String.valueOf(mLastLocation.getLongitude());
                    Log.e("lat", lat);
                    Log.e("Long", lon);

                    SharedPreferences.Editor pre = getPreferences().edit();
                    pre.putFloat(RequestParamUtils.LATITUDE, (float) mLastLocation.getLatitude());
                    pre.putFloat(RequestParamUtils.LONGITUDE, (float) mLastLocation.getLongitude());
                    pre.commit();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        buildGoogleApiClient();
    }

    @Override
    public void onLocationChanged(Location location) {

        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

//        lat = 21.21981 + "";
//        lon = "" + 72.7808673;
        Location locationA = new Location("Location1");
        locationA.setLatitude(getPreferences().getFloat(RequestParamUtils.LATITUDE, 0));
        locationA.setLongitude(getPreferences().getFloat(RequestParamUtils.LONGITUDE, 0));

        Location locationB = new Location("Location2");
        locationB.setLatitude(Float.parseFloat(lat));
        locationB.setLongitude(Float.parseFloat(lon));


        if (locationA.distanceTo(locationB) >= Constant.DISTANCERANGE && !getPreferences().getString(RequestParamUtils.DEVICE_TOKEN, "").equals("")) {
            Log.e("Old LatLong is ", locationA.getLatitude() + ", " + locationA.getLongitude());
            Log.e("New LatLong is ", lat + "," + lon);
            geoFencingCall(getPreferences().getFloat(RequestParamUtils.LATITUDE, Float.parseFloat(lat)) + "", getPreferences().getFloat(RequestParamUtils.LONGITUDE, Float.parseFloat(lon)) + "");
            SharedPreferences sharedpreferences = getSharedPreferences(
                    Constant.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor pre = sharedpreferences.edit();
            pre.putFloat(RequestParamUtils.LATITUDE, Float.parseFloat(lat));
            pre.putFloat(RequestParamUtils.LONGITUDE, Float.parseFloat(lon));
            pre.commit();
        }
    }

    public void geoFencingCall(String lat, String lng) {
        if (Utils.isInternetConnected(this)) {
            PostApi postApi = new PostApi(BaseActivity.this, RequestParamUtils.geoFencingCall, this, getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.DEVICE_TOKEN, Constant.DEVICE_TOKEN);
                jsonObject.put(RequestParamUtils.LATITUDE, lat);
                jsonObject.put(RequestParamUtils.LONGITUDE, lng);
                jsonObject.put(RequestParamUtils.DEVICE_TYPE, "2");
                postApi.callPostApi(new URLS().GEOFENCING, jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }

        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(String response, String methodName) {
        Log.e("Response 1==> ", response);

        if (methodName.equals(RequestParamUtils.geoFencingCall)) {
            if (response != null && response.length() > 0) {
                try {
                    Log.e("Response is ", response);
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    if (mLastLocation != null) {
                        lat = String.valueOf(mLastLocation.getLatitude());
                        lon = String.valueOf(mLastLocation.getLongitude());

                        SharedPreferences.Editor pre = getPreferences().edit();
                        pre.putFloat(RequestParamUtils.LATITUDE, (float) mLastLocation.getLatitude());
                        pre.putFloat(RequestParamUtils.LONGITUDE, (float) mLastLocation.getLongitude());
                        pre.commit();

                    }
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showCart() {
        flCart = findViewById(R.id.flCart);
        tvToolCart = findViewById(R.id.tvToolCart);

        databaseHelper = new DatabaseHelper(this);

        if (tvToolCart != null && flCart != null) {

            Drawable unwrappedDrawable = tvToolCart.getBackground();
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

            ((ImageView) findViewById(R.id.ivCart)).setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));


            if (databaseHelper.getFromCart(0).size() > 0) {
                tvToolCart.setText(databaseHelper.getFromCart(0).size() + "");
                tvToolCart.setVisibility(View.VISIBLE);
                flCart.setVisibility(View.VISIBLE);
            } else {
                tvToolCart.setVisibility(View.GONE);
                flCart.setVisibility(View.GONE);
            }
            flCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BaseActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            });
        }


        TextViewRegular tvBottomCartCount = findViewById(R.id.tvBottomCartCount);
        if (tvBottomCartCount != null) {
            if (new DatabaseHelper(this).getFromCart(0).size() > 0) {
                tvBottomCartCount.setText(new DatabaseHelper(this).getFromCart(0).size() + "");
                tvBottomCartCount.setVisibility(View.VISIBLE);
            } else {
                tvBottomCartCount.setVisibility(View.GONE);
            }
        }

        if (tvToolCart != null && flCart != null) {
            if (Config.IS_CATALOG_MODE_OPTION) {
                flCart.setVisibility(View.GONE);
                tvToolCart.setVisibility(View.GONE);
                if (tvBottomCartCount != null)
                    tvBottomCartCount.setVisibility(View.GONE);
            } else {
                if (databaseHelper.getFromCart(0).size() > 0) {
                    tvToolCart.setVisibility(View.VISIBLE);
                    if (tvBottomCartCount != null)
                        tvBottomCartCount.setVisibility(View.VISIBLE);
                } else {
                    tvToolCart.setVisibility(View.GONE);
                    if (tvBottomCartCount != null)
                        tvBottomCartCount.setVisibility(View.GONE);
                }
                flCart.setVisibility(View.VISIBLE);
            }
        }

//        tvToolCart.getBackground().setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)), PorterDuff.Mode.SRC_IN);

    }

    public void hideSearchNotification() {
        ivSearch = findViewById(R.id.ivSearch);
        ivNotification = findViewById(R.id.ivNotification);
        ivSearch.setVisibility(View.GONE);
        ivNotification.setVisibility(View.GONE);
    }

    public void showNotification() {
        ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setVisibility(View.VISIBLE);
        ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showNews() {
        ivNews = findViewById(R.id.ivNews);
        ivNews.setVisibility(View.VISIBLE);
        ivNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });
    }


    public void showBackButton() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void hideBackButton() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setVisibility(View.GONE);

    }

    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    //TODO : Show Progress
    public void showProgress(String val) {
        if (progressDialog != null) {
            progressDialog.dissmissDialog();
        }
        progressDialog = new CustomProgressDialog(BaseActivity.this);
        if (!isDestroyed()) {
            progressDialog.showCustomDialog(BaseActivity.this);
        }
    }

    //TODO : Dismiss progress
    public void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dissmissDialog();
        }

    }

    @Override
    public void attachBaseContext(Context base) {
        Log.e("Attache", "called");

        super.attachBaseContext(updateBaseContextLocale(base));

    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    public Context updateBaseContextLocale(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constant.MyPREFERENCES, Context.MODE_PRIVATE);
        // String language = sharedPref.getString(RequestParamUtils.LANGUAGE, "en");
        if (sharedPref.getString(RequestParamUtils.LANGUAGE, "").isEmpty()) {
            if (!sharedPref.getString(RequestParamUtils.DEFAULTLANGUAGE, "").isEmpty()) {
                language = sharedPref.getString(RequestParamUtils.DEFAULTLANGUAGE, "");
            } else {
                language = "en";
            }
        } else {
            language = sharedPref.getString(RequestParamUtils.LANGUAGE, "");
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale);
        }
        return updateResourcesLocaleLegacy(context, locale);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    public String getlanuage() {
        String lng = getPreferences().getString(RequestParamUtils.LANGUAGE, "");
        if (!lng.equals("") && lng != null) {
            return lng;
        } else {
            if (Constant.IS_WPML_ACTIVE) {
                String defaultLng = getPreferences().getString(RequestParamUtils.DEFAULTLANGUAGE, "");
                if (!defaultLng.equals("") && defaultLng != null) {
                    return defaultLng;
                } else {
                    return "";
                }
            } else {
                return "";
            }
        }
    }

    public void setPrice(TextViewRegular tvPrice, TextViewRegular tvPrice1, String price) {
        Log.e(TAG, "setPrice: " + price);
        if (price != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvPrice.setText(Html.fromHtml(price + "", Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvPrice.setText(Html.fromHtml(price));
            }
        if (tvPrice.getText().toString().contains("–")) {
            Log.e(TAG, "setPrice: " + "if");
            tvPrice.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
            tvPrice1.setText("");
            tvPrice.setPaintFlags(0);
        } else if (price != null) {
            if (tvPrice.getText().toString().contains(" ") && price.contains("<del>")) {
                Log.e(TAG, "setPrice: " + "elseif");
                String[] seprated = price.split("</del>");

                String firstPrice = seprated[0];
                String secondPrice = seprated[1];

                Log.e(TAG, "setPrice: firstprice: " + firstPrice);
                Log.e(TAG, "setPrice: secondprice" + secondPrice);

                String htmlText = "" + " " + firstPrice + "</font>";
                String htmlText1 = "" + " " + secondPrice + "</font>";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tvPrice.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));
                    tvPrice1.setText(Html.fromHtml(htmlText1, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tvPrice.setText(Html.fromHtml(htmlText));
                    tvPrice1.setText(Html.fromHtml(htmlText1));
                }
                String price11 = tvPrice.getText().toString();
                String price22 = tvPrice1.getText().toString();
                if (Constant.CURRENCYSYMBOL != null) {
                    price11 = tvPrice.getText().toString().replace(Constant.CURRENCYSYMBOL, "");
                    price22 = tvPrice1.getText().toString().replace(Constant.CURRENCYSYMBOL, "");
                }

                String price1 = price11.replace(",", "");
                String price2 = price22.replace(",", "");
                try {
                    if (Double.parseDouble(price1.replaceAll("\\s+", "")) > Double.parseDouble(price2.replaceAll("\\s+", ""))) {
                        tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        tvPrice1.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                        tvPrice.setTextColor(getResources().getColor(R.color.gray_light));
                        tvPrice.setTextSize(14);
                        tvPrice1.setTextSize(15);
                    } else {
                        tvPrice1.setPaintFlags(tvPrice1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        tvPrice.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                        tvPrice1.setTextColor(getResources().getColor(R.color.gray_light));
                        tvPrice.setTextSize(15);
                        tvPrice1.setTextSize(14);
                    }
                } catch (Exception e) {
                    Log.e("Exception is ", e.getMessage());
                    tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            } else {
                Log.e(TAG, "setPrice: " + "else" + price);
                tvPrice.setText(tvPrice.getText().toString());
                tvPrice.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                tvPrice1.setText("");
                tvPrice.setTextSize(15);
            }
        }
    }

    /*public void setPrice(TextViewRegular tvPrice, TextViewRegular tvPrice1, String price) {
        if (price != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvPrice.setText(Html.fromHtml(price + "", Html.FROM_HTML_MODE_COMPACT));

            } else {
                tvPrice.setText(Html.fromHtml(price) + "");
            }


        if (tvPrice.getText().toString().contains("–")) {
            tvPrice.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
            tvPrice1.setText("");
            tvPrice.setPaintFlags(0);
        } else if (tvPrice.getText().toString().contains(" ") && price.contains("<del>")) {

            String firstPrice = price.substring(price.indexOf("<del>"), price.indexOf("</del>"));
            String seconfPrice = price.substring(price.indexOf("<ins>"), price.indexOf("</ins>"));
            String htmlText = "<html>" + " " + firstPrice + "</font></html>";
            String htmlText1 = "<html>" + " " + seconfPrice + "</font></html>";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvPrice.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));
                tvPrice1.setText(Html.fromHtml(htmlText1, Html.FROM_HTML_MODE_COMPACT));

            } else {
                tvPrice.setText(Html.fromHtml(htmlText));
                tvPrice1.setText(Html.fromHtml(htmlText1));
            }
            String price11 = tvPrice.getText().toString();
            String price22 = tvPrice1.getText().toString();
            if (Constant.CURRENCYSYMBOL != null) {
                price11 = tvPrice.getText().toString().replace(Constant.CURRENCYSYMBOL, "");
                price22 = tvPrice1.getText().toString().replace(Constant.CURRENCYSYMBOL, "");

            }

            String price1 = price11.replace(",", "");
            String price2 = price22.replace(",", "");
            try {

                if (Double.parseDouble(price1.replaceAll("\\s+", "")) > Double.parseDouble(price2.replaceAll("\\s+", ""))) {
                    tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvPrice1.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                    tvPrice.setTextColor(getResources().getColor(R.color.gray_light));
                    tvPrice.setTextSize(14);
                    tvPrice1.setTextSize(15);
                } else {
                    tvPrice1.setPaintFlags(tvPrice1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvPrice.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                    tvPrice1.setTextColor(getResources().getColor(R.color.gray_light));
                    tvPrice.setTextSize(15);
                    tvPrice1.setTextSize(14);
                }
            } catch (Exception e) {
                Log.e("Exception is ", e.getMessage());
                tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

        } else {
            tvPrice1.setText(tvPrice.getText().toString());
            tvPrice1.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
            tvPrice.setText("");
        }
    }*/



    public void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void openWhatsApp(String number, String message) {

        try {
            PackageManager packageManager = getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            } else {
                Toast.makeText(this, R.string.whatsapp_not_installed, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("ERROR WHATSAPP", e.toString());
            Toast.makeText(this, R.string.whatsapp_not_installed, Toast.LENGTH_LONG).show();
        }
    }

    public void logSearchedEvent(String contentType, String searchString, boolean success) {
        Bundle params = new Bundle();
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_SEARCH_STRING, searchString);
        params.putInt(AppEventsConstants.EVENT_PARAM_SUCCESS, success ? 1 : 0);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, params);
    }

    //Product Details
    public void logViewedContentEvent(String contentType, String contentId, String currency, double price) {
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, price, params);
    }

    //Add To WishList Details
    public void logAddedToWishlistEvent(String contentId, String contentType, String currency, double price) {
        AppEventsLogger logger = AppEventsLogger.newLogger(BaseActivity.this);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, price, params);
    }

    //Add To Cart
    public void logAddedToCartEvent(String contentId, String contentType, String currency, double price) {
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, price, params);
        Log.e("logAddedToCartEvent ", "Done");
    }

    //Purchase Event
    public void logPurchasedEvent(int numItems, String contentType, String contentId, String currency, double price) {
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        Bundle params = new Bundle();
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logPurchase(BigDecimal.valueOf(price), Currency.getInstance(currency), params);
    }

    //Initial Checkout
    public void logInitiatedCheckoutEvent(String contentId, String contentType, int numItems, boolean paymentInfoAvailable, String currency, double totalPrice) {
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
        params.putInt(AppEventsConstants.EVENT_PARAM_PAYMENT_INFO_AVAILABLE, paymentInfoAvailable ? 1 : 0);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, totalPrice, params);
    }

    public void logAbandoned_CartEvent(String contentId, String contentType, int numItems, String currency, double price) {
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        Bundle params = new Bundle();
        params.putString("contentId", contentId);
        params.putString("contentType", contentType);
        params.putInt("numItems", numItems);
        params.putString("currency", currency);
        params.putDouble("price", price);
        logger.logEvent("Abandoned_Cart", params);
    }

    private void getabandonedDetails() {

        if (getPreferences().getString(RequestParamUtils.ABANDONED, "") != null && !getPreferences().getString(RequestParamUtils.ABANDONED, "").isEmpty()) {

            if (getPreferences().getString(RequestParamUtils.ABANDONEDTIME, "") != null && !getPreferences().getString(RequestParamUtils.ABANDONEDTIME, "").isEmpty()) {


                // Get system current date and time
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String formattedDate = df.format(calendar.getTime());
                Log.e(TAG, "formattedDate: " + formattedDate);
                String pattern = "yyyy/MM/dd HH:mm:ss";
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                try {
                    Date one = dateFormat.parse(formattedDate);
                    Date two = dateFormat.parse(getPreferences().getString(RequestParamUtils.ABANDONEDTIME, ""));

                    //get Different between two days
                    printDifference(two, one);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } else {
            SharedPreferences.Editor pre = getPreferences().edit();
            pre.putString(RequestParamUtils.ABANDONED, "");
            pre.putString(RequestParamUtils.ABANDONEDTIME, "");
            pre.commit();
        }
    }

    public void printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        long elapsedSeconds = different / secondsInMilli;

        Boolean time = false;

        if (elapsedDays > 0 || elapsedHours > 0) {
            time = true;
        } else {
            if (elapsedMinutes >= 1) {
                time = true;
            }
        }
        if (time) {
            Log.e(TAG, "printDifference: " + "Call Now now now..@!");

            //Cart array list value get from sharepreference
            String abandoned = getPreferences().getString(RequestParamUtils.ABANDONED, "");
            Type type = new TypeToken<List<Cart>>() {
            }.getType();
            List<Cart> cartList = new Gson().fromJson(abandoned, type);
            Log.e(TAG, "shouldOverrideUrlLoading: " + cartList.size());

            for (int i = 0; i < cartList.size(); i++) {

                String product = cartList.get(i).getProduct();
                // get product detail via Gson from string
                CategoryList categoryListRider = new Gson().fromJson(product, new TypeToken<CategoryList>() {
                }.getType());

                //Call  Abandoned cart Event
                logAbandoned_CartEvent(cartList.get(i).getProductid(), categoryListRider.name, cartList.get(i).getQuantity(), Constant.CURRENCYSYMBOL, Double.parseDouble(categoryListRider.price));
            }

            SharedPreferences.Editor pre = getPreferences().edit();
            pre.putString(RequestParamUtils.ABANDONED, "");
            pre.putString(RequestParamUtils.ABANDONEDTIME, "");
            pre.commit();
        }
    }

    private void OnWhatsappClick() {

        if (!Constant.WHATSAPP.isEmpty()) {

            if (Constant.MOBILE_COUNTRY_CODE != null && Constant.MOBILE_COUNTRY_CODE.length() > 0 && Constant.WHATSAPP.contains("+")) {

                openWhatsApp(Constant.WHATSAPP, URLS.APP_URL);
            } else {
                openWhatsApp(Constant.MOBILE_COUNTRY_CODE + Constant.WHATSAPP, URLS.APP_URL);
            }

        }
    }

    public void indexNote(CategoryList categoryList) {
        Log.e("Name is :-" + categoryList.name + " and Url is ", categoryList.externalUrl);
        Indexable noteToIndex = Indexables.noteDigitalDocumentBuilder()
                .setName(categoryList.name + " Note")
                .setText(categoryList.description)
                .setUrl(categoryList.permalink + "#" + categoryList.id)
                .build();


        FirebaseAppIndex.getInstance().update(noteToIndex);
        // ...
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUserActions.getInstance().end(null);
    }

    public class GestureTap extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            OnWhatsappClick();
            return true;
        }
    }

    @Override
    public void onBackPressed() {

        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

        if (taskList.get(0).numActivities == 1 &&
                taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            if (!getPreferences().getBoolean(RequestParamUtils.INFINITESCROLL, false)) {
                if (!getLocalClassName().contains("IntroSliderActivity") && !getLocalClassName().contains("HomeActivity") && !getLocalClassName().contains("LogInActivity") && !getLocalClassName().contains("SignUpActivity")) {

                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    super.onBackPressed();
                }
            } else {
                if (!getLocalClassName().contains("InfiniteScrollActivity") && !getLocalClassName().contains("IntroSliderActivity") && !getLocalClassName().contains("LogInActivity")
                        && !getLocalClassName().contains("SignUpActivity")) {
                    Intent intent = new Intent(this, InfiniteScrollActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    super.onBackPressed();
                }
            }

            Log.e("Class Name is ", getLocalClassName());
        } else {
            super.onBackPressed();
        }


    }

    public void showDiscount(TextViewRegular tvDiscount, String salePrice, String regularPrice) {
        tvDiscount.setVisibility(View.VISIBLE);
        Drawable unwrappedDrawable = tvDiscount.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));
        if (salePrice != null && !salePrice.equals("") && !salePrice.equals("0.0")) {
            String discount = getDiscount(regularPrice, salePrice);
            if (!discount.equals("")) {
                tvDiscount.setText(discount + " " + getResources().getString(R.string.off));
            } else {
                tvDiscount.setVisibility(View.GONE);
            }

        } else {
            tvDiscount.setVisibility(View.GONE);
        }
    }

    //TODO: API call to get home data from Backend
    public void callAPI(String data) {
        if (Utils.isInternetConnected(this)) {
            try {
                PostApi postApi = new PostApi(this, RequestParamUtils.getHomeData, this, getlanuage());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.appkey, URLS.PURCHASE_KEY);
                jsonObject.put(RequestParamUtils.IS_NOTYFI, data);

//                Log.e("?lang=fr: ", getPreferences().getString(RequestParamUtils.DefaultLanguage, "") );

                postApi.callPostApi(new URLS().IS_NOTIFIED, jsonObject.toString());
            } catch (Exception e) {
                Log.e("Home", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    public void verifyPurchase() {
        if (Utils.isInternetConnected(BaseActivity.this)) {
            PostApiWithoutOAuth postApi = new PostApiWithoutOAuth(BaseActivity.this, "post", BaseActivity.this);

            if (!getPreferences().getBoolean(RequestParamUtils.VERIFIED, false)) {
                try {
                    postApi.callPostApi(new URLS().NOTIFY, "Params");
                } catch (Exception e) {
                    Log.e("Json Exception", e.getMessage());
                }
            }
        } else {
            Toast.makeText(BaseActivity.this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    //TODO:Get Discount For sale price and original price
    public String getDiscount(String originalPrice, String salePrice) {

        try {
            Float orfinalPrices = Float.parseFloat(getPrice(originalPrice));
            Float salePrices = Float.parseFloat(getPrice(salePrice));
            Float priceDiff = orfinalPrices - salePrices;
            Double discount = Double.valueOf(priceDiff / orfinalPrices * 100);
            String discountPer = Constant.setDecimalTwo(discount) + "%";
            return discountPer;
        } catch (Exception e) {
            Log.e("Exception is =", e.getMessage());
            return "";
        }
    }

    public String getPrice(String price) {
        price = price.replace("\\s+", "");
        price = price.replace(Constant.THOUSANDSSEPRETER, "");
        price = price.replace(Constant.CURRENCYSYMBOL, "");
        return price;
    }


    public void setLocale(String lang) {
        if (!getPreferences().getString(RequestParamUtils.LANGUAGE, "").equals("")) {
            lang = getPreferences().getString(RequestParamUtils.LANGUAGE, "");
        }
        String languageToLoad = lang; // your language
        if (lang.contains("-")) {
            String[] array = lang.split("-");
            if (array.length > 0) {
                languageToLoad = array[0];
            } else {
                languageToLoad = lang;
            }
        } else {
            languageToLoad = lang;
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        if (!getPreferences().getString(RequestParamUtils.DEFAULTLANGUAGE, "").equals("") && !getPreferences().getString(RequestParamUtils.DEFAULTLANGUAGE, "").equals(languageToLoad)) {
            recreate();
            getPreferences().edit().putBoolean(RequestParamUtils.iSSITELANGUAGECALLED, false).commit();
        }
        if (getPreferences().getString(RequestParamUtils.LANGUAGE, "").isEmpty()) {
            if (!getPreferences().getBoolean(RequestParamUtils.iSSITELANGUAGECALLED, false)) {
                getPreferences().edit().putBoolean(RequestParamUtils.iSSITELANGUAGECALLED, true).commit();
                getPreferences().edit().putString(RequestParamUtils.DEFAULTLANGUAGE, languageToLoad).commit();
                recreate();
            }
        }
        setScreenLayoutDirection();
    }

    public void checkReview(JSONObject jsonObject) {
        SharedPreferences.Editor editor = getPreferences().edit();
        try {
            if (jsonObject.has("woocommerce_enable_reviews") && jsonObject.getString("woocommerce_enable_reviews") != null && jsonObject.getString("woocommerce_enable_reviews").equals("yes")) {
                editor.putBoolean(RequestParamUtils.Enable_Review, true);
            } else {
                editor.putBoolean(RequestParamUtils.Enable_Review, false);
            }
        } catch (Exception e) {
            Log.e("Exception =", e.getMessage());
        }
        try {
            if (jsonObject.has("woocommerce_review_rating_verification_required") && jsonObject.getString("woocommerce_review_rating_verification_required") != null && jsonObject.getString("woocommerce_review_rating_verification_required").equals("yes")) {
                editor.putBoolean(RequestParamUtils.Review_Varification, true);
            } else {
                editor.putBoolean(RequestParamUtils.Review_Varification, false);
            }
        } catch (Exception e) {
            Log.e("Exception =", e.getMessage());
        }
        editor.commit();
    }

    public void clearCustomer() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(RequestParamUtils.CUSTOMER, "");
        editor.commit();
    }


    //Playstore Update
    public void getPlaystoreVersion() {
        if (!getPreferences().getBoolean(Constant.PlayStore_update, false)) {
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + BaseActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                Log.e(TAG, "Harsh: PlaystoreVersion: " + newVersion);
            } catch (Exception e) {
                Log.e(TAG, "Exception::" + Arrays.toString(e.getStackTrace()));
            }

            Log.e(TAG, "Harsh: getPlaystoreVersion: New Version: " + newVersion);

            String finalNewVersion = newVersion;

            Log.e(TAG, "Harsh: getPlaystoreVersion: Final New Version " + finalNewVersion);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //UI Thread work here
                    String currentVersion = null;

                    try {
                        currentVersion = BaseActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                        Log.e(TAG, "Harsh: Currentversion" + currentVersion);
                        if (finalNewVersion != null && !finalNewVersion.isEmpty()) {
                            Log.e(TAG, "Harsh: Final Compare:  " + "Current version " + currentVersion + "playstore version " + finalNewVersion);
                            if (Float.parseFloat(currentVersion) < Float.parseFloat(finalNewVersion)) {
                                displayUpdateDialogu();
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });
        });
    }

    public void displayUpdateDialogu() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_update, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        TextView title = dialogView.findViewById(R.id.tv_title);
        TextView tv_update = dialogView.findViewById(R.id.tv_update);
        TextView tv_cancel = dialogView.findViewById(R.id.tv_cancel);
        title.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tv_update.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tv_cancel.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        tv_update.setOnClickListener(view -> {
            openPlaystore();
        });
        tv_cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        getPreferences().edit().putBoolean(Constant.PlayStore_update, false).apply();
        alertDialog.show();
    }

    public void openPlaystore() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }


}
