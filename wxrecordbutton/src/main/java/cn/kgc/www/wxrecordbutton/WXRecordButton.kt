package cn.kgc.www.wxrecordbutton

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
class WXRecordButton : View {

    private val MODE_NORMAL = 0
    private val MODE_RECORD = 1
    private var buttonMode = MODE_NORMAL

    private lateinit var oval: RectF
    private var mProgress = 0
    private var mMax = 0
    private var angle = 0f
    private var mRadius = 0f
    private var mRadiusBig = 0f
    private var mRadiusSmall = 0f
    private var zoom = 0.8f
    private var listener: GestureListener? = null
    private val dp10 = resources.getDimension(R.dimen.dp10)
    private val stroke = resources.getDimension(R.dimen.dp6)
    private lateinit var mPaint: Paint
    private var changeAnim: ValueAnimator? = null
    private lateinit var progressPaint: Paint
    private val mHandler = object: Handler(){
        override fun handleMessage(msg: Message?) {
            listener?.onLongPressStart()
            //长按事件发生了
            changeButtonMode(MODE_RECORD)
        }
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

    fun setMax(max: Int){
        mMax = max
    }

    fun setProgress(progress: Int){
        mProgress = progress
        if(mMax != 0) {
            angle = mProgress.toFloat() / mMax.toFloat() * 360f
        }else{
            angle = 0f
        }
        if(angle >= 360){
            listener?.onLongPressForceOver()
            post {
                changeButtonMode(MODE_NORMAL)
            }

        }
//        invalidate()
        postInvalidate()
    }

    fun setGestureListener(listener: GestureListener){
        this.listener = listener
    }

    interface GestureListener{
        fun onClick()
        fun onLongPressStart()
        fun onLongPressEnd()
        fun onLongPressForceOver()
    }
    private fun init(){
        mPaint = Paint()
        mPaint.isAntiAlias = true

        progressPaint = Paint()
        progressPaint.isAntiAlias = true
        progressPaint.color = Color.GREEN
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = stroke
    }
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mRadius = Math.min(width,height)/2f
        mRadiusBig = mRadius * zoom
        mRadiusSmall = mRadius * zoom - dp10
    }

    private fun changeButtonMode(targetMode: Int){
        buttonMode = targetMode
        when(buttonMode){
            MODE_NORMAL->{
//                mRadiusBig = mRadius * (zoom + 0f)
//                mRadiusSmall = mRadius * (zoom - 0f) - dp10
                changeModeAnim(0.2f, 0f)
            }
            MODE_RECORD->{
                changeModeAnim(0f,0.2f)
//                mRadiusBig = mRadius * (zoom + 0.2f)
//                mRadiusSmall = mRadius * (zoom - 0.2f) - dp10
//                oval = RectF(width / 2f - mRadiusBig + stroke / 2f, height / 2f - mRadiusBig + stroke / 2f, width / 2f + mRadiusBig - stroke / 2f, height / 2f + mRadiusBig - stroke / 2f)
            }
        }
        postInvalidate()
    }

    //从Normal->Record
    private fun changeModeAnim(start: Float, end: Float){
        if(changeAnim == null || !changeAnim!!.isRunning){
            changeAnim = ValueAnimator.ofFloat(start, end).setDuration(250)
            changeAnim!!.addUpdateListener{
                value->
                val animValue = value.animatedValue.toString().toFloat()
                mRadiusBig = mRadius * (zoom + animValue)
                mRadiusSmall = mRadius * (zoom - animValue) - dp10
                oval = RectF(width / 2f - mRadiusBig + stroke / 2f, height / 2f - mRadiusBig + stroke / 2f, width / 2f + mRadiusBig - stroke / 2f, height / 2f + mRadiusBig - stroke / 2f)
                invalidate()
            }
            changeAnim!!.start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        //先画大圆
        mPaint.color = Color.DKGRAY
        canvas.drawCircle(width/2f,height/2f,mRadiusBig,mPaint)

        mPaint.color = Color.WHITE
        canvas.drawCircle(width/2f,height/2f,mRadiusSmall,mPaint)

        if(buttonMode == MODE_RECORD){
            canvas.drawArc(oval, 270f, angle, false, progressPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                mHandler.sendEmptyMessageDelayed(0,250)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->{
                if(mHandler.hasMessages(0)){
                    mHandler.removeMessages(0)
                    //发生的是短按事件
                    listener?.onClick()
                }else{
                    if(buttonMode != MODE_NORMAL) {
                        listener?.onLongPressEnd()
                        //长按操作结束
                        changeButtonMode(MODE_NORMAL)
                    }
                }
            }
        }
        return true
    }
}