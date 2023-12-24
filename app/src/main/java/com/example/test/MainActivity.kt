package com.example.test

import android.R.id.edit
import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var dbrw: SQLiteDatabase
    private val items = ArrayList<String>()
    private val selectedTypes = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var ed_amount: EditText
    private lateinit var ed_date: EditText
    private lateinit var btn_insert: Button
    private lateinit var btn_query: Button
    private lateinit var btn_update: Button
    private lateinit var btn_pick_date: Button
    private lateinit var listView: ListView
    private lateinit var spinner_type: Spinner
    private val itemsForSpinner = arrayOf("食物", "交通", "娛樂", "住宿", "購物")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ed_amount = findViewById(R.id.ed_income)
        ed_date = findViewById(R.id.ed_date)
        btn_insert = findViewById(R.id.btn_insert)
        btn_query = findViewById(R.id.btn_query)
        btn_pick_date = findViewById(R.id.btn_pick_date)
        listView = findViewById(R.id.listview)
        spinner_type = findViewById(R.id.spinner_type)
        btn_update = findViewById(R.id.btn_update)

        // 設定ListView的適配器以顯示items列表中的資料
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

// 設定Spinner以顯示itemsForSpinner列表中的資料
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemsForSpinner)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_type.adapter = spinnerAdapter

// 初始化資料庫實例
        dbrw = MyDBHelper(this).writableDatabase

// 設定Spinner的選擇監聽器
        spinner_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 當選擇項目時觸發
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 獲取選中的項目
                val selectedType = spinner_type.selectedItem.toString()
                // 將選中的項目加入到selectedTypes列表中
                selectedTypes.add(selectedType)
                // 通知ListView的適配器更新數據
                adapter.notifyDataSetChanged()
            }
            // 當沒有選擇項目時觸發
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 在此不做任何操作
            }
        }
        btn_insert.setOnClickListener {
            // 從 EditText 和 Spinner 獲取相應的值
            val amount = ed_amount.text.toString()
            val date = ed_date.text.toString()
            val selectedType = spinner_type.selectedItem.toString()

            // 檢查輸入的金額和日期是否為空
            if (amount.isEmpty() || date.isEmpty()) {
                showToast("欄位請勿留空")  // 若為空，顯示錯誤提示
                return@setOnClickListener
            }

            try {
                // 嘗試執行插入操作
                dbrw.execSQL(
                    "INSERT INTO accountTable( amount, date, typeIndex) VALUES ( ?, ?, ?)",
                    arrayOf(amount, date, itemsForSpinner.indexOf(selectedType))
                )
                showToast("已新增記錄")  // 顯示插入成功的提示
                clearEditTexts()  // 清除 EditText 中的輸入
            } catch (e: SQLException) {
                handleDatabaseError(e)  // 處理資料庫操作時的錯誤
            }
        }

        btn_update.setOnClickListener {
            // 從 EditText 和 Spinner 獲取相應的值
            val amount = ed_amount.text.toString()
            val date = ed_date.text.toString()
            val selectedType = spinner_type.selectedItem.toString()

            // 檢查輸入的金額和日期是否為空
            if (amount.isEmpty() || date.isEmpty()) {
                showToast("欄位請勿留空")
                return@setOnClickListener
            }

            try {
                // 嘗試執行更新操作
                dbrw.execSQL(
                    "UPDATE accountTable SET amount = ?, typeIndex = ? WHERE date = ?",
                    arrayOf(amount, itemsForSpinner.indexOf(selectedType), date)
                )
                showToast("已更新記錄")  // 顯示更新成功的提示
                clearEditTexts()  // 清除 EditText 中的輸入
            } catch (e: SQLException) {
                handleDatabaseError(e)  // 處理資料庫操作時的錯誤
            }
        }


        btn_query.setOnClickListener {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            queryDataByDate(currentDate)
        }

        btn_pick_date.setOnClickListener {
            // 取得當前日期
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // 創建日期選擇對話框
            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // 將所選日期格式化為 yyyy-MM-dd 的形式
                val selectedDate = "$selectedYear-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}"
                // 設置所選日期到 ed_date（一個 EditText）中
                ed_date.setText(selectedDate)
            }, year, month, day)

            // 顯示日期選擇對話框
            datePickerDialog.show()
        }
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearEditTexts() {
        ed_amount.setText("")  // 清空金額的 EditText 中的文字
        ed_date.setText("")    // 清空日期的 EditText 中的文字
    }

    private fun handleDatabaseError(e: SQLException) {
        e.printStackTrace()
        showToast("操作失敗: ${e.message}")
    }

    private fun queryDataByDate(date: String) {
        val c = dbrw.rawQuery("SELECT * FROM accountTable WHERE date = ?", arrayOf(date))
        handleQueryResults(c)
    }

    private fun handleQueryResults(c: Cursor) {
        items.clear()  // 清空現有的 items 列表，準備放入新的資料

        // 檢查 Cursor 是否有效且有資料
        if (c != null && c.moveToFirst()) {
            showToast("共有${c.count}筆")  // 顯示當前查詢結果的資料筆數

            // 使用 do-while 循環 Cursor 中的每一行
            do {
                val amount = c.getString(0)  // 從第一列獲取金額資料
                val date = c.getString(1)    // 從第二列獲取日期資料
                val typeIndex = c.getInt(2)  // 從第三列獲取類型的索引值
                val type = if (typeIndex >= 0 && typeIndex < itemsForSpinner.size) itemsForSpinner[typeIndex] else "未知"
                // 如果索引值在合法範圍內，從 itemsForSpinner 中取得對應的類型名稱，否則顯示 "未知"

                // 將獲取到的資料組合成一個字符串，然後添加到 items 列表中
                items.add("金額: $amount\t\t\t日期: $date\t\t\t類型: $type")
            } while (c.moveToNext())  // 移動到下一行，直到所有資料都被處理完畢

            adapter.notifyDataSetChanged()  // 通知 Adapter 更新資料
            c.close()  // 關閉 Cursor
        } else {
            showToast("當日沒有資料")  // 如果 Cursor 為空或沒有資料，顯示相應的提示消息
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbrw.close()
    }
}