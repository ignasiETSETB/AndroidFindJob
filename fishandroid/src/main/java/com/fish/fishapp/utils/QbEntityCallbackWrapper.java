package com.fish.fishapp.utils;

/**
 * Created by Isangi on 3/10/2016.
 */
import android.os.Bundle;

import com.quickblox.core.QBEntityCallback;

public class QbEntityCallbackWrapper<T> extends QbEntityCallbackTwoTypeWrapper<T, T> {
    public QbEntityCallbackWrapper(QBEntityCallback<T> callback) {
        super(callback);
    }

    @Override
    public void onSuccess(T t, Bundle bundle) {
        onSuccessInMainThread(t, bundle);
    }
}
