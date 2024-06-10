package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Profile
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Timesheets
import za.co.varsitycollege.st10215473.rvmtimesolutions.Decorator.CircularImageView
import java.io.File
import java.io.IOException

class ProfileFragment : Fragment() {
    private lateinit var nameText: TextView
    private lateinit var surnameText: TextView
    private lateinit var emailText: TextView
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var addProfilePicture: ImageView
    private var uri: Uri? = null
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private var addedAnImage: Boolean = false
    private lateinit var profilePicture: CircularImageView
    private lateinit var storageRef: StorageReference
    private lateinit var profileList: ArrayList<Profile>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        nameText = view.findViewById(R.id.txtName2)
        surnameText = view.findViewById(R.id.edtSurname)
        emailText = view.findViewById(R.id.edtEmail)

        addProfilePicture = view.findViewById(R.id.AddProfilePicture)
        profilePicture = view.findViewById(R.id.profilePicture)
        storageRef = FirebaseStorage.getInstance().getReference("profile")
        firebaseRef = FirebaseDatabase.getInstance().getReference("Profile")

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            profilePicture.setImageURI(it)
            if(it != null){
                addedAnImage = true
                uri = it
                uploadImageToFirebase()
            }
        }

        uri = createUri()//Some code by CodingZest on Youtube: https://www.youtube.com/watch?v=9XSlbZN1yFg&t=761s
        registerPictureLauncher()//Some code by CodingZest on Youtube: https://www.youtube.com/watch?v=9XSlbZN1yFg&t=761s

        addProfilePicture.setOnClickListener{
            val options = arrayOf("Take Photo", "Choose from Gallery", "Remove Profile Picture")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Select Option")
            builder.setItems(options) { dialogInterface: DialogInterface, which: Int ->
                when (which) {
                    0 -> {
                        checkCameraPermissionAndOpen()//Some code by CodingZest on Youtube: https://www.youtube.com/watch?v=9XSlbZN1yFg&t=761s
                    }
                    1 -> {
                        pickImage.launch("image/*")
                    }
                    2 -> {
                        removeProfilePicture()
                    }

                }
                dialogInterface.dismiss()
            }
            builder.show()
        }

        auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        currentUser?.let {
            val uid = currentUser.uid
            firebaseRef = FirebaseDatabase.getInstance().reference.child("Profile").child(uid)
            fetchData()
        }

        // Logout button
        val btnLogout: Button = view.findViewById(R.id.btnLogOut)
        btnLogout.setOnClickListener {
            // Sign out the user
            auth.signOut()

            // Redirect to the login page
            val intent = Intent(requireContext(), LoginPage::class.java)
            startActivity(intent)
            requireActivity().finish() // Optional: Close the current activity
        }

        return view
    }

    private fun createUri(): Uri{
        val imageFile = File(requireActivity().application.filesDir,"camera_photo.jpg")
        return FileProvider.getUriForFile(
            requireContext().applicationContext,
            "za.co.varsitycollege.st10215473.rvmtimesolutions.fileprovider",
            imageFile
        )
    }

    private fun removeProfilePicture() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        firebaseRef = FirebaseDatabase.getInstance().getReference("Profile")

        if (uid != null) {
            firebaseRef.child(uid).child("profilePic").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl = snapshot.getValue(String::class.java)
                    if (imageUrl != null && imageUrl.isNotEmpty()) {
                        val photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                        photoRef.delete().addOnSuccessListener {
                            firebaseRef.child(uid).child("profilePic").removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    profilePicture.setImageResource(R.drawable.profile_placeholder)
                                    Toast.makeText(requireContext(), "Profile picture removed successfully.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(requireContext(), "Failed to remove profile picture from database.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to delete profile picture from storage.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "No profile picture found.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to access database.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun registerPictureLauncher(){
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()){isSuccess ->
            try {
                if(isSuccess){

                    addedAnImage = true
                    profilePicture.setImageURI(null)
                    profilePicture.setImageURI(uri)

                    uploadImageToFirebase()
                }
            }catch(e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageToFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        firebaseRef = FirebaseDatabase.getInstance().getReference("Profile")

        if (addedAnImage == true && uid != null) {
            uri?.let {
                storageRef.child(uid).putFile(it).addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                        val imgUrl = url.toString()
                        val updates = HashMap<String, Any>()
                        updates["profilePic"] = imgUrl

                        firebaseRef.child(uid).updateChildren(updates).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "Profile picture updated successfully.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Profile picture did not dala.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkCameraPermissionAndOpen(){
        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
        else{
            takePictureLauncher.launch(uri)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takePictureLauncher.launch(uri)
            }
            else{
                Toast.makeText(context, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchData() {
        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val surname = snapshot.child("surname").getValue(String::class.java)
                    val email = snapshot.child("email").getValue(String::class.java)
                    val imageUrl = snapshot.child("profilePic").getValue(String::class.java)

                    if(imageUrl != ""){
                        Picasso.get()
                            .load(imageUrl)
                            .rotate(getImageRotation(imageUrl ?: "default_image_url"))
                            .into(profilePicture)
                    }
                    nameText.text = name
                    surnameText.text = surname
                    emailText.text = email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }
    private fun getImageRotation(imageUrl: String): Float {
        try {
            val exif = ExifInterface(imageUrl)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0f
    }
}
 