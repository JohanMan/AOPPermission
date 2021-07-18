package com.johan.aop.permisssion;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class PermissionAspect {

    public static final String TAG = "PermissionAspect";

    private static final String PERMISSION_FRAGMENT_TAG = "AOPPermissionFragment";

    @Pointcut("execution(@com.johan.aop.permisssion.Permission * *(..))")
    public void onPermission() {

    }

    @Around("onPermission()")
    public void handlePermission(ProceedingJoinPoint point) {
        try {
            Object[] args = point.getArgs();
            Object target = point.getTarget();
            if (target == null) {
                Log.e(TAG, "AOP permission target is null");
                point.proceed(args);
                return;
            }
            FragmentManager manager = null;
            if (target instanceof FragmentActivity) {
                manager = ((FragmentActivity) target).getSupportFragmentManager();
            }
            if (target instanceof Fragment) {
                manager = ((Fragment) target).getChildFragmentManager();
            }
            if (manager == null) {
                Log.e(TAG, "AOP permission only support target with FragmentActivity and Fragment");
                point.proceed(args);
                return;
            }
            Signature signature = point.getSignature();
            if (!(signature instanceof MethodSignature)) {
                Log.e(TAG, "AOP permission only use on the method");
                point.proceed(args);
                return;
            }
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            Method method = methodSignature.getMethod();
            Permission annotation = method.getAnnotation(Permission.class);
            String[] permissions = annotation.value();
            Class<? extends PermissionRefuse> refuse = annotation.refuse();
            addPermissionFragment(point, refuse, manager, permissions);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void addPermissionFragment(final ProceedingJoinPoint joinPoint, final Class<? extends PermissionRefuse> refuseClass, final FragmentManager fragmentManager, String[] permissions) {
        Fragment fragment = fragmentManager.findFragmentByTag(PERMISSION_FRAGMENT_TAG);
        if (fragment != null && fragment.isAdded()) {
            Log.e(TAG, "Permission is requesting ...");
            return;
        }
        PermissionFragment permissionFragment = PermissionFragment.newInstance(permissions);
        permissionFragment.setCallback(new PermissionFragment.Callback() {
            @Override
            public void onAccept(String[] permissions, int resultCode) {
                // 接受
                removePermissionFragment(fragmentManager);
                try {
                    // 执行原来方法
                    joinPoint.proceed(joinPoint.getArgs());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            @Override
            public void onRefuse(String[] permissions) {
                // 拒绝
                removePermissionFragment(fragmentManager);
                try {
                    // 执行拒绝策略
                    PermissionRefuse refuseInstance = refuseClass.newInstance();
                    refuseInstance.onRefuse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(permissionFragment, PERMISSION_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    private void removePermissionFragment(FragmentManager manager) {
        Fragment fragment = manager.findFragmentByTag(PERMISSION_FRAGMENT_TAG);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

}
