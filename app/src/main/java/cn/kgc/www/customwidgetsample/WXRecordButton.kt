package cn.kgc.www.customwidgetsample

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by kgc on 2017/10/30.
 */
class WXRecordButton: View {
    private val MODE_NORMAL = 0
    private val MODE_RECORD = 1

    private var buttonMode = MODE_NORMAL
    private lateinit var mPaint: Paint
    private var mRadiusBig = 0f
    private var mRadiusSmall = 0f
    private var mRadius = 0f
    private var zoom = 0.8f
    private var mMax = 0
    private var mProgress = 0
    private var angle = 0f
    private lateinit var progressPaint: Paint
    private var oval = RectF()
    private var gestureListener: GestureListener? = null
    private val stroke = resources.getDimension(R.dimen.dp6)
    private val dp10 = resources.getDimension(R.dimen.dp10)
    private var changeModeAnim: ValueAnimator? = null

    private val mHandler = object : Handler(){
        override fun handleMessage(msg: Message?) {
            //start long press
            gestureListener?.onLongPressStart()
            changeButtonMode(MODE_RECORD)
        }
    }

    interface GestureListener{
        fun onClick()
        fun onLongPressStart()
        fun onLongPressEnd()
        fun onLongPressForceOver()
    }

    constructor(context: Context):super(context){
        init()
    }
    constructor(context: Context, attrs: AttributeSet):this(context, attrs, 0){
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):super(context, attrs, defStyleAttr){
        init()
    }

    fun setGestureListener(listener: GestureListener){
        gestureListener = listener
    }

    fun setMax(max: Int){
        mMax = max
    }

    fun setProgress(progress: Int){
        mProgress = progress
        if(mMax !=0){
            angle = mProgress.toFloat() / mMax.toFloat() * 360.0f
        }else{
            angle = 0f
        }
        if(angle>=360f){
            mProgress = 0
            mMax = 0
            gestureListener?.onLongPressForceOver()
            changeModeAnim?.cancel()
            post {
                changeButtonMode(MODE_NORMAL)
            }

        }else{
            postInvalidate()
        }
    }

    private fun changeButtonAnim(start: Float, end: Float){
        if(changeModeAnim == null || !changeModeAnim!!.isRunning){
            changeModeAnim = ValueAnimator.ofFloat(start,end).setDuration(200)
            changeModeAnim!!.addUpdateListener {
                value->
                val animValue = value.animatedValue.toString().toFloat()
                mRadiusBig = mRadius*(zoom + animValue)
                mRadiusSmall = mRadius*(zoom - animValue) - dp10
                oval = RectF(mRadius - mRadiusBig + stroke/2f, mRadius - mRadiusBig +stroke/2f,
                        mRadius + mRadiusBig-stroke/2f, mRadius + mRadiusBig-stroke/2f)
                postInvalidate()
            }
            changeModeAnim!!.start()
        }
    }

    private fun changeButtonMode(targetMode: Int){
        this.buttonMode = targetMode
        when(buttonMode){
            MODE_NORMAL->{
//                mRadiusBig = mRadius * zoom
//                mRadiusSmall = mRadius*zoom - dp10
//
                changeButtonAnim(0.2f, 0f)
            }
            MODE_RECORD->{
                mProgress = 0
                angle = 0f

                changeButtonAnim(0.0f, 0.2f)
//                mRadiusBig = mRadius
//                mRadiusSmall = mRadius*(zoom - 0.2f) - dp10
//                oval = RectF(mRadius - mRadiusBig + stroke/2f, mRadius - mRadiusBig +stroke/2f,
//                        mRadius + mRadiusBig-stroke/2f, mRadius + mRadiusBig-stroke/2f)
            }
        }
//        postInvalidate()
    }
    private fun init(){
        mPaint = Paint()
        mPaint.isAntiAlias = true

        progressPaint = Paint()
        progressPaint.isAntiAlias = true
        progressPaint.color = Color.GREEN
        progressPaint.strokeWidth = stroke
        progressPaint.style = Paint.Style.STROKE

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mRadius = Math.min(width, height)/2f
        mRadiusBig = zoom * mRadius
        mRadiusSmall = zoom*mRadius - dp10
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = resources.getColor(R.color.video_gray, null)
        canvas.drawCircle(mRadius,mRadius,mRadiusBig,mPaint)

        mPaint.color = Color.WHITE
        canvas.drawCircle(mRadius,mRadius,mRadiusSmall,mPaint)

        if(buttonMode == MODE_RECORD){
            canvas.drawArc(oval, 270f, angle, false, progressPaint)
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN->{
                mHandler.sendEmptyMessageDelayed(0,500)
            }
            MotionEvent.ACTION_UP->{
                if(mHandler.hasMessages(0)){
                    mHandler.removeMessages(0)
                    //short click
                    gestureListener?.onClick()
                }else{
                    //long press
                    gestureListener?.onLongPressEnd()
                    changeButtonMode(MODE_NORMAL)
                }
            }
        }
        return true
    }
}