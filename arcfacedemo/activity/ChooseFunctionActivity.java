package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.common.Constants;
import com.arcsoft.arcfacedemo.fragment.ChooseDetectDegreeDialog;
import com.arcsoft.arcfacedemo.service.MyMqttService;
import com.arcsoft.arcfacedemo.session.Session;
import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.VersionInfo;
import com.arcsoft.face.enums.RuntimeABI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ChooseFunctionActivity extends BaseActivity {
    private static final String TAG = "ChooseFunctionActivity";
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    // 在线激活所需的权限
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };
    boolean libraryExists = true;
    // Demo 所需的动态库文件
    private static final String[] LIBRARIES = new String[]{
            // 人脸相关
            "libarcsoft_face_engine.so",
            "libarcsoft_face.so",
            // 图像库相关
            "libarcsoft_image_util.so",
    };
    // 修改配置项的对话框
    ChooseDetectDegreeDialog chooseDetectDegreeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_function);

        libraryExists = checkSoFile(LIBRARIES);
        ApplicationInfo applicationInfo = getApplicationInfo();
        Log.i(TAG, "onCreate: " + applicationInfo.nativeLibraryDir);
        if (!libraryExists) {
            showToast(getString(R.string.library_not_found));
        }else {
            VersionInfo versionInfo = new VersionInfo();
            int code = FaceEngine.getVersion(versionInfo);
            Log.i(TAG, "onCreate: getVersion, code is: " + code + ", versionInfo is: " + versionInfo);
        }
        Button button=findViewById(R.id.cleanInfo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanInfo();
                Toast.makeText(ChooseFunctionActivity.this,"清除消息",Toast.LENGTH_SHORT).show();
            }
        });
        MyMqttService.startService(this);

    }

    /**
     * 检查能否找到动态链接库，如果找不到，请修改工程配置
     *
     * @param libraries 需要的动态链接库
     * @return 动态库是否存在
     */
    private boolean checkSoFile(String[] libraries) {
        File dir = new File(getApplicationInfo().nativeLibraryDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return false;
        }
        List<String> libraryNameList = new ArrayList<>();
        for (File file : files) {
            libraryNameList.add(file.getName());
        }
        boolean exists = true;
        for (String library : libraries) {
            exists &= libraryNameList.contains(library);
        }
        return exists;
    }

    /**
     * 打开相机，显示年龄性别
     *
     * @param view
     */
    public void jumpToPreviewActivity(View view) {
        checkLibraryAndJump(FaceAttrPreviewActivity.class);
    }

    /**
     * 处理单张图片，显示图片中所有人脸的信息，并且一一比对相似度
     *
     * @param view
     */
    public void jumpToAddInfoActivity(View view) {
        checkLibraryAndJump(AddInfoActivity.class);
    }

    /**
     * 选择一张主照，显示主照中人脸的详细信息，然后选择图片和主照进行比对
     *
     * @param view
     */
    public void jumpToMultiImageActivity(View view) {
        checkLibraryAndJump(MultiImageActivity.class);
    }

    /**
     * 打开相机，RGB活体检测，人脸注册，人脸识别
     *
     * @param view
     */
    public void jumpToFaceRecognizeActivity(View view) {
        checkLibraryAndJump(RegisterAndRecognizeActivity.class);
    }

    /**
     * 打开相机，IR活体检测，人脸注册，人脸识别
     *
     * @param view
     */
    public void jumpToIrFaceRecognizeActivity(View view) {
        checkLibraryAndJump(IrRegisterAndRecognizeActivity.class);
    }

    /**
     * 批量注册和删除功能
     *
     * @param view
     */
    public void jumpToBatchRegisterActivity(View view) {
        checkLibraryAndJump(FaceManageActivity.class);
    }

    /**
     * 注册页面
     */
    public void jumpToRegisterActivity(View view){
        Intent intent=new Intent(ChooseFunctionActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    /**
     * 获取位置activity
     */
    public void jumpToLocationActivity(View view){
        checkLibraryAndJump(LocationActivity.class);
    }
    /**
     * 公告通知
     */
    public void jumpToAnnouncementActivity(View view){
        checkLibraryAndJump(AnnouncementActivity.class);
    }
    /*
    * 登录
    * */
    public void jumpToLoginActivity(View view){
        Intent intent=new Intent(ChooseFunctionActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    /**
     * 注销登录消息
     */
    public void cleanInfo(){
        Session.getInstance().setState(false);
        Session.getInstance().setToken(null);
    }

    /**
     * 全部考勤记录
     * @param view
     */
    public void JumpToAllAttendance(View view) {
        checkLibraryAndJump(AttListActivity.class);
    }
    /**
     * 申请
     */
    public void jumpToAddApp(View view){
        checkLibraryAndJump(AddApplyActivity.class);
    }

    /**
     * 查看我的申请记录
     * @param view
     */
    public void jumpToMyApplyActivity(View view) {
        checkLibraryAndJump(MyApplyActivity.class);
    }

    /**
     * 审批
     * @param view
     */
    public void jumpToApprovalActivity(View view) {
        checkLibraryAndJump(ApprovalActivity.class);
    }

    /**
     * 我的信息
     * @param view
     */
    public void MyInfo(View view) {
        checkLibraryAndJump(MyInfoActivity.class);
    }
    /**
     * 通信录
     */
    public void jumpToAddressBook(View view){
        checkLibraryAndJump(AddressBookActivity.class);
    }
    /**
     * 激活引擎
     *
     * @param view
     */
    public void activeEngine(final View view) {
        if (!libraryExists) {
            showToast(getString(R.string.library_not_found));
            return;
        }
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        if (view != null) {
            view.setClickable(false);
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Log.i(TAG, "subscribe: getRuntimeABI() " + runtimeABI);

                long start = System.currentTimeMillis();
                int activeCode = FaceEngine.activeOnline(ChooseFunctionActivity.this, Constants.APP_ID, Constants.SDK_KEY);
                Log.i(TAG, "subscribe cost: " + (System.currentTimeMillis() - start));
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            showToast(getString(R.string.active_success));
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToast(getString(R.string.already_activated));
                        } else {
                            showToast(getString(R.string.active_failed, activeCode));
                        }

                        if (view != null) {
                            view.setClickable(true);
                        }
                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = FaceEngine.getActiveFileInfo(ChooseFunctionActivity.this, activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Log.i(TAG, activeFileInfo.toString());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e.getMessage());
                        if (view != null) {
                            view.setClickable(true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    void afterRequestPermission(int requestCode, boolean isAllGranted) {
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) {
                activeEngine(null);
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }

    void checkLibraryAndJump(Class activityClass) {
        if (!libraryExists) {
            showToast(getString(R.string.library_not_found));
            return;
        }
        Boolean state= Session.getInstance().getState();

        if (state!=null){
            if (state==true){
                startActivity(new Intent(this, activityClass));
            }else{
                Toast.makeText(ChooseFunctionActivity.this, "内部被拦截，跳转登录，请设置未true", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ChooseFunctionActivity.this, "外部被拦截，跳转登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ChooseFunctionActivity.this,LoginActivity.class));
        }

    }


    public void chooseDetectDegree(View view) {
        if (chooseDetectDegreeDialog == null) {
            chooseDetectDegreeDialog = new ChooseDetectDegreeDialog();
        }
        if (chooseDetectDegreeDialog.isAdded()) {
            chooseDetectDegreeDialog.dismiss();
        }
        chooseDetectDegreeDialog.show(getSupportFragmentManager(), ChooseDetectDegreeDialog.class.getSimpleName());
    }

}
