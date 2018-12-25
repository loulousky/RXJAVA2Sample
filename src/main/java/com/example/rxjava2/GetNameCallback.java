package com.example.rxjava2;

public class GetNameCallback implements DeferCallBack<String> {
    @Override
    public String call() {
        return "我的名字叫哈哈哈哈";
    }
}
