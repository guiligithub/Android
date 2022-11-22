package com.iskyun.im.utils.event;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DiamondChangeEvent(
    var consumeDiamond: Int = 0
) : Parcelable
