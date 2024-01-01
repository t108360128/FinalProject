package com.example.test
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button

import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import android.widget.AdapterView
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
class MainActivity2 : AppCompatActivity() {

    private lateinit var dbrw: SQLiteDatabase
    private val items = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var txtTotal :TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        txtTotal = findViewById(R.id.txtTotal)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)

        // 初始化資料庫實例
        dbrw = MyDBHelper(this).writableDatabase

        val totalAmountValue = intent.getStringExtra("TOTAL_AMOUNT_VALUE")

        // 檢查是否有值，然後顯示
        if (!totalAmountValue.isNullOrBlank()) {
            // 將 totalAmountValue 轉換為適當的數值類型
            val totalAmount = totalAmountValue.toInt()

            // 在這裡可以顯示 totalAmount，例如將其設置到 TextView 中
            val totalAmountTextView: TextView = findViewById(R.id.txtTotal)
            totalAmountTextView.text = "$totalAmount"
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page1 -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page2 -> {
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page3 -> {
                    val intent = Intent(this, MainActivity3::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}