package com.iskyun.im.data.bean

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.iskyun.im.BR

class Suggest: BaseObservable(){
    var imgList: List<FileType> = ArrayList()//截屏图片路径
    var createTime: String = ""

    @Bindable
    var ideaText:String=""//建议说明
    set(reportExplain) {
        field = reportExplain;
        notifyPropertyChanged(BR.reportExplain)
    }

    var userId:Int=0

    class FileType {
        //        var fileType: String = ""
        var fileUrl: String = ""
    }
}
