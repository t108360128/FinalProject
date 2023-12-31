package com.example.test

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
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

class MainActivity3 : AppCompatActivity() {

    // 新增變數
    private lateinit var dbrw: SQLiteDatabase // 資料庫
    private val items = ArrayList<String>() // 類型
    private val selectedTypes = ArrayList<String>() // 選擇類型
    private lateinit var adapter: ArrayAdapter<String> //
    private lateinit var tv_showdate: TextView // 顯示日期
    private lateinit var btn_sel_date: Button // 選擇日期按鈕
    private lateinit var spinner_am3_type: Spinner //
    private lateinit var btn_query: Button // 查詢
    private val itemsForSpinner = arrayOf("食物", "交通", "娛樂", "住宿", "購物") // 類型表內容
    private lateinit var selectedDate: String
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        // 連結xml物件
        tv_showdate = findViewById(R.id.tv_showdate)
        btn_sel_date = findViewById(R.id.btn_sel_date)
        spinner_am3_type = findViewById(R.id.spinner_am3_type)
        btn_query = findViewById(R.id.btn_query)

        // 設定ListView的適配器以顯示items列表中的資料
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter

        // 設定Spinner以顯示itemsForSpinner列表中的資料
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemsForSpinner)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_am3_type.adapter = spinnerAdapter

        // 初始化資料庫實例
        dbrw = MyDBHelper(this).writableDatabase

        //設定 Spinner 的選擇監聽器
        spinner_am3_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 當選擇項目時觸發
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 獲取選中的項目
                val selectedType = spinner_am3_type.selectedItem.toString()
                // 將選中的項目加入到selectedTypes列表中
                selectedTypes.add(selectedType)
            }
            // 當沒有選擇項目時觸發
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 在此不做任何操作
            }
        }

        // 選擇日期
        btn_sel_date.setOnClickListener {
            // 取得當前日期
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // 創建日期選擇對話框
            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // 將所選日期格式化為 yyyy-MM-dd 的形式
                selectedDate = "$selectedYear-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}"
                // 設置所選日期到 tv_showdate（一個 TextView）中
                tv_showdate.setText(selectedDate)
            }, year, month, day)

            // 顯示日期選擇對話框
            datePickerDialog.show()
        }

        // 查詢按鈕功能
        btn_query.setOnClickListener {
            // 從 TextView 和 Spinner 獲取相應的值
            val date = selectedDate
            val selectedType = spinner_am3_type.selectedItem.toString()

            // 檢查輸入的金額和日期是否為空
            /*if (date.isEmpty()) {
                showToast("欄位請勿留空")  // 若為空，顯示錯誤提示
                return@setOnClickListener
            }*/

            //查找
            try {
                val queryString = if (date.isEmpty())
                    "SELECT * FROM accountTable WHERE "
                else
                    "SELECT * FROM accountTable WHERE date LIKE '${date}'"

                val c = dbrw.rawQuery(queryString, null)
                c.moveToFirst() //從第一筆開始輸出
                items.clear() //清空舊資料
                showToast("共有${c.count}筆資料")
                for (i in 0 until c.count) {
                    //加入新資料
                    items.add("金額：${c.getInt(0)}\t\t\t\t 日期:${c.getString(1)}\t\t\t\t")
                    c.moveToNext() //移動到下一筆
                }
                adapter.notifyDataSetChanged() //更新列表資料
                c.close() //關閉 Cursor
            } catch (e: SQLException) {
                handleDatabaseError(e)  // 處理資料庫操作時的錯誤
            }
        }


        // 切換頁面
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page1 -> {
                    // 切换到 Activity1
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page2 -> {
                    // 切换到 Activity2
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                    true
                }
                R.id.page3 -> {
                    // 切换到 Activity3
                    val intent = Intent(this, MainActivity3::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }

        }
    }

    private fun handleDatabaseError(e: SQLException) {
        e.printStackTrace()
        showToast("操作失敗: ${e.message}")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearEditTexts() {
        tv_showdate.setText("")    // 清空日期的 TextView 中的文字
    }

    // 關閉資料庫
    override fun onDestroy() {
        super.onDestroy()
        dbrw.close()
    }
}