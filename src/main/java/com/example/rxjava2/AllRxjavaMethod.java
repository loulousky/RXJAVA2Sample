package com.example.rxjava2;


import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

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
     * 订阅开始点的上一个nexr值会缓存，订阅后发送给观察者
     */
    public void BehaviorSubjectMethod() {
        BehaviorSubject<Integer> integerBehaviorSubject = BehaviorSubject.create();

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
     */

    public void PublishSubjectMethod() {
        PublishSubject<Integer> integer = PublishSubject.create();

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
     * ReplaySubject  无论何时订阅 都能收到全部的onnext
     */
    public void ReplaySubjectMethods() {
        ReplaySubject<Integer> replaySubject = ReplaySubject.create();
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


    /**
     * buffer操作符， 缓冲区大小和每次向后移动的距离。buffer(3,1)发射3个数据，完成后像后移动1位再发射3个数据
     * 例如：just(1,2,3,4,5) buffer(3,1)  第一次发射 123，然后向后移动一位再发射(2,3,4)再向后移动 一位发射
     * (3,4,5)然后(4,5) 最后收到5
     */
    public void BufferMethods() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onComplete();
            }
        }).buffer(3, 2).subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Exception {
                integers.toString();//第一次收到123 第二次收到345 第三次收到5 结束
            }
        });

    }

//单一数据的情况下三个基本操作符的使用

    /**
     * 单一发送操作符之  Completable
     * 用来发送完成的消息，只关心完成的通知忽略过程的时候使用
     * 只能发送成功 或者发送失败 互斥的通知
     */
    public void CompletableMethods() {
        Completable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                        //接收到成功的通知
                    }

                    @Override
                    public void onError(Throwable e) {
                        //接收到失败的通知
                    }
                });
    }


    /**
     * 单一发送操作符之  Singer
     * 只能用来发送单一数据 或者异常通知，别的通知发送不了
     * 看观察者方法也就知道了 收到数据 和异常监听
     */
    public void SingerMethod() {
        Single.create(new SingleOnSubscribe<Integer>() {
            @Override
            public void subscribe(SingleEmitter<Integer> emitter) throws Exception {
                emitter.onSuccess(1);//这里不是onnext 了只能是onSuccess
            }
        }).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Integer integer) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    /**
     * 单一发送操作符之  Maybe
     * 每次可以发送单一的数据 然后可以发送一个完成的通知或者失败的通知
     * 数据必须在完成或者失败之前发送，完成和失败之间是互斥关系
     */
    public void MayBeMethod() {
        Maybe.create(new MaybeOnSubscribe<Integer>() {

            @Override
            public void subscribe(MaybeEmitter<Integer> emitter) throws Exception {
                emitter.onSuccess(1);//只能调用一次 没有onnext
                emitter.onComplete();//发送完成状态
                //emitter.onError();失败的状态
            }
        }).subscribe(new MaybeObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Integer integer) {

                //接收数据
            }

            @Override
            public void onError(Throwable e) {

                //失败状态
            }

            @Override
            public void onComplete() {
                //完成状态
            }
        });

    }


//单一操作符完结


    /**
     * concat 链接操作符，用的还是蛮多的
     * 可以链接多个obervable 而且每个obervable类型必须相同，顺序是有序的，每个Observable 执行完oncomplete之后才能执行下一个
     */
    public void ConcatMethod() {
        //one
        Observable<Integer> one = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                //一定要调用oncomplete不然下一个TWO不会执行
                emitter.onComplete();

            }
        });


        Observable<Integer> two = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onNext(6);
                emitter.onComplete();


            }
        });

        Observable.concat(one, two).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                //接收到的消息是有序的123456
            }
        });

    }

    /**
     * concatMap操作符
     * 类似floatMap操作符 与floatmap不同的是
     * concatMap是有序的 从一个observable转到另一个observable
     */
    public void concatMapMethod() {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("http://wwww,baidu.com");
                emitter.onNext("http://www.jd.com");
                emitter.onComplete();
            }
        }).concatMap(new Function<String, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(final String s) throws Exception {
                return Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(111 + Integer.valueOf(s));
                        emitter.onComplete();
                    }
                });
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                //接到数据
            }
        });
    }


    /**
     * Debounce
     * 过滤掉发送很快的数据
     */
    public void DebounceMethod() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                SystemClock.sleep(100);
                emitter.onNext(2);
                SystemClock.sleep(500);
                emitter.onNext(3);
                SystemClock.sleep(400);
                emitter.onNext(4);
                emitter.onComplete();
            }
        }).debounce(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //收到信息，1 3，4//2被过滤掉了
                    }
                });
    }

    /**
     * defer 操作符 底层实现还是create()
     * 常用封装自己的代码成为RXJAVA的格式
     * greendao就是通过这个封装的
     */
    public void deferMethods() {

        //数据之间解耦了
        final GetNameCallback callback = new GetNameCallback();
        getObservable(callback).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                //打印
            }
        });

        //使用defer
        getDerfer(callback).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

            }
        });


    }

    /**
     * 没用defer的情况下基本用这种方式来封装自己写大代码成RXJAVA流
     *
     * @param callBack
     * @param <R>
     * @return
     */
    public <R> Observable<R> getObservable(final DeferCallBack<R> callBack) {

        return Observable.create(new ObservableOnSubscribe<R>() {

            @Override
            public void subscribe(ObservableEmitter<R> emitter) throws Exception {
                emitter.onNext(callBack.call());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    public <R> Observable<R> getDerfer(final DeferCallBack<R> callBack) {
        return Observable.defer(new Callable<ObservableSource<R>>() {
            @Override
            public ObservableSource<R> call() throws Exception {
                return Observable.just(callBack.call());
            }
        });

    }

    /**
     * distinct 操作符，发送流去重，很简单医用就会
     */
    public void DistinctMethod(){
             Observable.just(1,2,2,2,3,4,5,1).distinct().subscribe(new Consumer<Integer>() {
                 @Override
                 public void accept(Integer integer) throws Exception {

                     //2只收到一次
                 }
             });
    }




}
