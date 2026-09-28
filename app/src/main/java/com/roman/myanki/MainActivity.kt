package com.roman.myanki

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

data class Card(
    val front: String,
    val reading: String,
    val meaning: String,
    val category: String,
    var interval: Int = 0,
    var repetitions: Int = 0
)

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var front: TextView
    private lateinit var back: TextView
    private lateinit var category: TextView
    private lateinit var stats: TextView
    private lateinit var reveal: Button
    private lateinit var ratingRow: LinearLayout
    private var index = 0
    private var reviews = 0

    private val cards = mutableListOf(
        Card("日本", "にほん", "Японія", "Країни"),
        Card("学生", "がくせい", "Студент", "Слова"),
        Card("先生", "せんせい", "Вчитель / викладач", "Слова"),
        Card("会社員", "かいしゃいん", "Працівник компанії", "Слова"),
        Card("家族", "かぞく", "Сім'я", "Сім'я"),
        Card("兄", "あに", "Старший брат", "Сім'я"),
        Card("姉", "あね", "Старша сестра", "Сім'я"),
        Card("弟", "おとうと", "Молодший брат", "Сім'я"),
        Card("妹", "いもうと", "Молодша сестра", "Сім'я"),
        Card("いくら", "いくら", "Скільки коштує?", "Магазин"),
        Card("たかいですね", "たかいですね", "Дорого, правда?", "Магазин"),
        Card("じゃあ", "じゃあ", "Тоді / ну тоді", "Магазин"),
        Card("ください", "ください", "Дайте, будь ласка", "Магазин"),
        Card("です", "です", "Ствердження", "Граматика"),
        Card("ですか", "ですか", "Питання", "Граматика"),
        Card("じゃありません", "じゃありません", "Заперечення", "Граматика")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)
        front = findViewById(R.id.cardFront)
        back = findViewById(R.id.cardBack)
        category = findViewById(R.id.cardCategory)
        stats = findViewById(R.id.stats)
        reveal = findViewById(R.id.reveal)
        ratingRow = findViewById(R.id.ratingRow)

        reveal.setOnClickListener { showAnswer() }
        findViewById<Button>(R.id.speak).setOnClickListener { speak() }
        findViewById<Button>(R.id.hard).setOnClickListener { rate(1) }
        findViewById<Button>(R.id.good).setOnClickListener { rate(2) }
        findViewById<Button>(R.id.easy).setOnClickListener { rate(3) }

        showCard()
    }

    private fun showCard() {
        val c = cards[index]
        front.text = c.front
        category.text = c.category
        back.text = "${c.reading}\n\n${c.meaning}"
        back.visibility = View.GONE
        reveal.visibility = View.VISIBLE
        ratingRow.visibility = View.GONE
        stats.text = "${cards.size} карток • $reviews повторень"
    }

    private fun showAnswer() {
        back.visibility = View.VISIBLE
        reveal.visibility = View.GONE
        ratingRow.visibility = View.VISIBLE
        speak()
    }

    private fun rate(level: Int) {
        val c = cards[index]
        c.repetitions++
        c.interval = when (level) {
            1 -> maxOf(1, c.interval / 2)
            2 -> maxOf(1, if (c.interval == 0) 1 else c.interval * 2)
            else -> maxOf(2, if (c.interval == 0) 4 else c.interval * 4)
        }
        reviews++
        index = (index + 1) % cards.size
        showCard()
    }

    private fun speak() {
        if (!::tts.isInitialized) return
        tts.speak(cards[index].front, TextToSpeech.QUEUE_FLUSH, null, "card")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.JAPAN
            tts.setSpeechRate(0.82f)
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}
