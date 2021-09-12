package com.demirli.a55typingpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private var listOfWords: List<String>? = null

    private var count = 0
    private var countDownJob: Job? = null

    private var startPhase = true

    private var selectedWord = ""

    private var score = 0

    private var level = 1
    private var levelCountDownFactor = 1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listOfWords = listOf("sediment", "volcanic", "flows", "formation", "earlier", "eruptions", "summit", "elevation", "predominate", "vertical")

        setStartPhase()

        start_btn.setOnClickListener {
            chectStartOrNextPhaseAndRunMainLogic()
        }

        timesUpListener()
        autoLevelSwitchListener()
    }

    fun chectStartOrNextPhaseAndRunMainLogic(){
        if(startPhase == true){//StartPhase

            if(setLevel_et.text.toString() != ""){
                setLevel(autoLevel_switch.isChecked,setLevel_et.text.toString().toDouble())
            }else{
                setLevel(autoLevel_switch.isChecked,1.0)
            }

            resumeGame()
            setNextPhase()

        }else if(startPhase == false){//NextPhase

            val typedWord = type_et.text.toString()

            if(typedWord == selectedWord){
                score += count
                score_tv.setText("Score: " + score)

                if(setLevel_et.text.toString() != ""){
                    setLevel(autoLevel_switch.isChecked,setLevel_et.text.toString().toDouble())
                }else{
                    setLevel(autoLevel_switch.isChecked,1.0)
                }
                resumeGame()

            }else{
                Toast.makeText(this, "Wrong word, game over. Your score: $score", Toast.LENGTH_LONG).show()
                setStartPhase()
            }


        }
    }

    fun timesUpListener(){
        counter_tv.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(s.toString() == "0"){
                    Toast.makeText(this@MainActivity, "Times up, game over. Your score: $score", Toast.LENGTH_LONG).show()
                    setStartPhase()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun autoLevelSwitchListener(){
        autoLevel_switch.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked == false){
                    setLevel_et.visibility = View.VISIBLE
                }else if(isChecked == true){
                    setLevel_et.visibility = View.GONE
                }
            }
        })
    }

    fun setStartPhase(){
        startPhase = true
        if(countDownJob != null){
            countDownJob!!.cancel()
        }

        counter_tv.setText("COUNTER")
        word_tv.setText("WORD")
        type_et.setText("")
        start_btn.setText("START")

        score = 0
        level = 1
        levelCountDownFactor = 1.0
        level_tv.setText("Level: " + level.toString())
        score_tv.setText("Score: " + score)
    }

    fun setNextPhase(){
        startPhase = false
        start_btn.setText("NEXT")
    }

    fun resumeGame(){
        if(countDownJob != null){
            countDownJob!!.cancel()
        }

        selectedWord = listOfWords!!.shuffled()[0]
        word_tv.setText(selectedWord)

        count = (selectedWord.length.toDouble()/levelCountDownFactor).toInt()

        counter_tv.setText(count.toString())

        countDownFun()

        type_et.setText("")
    }

    fun setLevel(isAutoLevel: Boolean, levelCountDownInput: Double){

        if(isAutoLevel == true){
            when(score){
                in 1..10 -> {
                    level = 1
                    level_tv.setText("Level: " + level.toString())
                    levelCountDownFactor = 1.0
                }
                in 10..20 -> {
                    level = 2
                    level_tv.setText("Level: " + level.toString())
                    levelCountDownFactor = 2.0
                }
                in 20..1000 -> {
                    level = 3
                    level_tv.setText("Level: " + level.toString())
                    levelCountDownFactor = 3.0
                }
            }
        }else if(isAutoLevel == false){
            level = levelCountDownInput.toInt()
            level_tv.setText("Level: " + level.toString())
            levelCountDownFactor = levelCountDownInput
        }
    }

    fun countDownFun(){
        countDownJob= GlobalScope.launch(Dispatchers.Main) {
            repeat(count){
                if(count != 0){
                    delay(1000)
                    count --
                    counter_tv.setText(count.toString())
                }
            }
        }
    }
}
