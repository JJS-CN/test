package com.example.admin.test.auto;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.lang.reflect.Modifier;

/**
 * 说明：
 * Created by jjs on 2018/7/10.
 */

@Aspect
public class MyAop {
    @Before("call(* android.app.Activity.on**(..))")
    public void onActivityMethodBefore(JoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        Log.e("ssssssssssssssssss", "onActivityMethodBefore: " + key);
    }

  /*  @After("call(* com.example.admin.test.MainActivity.testAOP())")
    public void aspectJavaDemoAdvice(JoinPoint joinPoint) throws Throwable {
        Log.e("helloAOP", "aspect:::" + joinPoint.getSignature());
    }*/

    @Around("execution(* com.example.admin.test.AAA.ssss())")
    public void Aoun(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.e("sssssssssssssss", "before");
        joinPoint.proceed();
        Log.e("sssssssssssssss", "after");
        Log.e("AOP", "目标方法名为:" + joinPoint.getSignature().getName());
        Log.e("AOP", "目标方法所属类的简单类名:" + joinPoint.getSignature().getDeclaringType().getSimpleName());
        Log.e("AOP", "目标方法所属类的类名:" + joinPoint.getSignature().getDeclaringTypeName());
        Log.e("AOP", "目标方法声明类型:" + Modifier.toString(joinPoint.getSignature().getModifiers()));
        Log.e("AOP", "kkk:" + joinPoint.getStaticPart());
        Log.e("AOP", "kkk:" + joinPoint.getKind());
        Log.e("AOP", "kkk:" + joinPoint.getSignature());
        Log.e("AOP", "kkk:" + joinPoint.getSourceLocation());
        Log.e("AOP", "被代理的对象:" + joinPoint.getTarget());
        Log.e("AOP", "代理对象自己:" + joinPoint.getThis());
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            Log.e("AOP", "第" + (i + 1) + "个参数为:" + args[i]);
        }

    }

}
