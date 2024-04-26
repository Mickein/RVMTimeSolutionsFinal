package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ProfileFragment : Fragment() {
    private lateinit var nameText: TextView
    private lateinit var surnameText: TextView
    private lateinit var emailText: TextView
    private lateinit var passwordText: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        nameText = view.findViewById(R.id.txtName2)
        surnameText = view.findViewById(R.id.txtSurname)
        emailText = view.findViewById(R.id.edtEmail)
        passwordText = view.findViewById(R.id.txtPassword)

        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        user?.let {
            // User is signed in
            val name = user.displayName
            val email = user.email
            val surname = user


            // Update the TextViews with user data
            nameText.text = name
            emailText.text = email


        } ?: run {
            // No user is signed in
            // Redirect to login or handle the situation accordingly
        }

        return view
    }

}