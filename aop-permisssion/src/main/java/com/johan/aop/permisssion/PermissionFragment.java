package com.johan.aop.permisssion;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionFragment extends Fragment {

    private static final String KEY_PERMISSION = "KEY_PERMISSION";
    private static final int REQUEST_PERMISSION_CODE = 100;


    private String[] permissions;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            permissions = getArguments().getStringArray(KEY_PERMISSION);
        }
    }

    public static PermissionFragment newInstance(String[] permissions) {
        PermissionFragment fragment = new PermissionFragment();
        Bundle args = new Bundle();
        args.putStringArray(KEY_PERMISSION, permissions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestPermissions();
    }

    private void requestPermissions() {
        if (getActivity() == null) {
            Log.e(PermissionAspect.TAG, "Permission Fragment getActivity is null");
            callAccept(-1);
            return;
        }
        List<String> needRequestPermissions = new ArrayList<>();
        for (String permission : permissions) {
            int flag = ActivityCompat.checkSelfPermission(getActivity(), permission);
            if (flag != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissions.add(permission);
            }
        }
        if (needRequestPermissions.size() > 0) {
            requestPermissions(needRequestPermissions.toArray(new String[needRequestPermissions.size()]), REQUEST_PERMISSION_CODE);
        } else {
            callback.onAccept(permissions, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean ok = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                callAccept(0);
            } else {
                callRefuse();
            }
        }
    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void callAccept(int resultCode) {
        if (callback != null) {
            callback.onAccept(permissions, resultCode);
        }
    }

    private void callRefuse() {
        if (callback != null) {
            callback.onRefuse(permissions);
        }
    }

    public interface Callback {
        void onAccept(String[] permissions, int resultCode);
        void onRefuse(String[] permissions);
    }

}
