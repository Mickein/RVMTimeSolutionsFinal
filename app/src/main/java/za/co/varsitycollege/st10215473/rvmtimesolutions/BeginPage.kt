package za.co.varsitycollege.st10215473.rvmtimesolutions


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class BeginPage : AppCompatActivity() {
    lateinit var openLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.begin_page)

        openLoginPage()
    }

    fun openLoginPage()
    {
        openLog = findViewById(R.id.txtBegin)
        openLog.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        })

    }
}