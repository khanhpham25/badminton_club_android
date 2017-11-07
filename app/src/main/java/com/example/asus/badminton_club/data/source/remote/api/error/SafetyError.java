package com.example.asus.badminton_club.data.source.remote.api.error;

import rx.functions.Action1;

/**
 * Created by Sun on 4/16/2017.
 */

public abstract class SafetyError implements Action1<Throwable> {

    /**
     * Don't override this method.
     * Override {@link SafetyError#onSafetyError(BaseException)} instead
     */
    @Override
    public void call(Throwable throwable) {
        if (throwable == null) {
            onSafetyError(BaseException.toUnexpectedError(new Throwable("Unknown exception")));
            return;
        }
        if (throwable instanceof BaseException) {
            onSafetyError((BaseException) throwable);
        } else {
            onSafetyError(BaseException.toUnexpectedError(throwable));
        }
    }

    public abstract void onSafetyError(BaseException error);
}
