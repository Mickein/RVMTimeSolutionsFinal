package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginPage : AppCompatActivity() {
    lateinit var openDash: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var passwordEdit: EditText
    private lateinit var loginemail: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        passwordEdit = findViewById(R.id.edtLoginPassword)
        loginemail = findViewById(R.id.edtLoginEmail)
        openDash = findViewById(R.id.txtLogin)
        auth = Firebase.auth

        openDash.setOnClickListener()
        {
            val email = loginemail.text.toString()
            val password = passwordEdit.text.toString()
            LoginUser(email, password)
        }


    }

    fun openDashboardPage()
    {
        openDash = findViewById(R.id.txtLogin)
    openDash.setOnClickListener(View.OnClickListener {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    })

}

    private fun LoginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
// Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Login Successful", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    openDashboardPage()
                } else {
// If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}
