package com.example.rxjavapractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rxjavapractice.databinding.ActivityRxJavaSecondBinding

class RxJavaSecondActivity : AppCompatActivity() {

    private val bindingReference: ActivityRxJavaSecondBinding by lazy {
        ActivityRxJavaSecondBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindingReference.root)
    }
}
