package com.example.rxjava2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    AllRxjavaMethod method;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        method=AllRxjavaMethod.getInstance(this);

        method.baseicUsed();

        method.AsyncSubjectMethod();

        method.BehaviorSubjectMethod();

        method.PublishSubjectMethod();

        method.ReplaySubjectMethods();

        method.DoNextMethod();

        method.BufferMethods();





    }
}
