package cn.kgc.www.customwidgetsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class MainActivity : AppCompatActivity() {
    val singleThreadPool = Executors.newSingleThreadExecutor()
    var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wxRecordButton.setMax(4000)
        wxRecordButton.setGestureListener(object: WXRecordButton.GestureListener{
            override fun onClick() {

            }

            override fun onLongPressStart() {
                singleThreadPool.execute(object: Runnable{
                    override fun run() {
                        isRecording = true
                        var progress = 0
                        while(isRecording){
                            Thread.sleep(50)
                            progress+=50
                            wxRecordButton.setProgress(progress)
                        }
                    }

                })
            }

            override fun onLongPressEnd() {
                isRecording = false
            }

            override fun onLongPressForceOver() {
                isRecording = false
            }

        })
    }
}
