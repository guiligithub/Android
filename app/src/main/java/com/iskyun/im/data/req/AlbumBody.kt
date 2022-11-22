package com.iskyun.im.data.req

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.iskyun.im.BR

class AlbumBody : BaseObservable() {
    @Bindable
    var commonFileId: List<String> = ArrayList()
        set(commonFileId) {
            field = commonFileId
            notifyPropertyChanged(BR.commonFileId)
        }

    @Bindable
    var photoList: List<String> = ArrayList()
        set(photoList) {
            field = photoList
            notifyPropertyChanged(BR.photoList)
        }
    var tag: Int = 0
}

enum class AlbumTag(var tag: Int) {
    MY_ALBUM(1),
    ADD_ALBUM(2),
    DELETE_ALBUM(3);
}
