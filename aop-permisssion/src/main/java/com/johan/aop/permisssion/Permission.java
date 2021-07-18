package com.johan.aop.permisssion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {
    // 权限
    String[] value();
    // 拒绝权限策略
    Class<? extends PermissionRefuse> refuse() default DefaultRefuse.class;
}
