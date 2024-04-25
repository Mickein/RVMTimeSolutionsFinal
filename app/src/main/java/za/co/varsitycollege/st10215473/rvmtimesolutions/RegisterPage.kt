package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class RegisterPage : AppCompatActivity() {
    lateinit var openLog: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)
        openLoginPage()
    }
    fun openLoginPage()
    {
        openLog = findViewById(R.id.txtRegister)
        openLog.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        })

    }
}