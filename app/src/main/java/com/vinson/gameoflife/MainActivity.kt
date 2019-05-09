package com.vinson.gameoflife

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {

    private lateinit var gameScreen: GameScreen
    private lateinit var deviceProfile: DeviceProfile
    private lateinit var model: CellModel

    private var disposables = HashSet<Disposable>()

    private val colAndRow : Int
        get() = colAndRowArray[colAndRowIndex]
    private var colAndRowIndex = 3
    private val colAndRowArray: IntArray by lazy {
        resources.getIntArray(R.array.ColAndRow)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameScreen = findViewById(R.id.screen)

        initGameEnvironment()
    }

    private fun initGameEnvironment() {
        deviceProfile = DeviceProfile(resources.displayMetrics.scaledDensity * 400, colAndRow)
        gameScreen.setup(deviceProfile)
        model = CellModel(colAndRow, getObserver())
    }

    private fun getObserver(): Observer<ArrayList<Cell>> {
        return object : Observer<ArrayList<Cell>> {

            override fun onSubscribe(d: Disposable) {
                println("onSubscribe $d")
                disposables.add(d)
            }

            override fun onNext(value: ArrayList<Cell>) {
                println("onNext $value, ${System.currentTimeMillis()}")
                gameScreen.refresh(value)
            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
                println("onComplete")
            }
        }
    }

    fun onClickGameScreen(v: View) {
        if (v !is GameScreen) return
        model.changeCellFromUser(v.getClickCellPos())
    }

    fun onClickResetGame(v: View) {
        model.resetGameWorld()
    }

    fun onClickSmaller(v: View) {
        if (colAndRowIndex >= colAndRowArray.size - 1) return
        unregister()
        colAndRowIndex++
        initGameEnvironment()
    }

    fun onClickBigger(v: View) {
        if (colAndRowIndex <= 0) return
        unregister()
        colAndRowIndex--
        initGameEnvironment()
    }

    private fun unregister() {
        for (d in disposables) {
            d.dispose()
        }
        disposables.clear()
    }
}
