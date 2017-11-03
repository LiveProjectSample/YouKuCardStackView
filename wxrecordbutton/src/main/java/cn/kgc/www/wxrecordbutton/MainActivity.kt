package cn.kgc.www.wxrecordbutton

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    val singleThreadExecutor = Executors.newSingleThreadExecutor()
    var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wxRecordButton.setMax(4000)

        wxRecordButton.setGestureListener(object: WXRecordButton.GestureListener{
            override fun onClick() {
                Toast.makeText(this@MainActivity, "onClick", Toast.LENGTH_SHORT).show()
            }

            override fun onLongPressStart() {
                Toast.makeText(this@MainActivity, "onLongPressStart", Toast.LENGTH_SHORT).show()
                isRecording = true
                singleThreadExecutor.execute(object: Runnable{
                    override fun run() {
                        var progress = 0
                        while(isRecording){
                            wxRecordButton.setProgress(progress)
                            Thread.sleep(25)
                            progress+= 25
                        }
                    }

                })
            }

            override fun onLongPressEnd() {
                isRecording = false
                Toast.makeText(this@MainActivity, "onLongPressEnd", Toast.LENGTH_SHORT).show()
            }

            override fun onLongPressForceOver() {
                isRecording = false
                wxRecordButton.post {
                    Toast.makeText(this@MainActivity, "onLongPressForceOver", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }
}
