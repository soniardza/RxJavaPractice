package com.example.rxjavapractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.rxjavapractice.databinding.ActivityMainBinding
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val bindingReference : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var compositeDisposables: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindingReference.root)

        compositeDisposables = CompositeDisposable()

        bindingReference.exerciseOneButton.setOnClickListener {
            subscribingExerciseOne(bindingReference.toggleButton.isChecked)
        }

        bindingReference.exerciseTwoButton.setOnClickListener {
            subscribingExerciseTwo()

        }

        bindingReference.exerciseThreeButton.setOnClickListener {
            exerciseThree(
                "hola",
                "sonia"
            )
        }

        bindingReference.subscriptionButton.setOnClickListener {
            exerciseFour()
        }

        bindingReference.newActivityButton.setOnClickListener {

            openNewActivity()
        }

    }


    private fun openNewActivity() {
        val intent = Intent(this, RxJavaSecondActivity::class.java)
        startActivity(intent)

        finish()
    }
    override fun onStop() {
        super.onStop()
        compositeDisposables.clear()
    }


    // Exercise one
    // lo que está dentro de los paréntesis del create no se ejecuta hasta que alguien se subscriba
    private fun exerciseOne(isActive: Boolean): Completable {
        val delay: Long = if (isActive) EXERCISE_ONE_SUCCESS_TIME_SECONDS else EXERCISE_ONE_ERROR_TIME_SECONDS
        return Completable.create { emitter ->
            if(isActive) {
                emitter.onComplete()
            } else {
                emitter.onError(Throwable("Error!!"))
            }
        }.delay(delay, TimeUnit.SECONDS, Schedulers.computation(), true)
    }

    private fun subscribingExerciseOne(isActive: Boolean) {
        compositeDisposables.add(
            exerciseOne(isActive)
                .subscribeOn(Schedulers.io())
                .observeOn((AndroidSchedulers.mainThread()))
                .subscribe({
                    showMsgToast("You have successfully subscribed")
                },
                    { _ -> showMsgToast("You could not subscribed") })
        )
    }

    // exercise two
    private fun exerciseTwo(number: Double): Single<Double> {
        return Single.create<Double> { emitter ->
            emitter.onSuccess(number*1.2 )
        }.delay(EXERCISE_TWO_SUCCESS_TIME_SECONDS, TimeUnit.SECONDS)
    }

    private fun subscribingExerciseTwo() {
        compositeDisposables.add(
            exerciseTwo(3.333)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        showMsgToast("Multiplication result of 3.33 * 1.2 is $result")
                    },
                    { showMsgToast("Error!!!") }
                )
        )
    }

    // exercise three

    private fun exerciseThree(text1: String, text2: String) {
        // singles
        val singleA = singleOne(text1, text2).subscribeOn(Schedulers.newThread())
        val singleB = singleTwo(text2).subscribeOn(Schedulers.newThread())

        // observable
        compositeDisposables.add(Single.zip(
            singleA,
            singleB,
            { singleA, singleB -> "$singleA $singleB" }
        ).observeOn(AndroidSchedulers.mainThread()).subscribe { msg ->
            showMsgToast(msg)
        })
    }

    // observable
    private fun singleOne(text1: String, text2: String ): Single<String> {
        return Single.create<String> { emitter ->
            emitter.onSuccess( "$text1 $text2")
        }.delay(EXERCISE_THREE_SUCCESS_TIME_SECONDS, TimeUnit.SECONDS)
    }

    private fun singleTwo(text: String): Single<String> {
        return Single.create { emitter ->
            emitter.onSuccess(
                text.uppercase()
            )
        }
    }

    // exercise four

    private fun exerciseFour() {
        val subscription = Completable.create { emitter ->
            emitter.onComplete()
        }.delay(EXERCISE_FOUR_SUCCESS_TIME_SECONDS, TimeUnit.SECONDS)

        compositeDisposables.add(
            subscription
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose {
                    Log.d("RxJavaActivity", "The disposable was disposed")
                }
                .subscribe {
                    showMsgToast("The subscription is over")
                }
        )
    }

    private fun showMsgToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val EXERCISE_ONE_SUCCESS_TIME_SECONDS: Long = 3
        const val EXERCISE_ONE_ERROR_TIME_SECONDS: Long = 2
        const val EXERCISE_TWO_SUCCESS_TIME_SECONDS: Long = 2
        const val EXERCISE_THREE_SUCCESS_TIME_SECONDS: Long = 1
        const val EXERCISE_FOUR_SUCCESS_TIME_SECONDS: Long = 5
    }
}
