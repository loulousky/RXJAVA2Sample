package com.example.rxjava2;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

public class AllRxjavaMethod {


    private static volatile AllRxjavaMethod method;

    private Context contxt;

    private AllRxjavaMethod(Context context) {
        this.contxt = context;
    }

    public static AllRxjavaMethod getInstance(Context context) {
        if (method == null) {
            synchronized (AllRxjavaMethod.class) {
                if (method == null) {
                    return new AllRxjavaMethod(context);
                }
            }
        }

        return method;


    }

    /**
     * 最基本用法
     */
    public void baseicUsed() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("基础的使用方式发送一段字符串");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Toast.makeText(contxt, s, 1).show();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    /**
     * donext操作符，一般对发送出来的数据进行一些处理 打印存储等
     * 或者在观察者之前做一些初始化工作
     */

    public void DoNextMethod() {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) {

                emitter.onNext("donext的用法");
                emitter.onComplete();

            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("打印", "在这里我可以对这个数据打印，同样也可以对数据进行存储");
            }
        });
    }

    /**
     * AsyncSubject 无论发送多少数据 是否 oncomplete 只能收到最后一个，只能用create创建别的创建无效
     */
    public void AsyncSubjectMethod() {
        AsyncSubject<Integer> asyncSubject = AsyncSubject.create();
        asyncSubject.onNext(1);
        asyncSubject.onNext(2);
        asyncSubject.onNext(3);
        asyncSubject.onNext(4);
        asyncSubject.onComplete();
        asyncSubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                //收到4
                Toast.makeText(contxt, integer.toString(), 1).show();
            }
        });
    }


    /**
     *订阅开始点的上一个nexr值会缓存，订阅后发送给观察者
     */
    public void BehaviorSubjectMethod()
    {
        BehaviorSubject<Integer> integerBehaviorSubject=BehaviorSubject.create();

        integerBehaviorSubject.onNext(1);
        //这里subscribe会收到1 2 3 4
        integerBehaviorSubject.onNext(2);
        //这里subscribe会收到2 3 4 和下面发送的next值
        integerBehaviorSubject.onNext(3);

        integerBehaviorSubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                //会收到3 4

            }
        });
        integerBehaviorSubject.onNext(4);

        integerBehaviorSubject.onComplete();
        //这里订阅一个都收不到
    }


    /**
     * publishSubject  正常的流程 onnext发送给每个观察者
     *
     */

    public void PublishSubjectMethod(){
        PublishSubject<Integer> integer=PublishSubject.create();

        integer.onNext(1);
        integer.onNext(2);


        integer.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
             //收到每一个值
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        //在complete后订阅无效
        integer.onComplete();
    }


    /**
     *   ReplaySubject  无论何时订阅 都能收到全部的onnext
     */
    public void ReplaySubjectMethods(){
        ReplaySubject<Integer> replaySubject=ReplaySubject.create();
        replaySubject.onNext(1);
        replaySubject.onNext(2);

        replaySubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                //1 2 3 4 都能收到
            }
        });

        replaySubject.onNext(3);
        replaySubject.onNext(4);
        replaySubject.onComplete();

    }




}
