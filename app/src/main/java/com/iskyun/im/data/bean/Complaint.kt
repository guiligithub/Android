package com.iskyun.im.data.bean

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.iskyun.im.BR

class Complaint : BaseObservable(){
    var appCommonFileList: List<FileType> = ArrayList()//截屏图片路径
    var createTime: String = ""
    var isBlacklist:Boolean = false

   // var feedbackStatus:Int=0
   // var resultExplain:String=""
    //var refundNum:Int=0
    @Bindable
    var informerId:String=""//被举报用户id
        set(informerId) {
            field = informerId;
            notifyPropertyChanged(BR.informerId)
        }

    @Bindable
    var reportExplain:String=""//举报说明
        set(reportExplain) {
            field = reportExplain;
            notifyPropertyChanged(BR.reportExplain)
        }

    var reportReasons:Int=0//举报类型 色情1，广告2，诈骗3，其他4，通道投诉5


    @Bindable
    var telephoneNumber:String=""//电话号码
        set(telephoneNumber) {
            field = telephoneNumber;
            notifyPropertyChanged(BR.telephoneNumber)
        }

    var userId:Int=0

    class FileType {
        //        var fileType: String = ""
        var fileUrl: String = ""
    }
}
