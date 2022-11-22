package com.iskyun.im.ui.message.viewmodel;

import androidx.lifecycle.ViewModel;

import com.hyphenate.easeui.model.EaseEvent;
import com.iskyun.im.helper.LiveDataBus;

public class MessageViewModel extends ViewModel {

    private final LiveDataBus messageObservable;

    public MessageViewModel() {
        messageObservable = LiveDataBus.get();
    }

    public void setMessageChange(EaseEvent change) {
        messageObservable.with(change.event).postValue(change);
    }

    public LiveDataBus getMessageChange() {
        return messageObservable;
    }

}