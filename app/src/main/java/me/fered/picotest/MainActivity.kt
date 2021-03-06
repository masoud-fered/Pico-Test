package me.fered.picotest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.fered.picotest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var pictureAdapter: PictureAdapter
    private lateinit var pictureRecyclerView: RecyclerView
    private var pictureListShuffled: MutableList<Picture> = ArrayList()
    private var pictureList: MutableList<Picture> = ArrayList()
    private var backgroundLevelStep: Int = BACKGROUND_MAX_LEVEL / (TIME * 20)

    private companion object {
        private const val TIME: Int = 30
        private const val BACKGROUND_MAX_LEVEL = 10000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initGame()
        startGame()

        activityMainBinding.showCardButton.setOnClickListener {
            var firstPic: Picture? = null
            var secondPic: Picture? = null
            for (i in 0 until pictureListShuffled.size) {
                if (!pictureListShuffled[i].solved) {
                    firstPic = pictureListShuffled[i]
                    secondPic = if (firstPic.id!! % 2 == 0) {
                        pictureList[(firstPic.id!! - 1) - 1]
                    } else {
                        pictureList[(firstPic.id!! + 1) - 1]
                    }
                    break
                }
            }

            if (firstPic != null && secondPic != null) {
                pictureRecyclerView[pictureListShuffled.indexOf(firstPic)].performClick()
                pictureRecyclerView[pictureListShuffled.indexOf(secondPic)].performClick()
            } else {
                it.isEnabled = false
            }

        }

        activityMainBinding.moreTimeButton.setOnClickListener {
            activityMainBinding.parentLayout.background.level = 0
        }

        activityMainBinding.restartGameButton.setOnClickListener { restartGame() }

    }

    private fun initGame() {
        pictureRecyclerView = activityMainBinding.pictureRecyclerView
        pictureAdapter = PictureAdapter(pictureListShuffled)
        pictureRecyclerView.layoutManager = GridLayoutManager(this, 4)
        pictureRecyclerView.adapter = pictureAdapter

        pictureList.add(Picture(1, ContextCompat.getDrawable(this, R.drawable.girafa), 1, false))
        pictureList.add(Picture(2, ContextCompat.getDrawable(this, R.drawable.girafa), 1, false))
        pictureList.add(Picture(3, ContextCompat.getDrawable(this, R.drawable.leao), 2, false))
        pictureList.add(Picture(4, ContextCompat.getDrawable(this, R.drawable.leao), 2, false))
        pictureList.add(Picture(5, ContextCompat.getDrawable(this, R.drawable.macaco), 3, false))
        pictureList.add(Picture(6, ContextCompat.getDrawable(this, R.drawable.macaco), 3, false))
        pictureList.add(Picture(7, ContextCompat.getDrawable(this, R.drawable.pato), 4, false))
        pictureList.add(Picture(8, ContextCompat.getDrawable(this, R.drawable.pato), 4, false))
        pictureList.add(Picture(9, ContextCompat.getDrawable(this, R.drawable.tigre), 5, false))
        pictureList.add(Picture(10, ContextCompat.getDrawable(this, R.drawable.tigre), 5, false))
        pictureList.add(Picture(11, ContextCompat.getDrawable(this, R.drawable.touro), 6, false))
        pictureList.add(Picture(12, ContextCompat.getDrawable(this, R.drawable.touro), 6, false))
        pictureList.add(Picture(13, ContextCompat.getDrawable(this, R.drawable.gato), 7, false))
        pictureList.add(Picture(14, ContextCompat.getDrawable(this, R.drawable.gato), 7, false))
        pictureList.add(Picture(15, ContextCompat.getDrawable(this, R.drawable.rato), 8, false))
        pictureList.add(Picture(16, ContextCompat.getDrawable(this, R.drawable.rato), 8, false))

        pictureListShuffled.addAll(pictureList)
        pictureListShuffled.shuffle()

        pictureAdapter.notifyDataSetChanged()

        activityMainBinding.parentLayout.background.level = 0
        activityMainBinding.timeTextView.text = TIME.toString()
    }

    private fun startGame() {
        val handler = Handler(Looper.getMainLooper())
        val runnable: Runnable = object : Runnable {
            override fun run() {
                when {
                    pictureAdapter.getSolvedCount() == 8 -> {
                        finishGame(1)
                    }
                    activityMainBinding.parentLayout.background.level >= BACKGROUND_MAX_LEVEL -> {
                        finishGame(0)
                    }
                    else -> {
                        activityMainBinding.parentLayout.background.level += backgroundLevelStep
                        activityMainBinding.timeTextView.text = (TIME - (activityMainBinding.parentLayout.background.level / (BACKGROUND_MAX_LEVEL / TIME))).toString()
                    }
                }
                handler.postDelayed(this, 50)
            }
        }
        handler.post(runnable)
    }

    private fun finishGame(status: Int) {
        when (status) {
            1 -> {
                activityMainBinding.timeTextView.text = "?????????? ?????? ?????????? ????????"
            }
            0 -> {
                activityMainBinding.timeTextView.text = "???????????????? ?????? ?????? ???? ?????????? ????????"
            }
        }
        activityMainBinding.moreTimeButton.isEnabled = false
        activityMainBinding.showCardButton.isEnabled = false
        for (i in 0 until pictureRecyclerView.size) {
            pictureRecyclerView[i].isEnabled = false
        }

//        handler.removeCallbacksAndMessages(null)
        activityMainBinding.restartGameButton.visibility = VISIBLE
    }

    private fun restartGame() {
        activityMainBinding.parentLayout.background.level = 0
        pictureAdapter.setSolvedCount(0)
        pictureList.clear()
        pictureListShuffled.clear()
        for (i in 0 until pictureRecyclerView.size) {
            pictureRecyclerView[i].isEnabled = true
        }
        activityMainBinding.restartGameButton.visibility = INVISIBLE
        activityMainBinding.moreTimeButton.isEnabled = true
        activityMainBinding.showCardButton.isEnabled = true
        initGame()
    }
}