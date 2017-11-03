package cn.kgc.www.cardstackview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * Created by kgc on 2017/11/3.
 */
class CardStackView : ViewGroup {
    val childTopMargin = resources.getDimension(R.dimen.dp25)
    val childSideMargin = resources.getDimension(R.dimen.dp50)

    constructor(context: Context):super(context){
    }
    constructor(context: Context, attrs: AttributeSet):this(context, attrs, 0){
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):super(context, attrs, defStyleAttr){
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for(childIndex in 0..childCount-1) {
            val child = getChildAt(childIndex)
            child.layout(childSideMargin.toInt(), childTopMargin.toInt(), childSideMargin.toInt()+child.measuredWidth, childTopMargin.toInt()+child.measuredHeight)
        }
    }

    //wrap_content
    //match_parent
    //绝对数值
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        var widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        widthSpecSize = (widthSpecSize - childSideMargin * 2).toInt()

        var heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        var heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        heightSpecSize = (heightSpecSize - childTopMargin).toInt()

        for(childIndex in 0..childCount-1){
            val child = getChildAt(childIndex)
            val myWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize, widthSpecMode)
            val myHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpecSize, heightSpecMode)
            child.measure(myWidthMeasureSpec, myHeightMeasureSpec)
        }
    }
}