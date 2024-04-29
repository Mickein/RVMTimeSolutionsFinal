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
    //variable for going to dashboard page if user has a registered account
    lateinit var openDash: TextView
    //variables for firebase authentication
    private lateinit var auth: FirebaseAuth
    private lateinit var passwordEdit: EditText
    private lateinit var loginemail: EditText
    //variable for going to register page if user doesnt have an account
    private lateinit var goToReg: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        //Firebase Authentication
        passwordEdit = findViewById(R.id.edtLoginPassword)
        loginemail = findViewById(R.id.edtLoginEmail)
        openDash = findViewById(R.id.txtLogin)
        auth = Firebase.auth

        openDash = findViewById(R.id.txtLogin)
        openDash.setOnClickListener(View.OnClickListener {
            val password = passwordEdit.text.toString()
            val email = loginemail.text.toString()

            if(password.isEmpty()) {
                passwordEdit.error = "Type a password"
                return@OnClickListener  // Return to prevent further execution
            }

            if(email.isEmpty()) {
                loginemail.error = "Type an email"
                return@OnClickListener  // Return to prevent further execution
            }
            if(password.isNotEmpty() && email.isNotEmpty()){
                LoginUser(email, password)
            }
        })

        //Open Register Page
        openRegPage()
    }

    private fun LoginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Login Successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else {
                // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun openRegPage()
    {
        goToReg = findViewById(R.id.txtGoToRegisterPage)
        goToReg.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterPage::class.java)
            startActivity(intent)
        })
    }

}



