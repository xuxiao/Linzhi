package com.linzhi;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.linzhi.adapter.SiteIDSpinnerAdapter;
import com.linzhi.base.BaseActivity;
import com.linzhi.common.MyException;
import com.linzhi.dialog.Loading;
import com.linzhi.helper.UserHelper;
import com.linzhi.model.SiteIDModel;
import com.linzhi.utils.PageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.linzhi.R.id.tv_SiteID;

public class LoginActivity extends BaseActivity {

    @BindView(tv_SiteID)
    Spinner spinner_SiteID;

    @BindView(R.id.tv_userName)
    EditText tv_userName;

    @BindView(R.id.tv_psd)
    EditText tv_psd;

    @BindView(R.id.btn_login)
    Button btn_login;

    //变量
    private String siteID = "";
    private String userName = "";
    private String passWord = "";
    List<SiteIDModel> listSiteID;
    private SiteIDSpinnerAdapter spinnerAdapter;
    //常量
    private static final int POST_SUCCESS = 11;
    private static final int POST_FAILED = 12;
    private static final int GET_SITE_SUCCESS = 13;
    private static final String TAG = "SJY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        initView();
        getSiteID();
        initListener();

    }

    private void initView() {
        ButterKnife.bind(this);
    }

    private void initListener() {
        //选择spinner
        spinner_SiteID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listSiteID != null) {
                    siteID = spinnerAdapter.getItem(position).getSiteID();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                siteID = "";
            }
        });
    }

    private void getSiteID() {
        Loading.noDialogRun(this, new Runnable() {
            @Override
            public void run() {
                try {
                    List<SiteIDModel> listSiteID = new ArrayList<SiteIDModel>();

                    listSiteID = UserHelper.getSiteID(LoginActivity.this);
                    sendMessage(GET_SITE_SUCCESS, listSiteID);
                } catch (MyException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private boolean isEmpty() {
        if (TextUtils.isEmpty(siteID)) {
            PageUtil.DisplayToast("网点号不能为空！");
            return false;
        }
        if (TextUtils.isEmpty(userName)) {
            PageUtil.DisplayToast("用户名不能为空！");
            return false;
        }
        if (TextUtils.isEmpty(passWord)) {
            PageUtil.DisplayToast("密码不能为空！");
            return false;
        }
        return true;
    }

    @OnClick(R.id.btn_login)
    public void Onclick() {
        //        siteID = tv_SiteID.getText().toString().trim();
        userName = tv_userName.getText().toString().trim();
        passWord = tv_psd.getText().toString().trim();

        //非空判断
//        if (!isEmpty()) {
//            return;
//        }

        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "run: siteID="+siteID+"--userName="+userName+"--passWord="+passWord);
                    UserHelper.loginByPs(getApplicationContext(), siteID, "aaa", "123");//userName passWord
                    sendMessage(POST_SUCCESS);
                } catch (MyException e) {
                    Log.d("SJY", "登录异常：" + e.getMessage());
                    sendMessage(POST_FAILED, e.getMessage());
                }
            }
        });

    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case POST_SUCCESS:
                startActivity(MainActivity.class);
                break;
            case POST_FAILED:
                PageUtil.DisplayToast((String) msg.obj);
                break;

            case GET_SITE_SUCCESS:
                listSiteID = (List<SiteIDModel>) msg.obj;
                bindSiteSpinner(listSiteID);// 动态绑定到spinner
                break;
        }
    }

    //spinner绑定
    private void bindSiteSpinner(List<SiteIDModel> listSiteID) {
        spinnerAdapter = new SiteIDSpinnerAdapter(this, (ArrayList<SiteIDModel>) listSiteID);
        spinner_SiteID.setAdapter(spinnerAdapter);
        try {
            //设置默认网点
            spinner_SiteID.setSelection(getSiteIDIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSiteIDIndex() {
        //可自定义登录保存的网点号
        return 0;//默认spinner显示第一个值
    }
}

