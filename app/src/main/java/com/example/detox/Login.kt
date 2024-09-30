package com.example.detox

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText = findViewById<EditText>(R.id.editTextText)
        val passwordEditText = findViewById<EditText>(R.id.editTextText2)
        val loginButton = findViewById<Button>(R.id.button)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username == "ankit" && password == "12345678") {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                // You can navigate to the next screen here
            } else {
                Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show()
            }
        }
    }
}