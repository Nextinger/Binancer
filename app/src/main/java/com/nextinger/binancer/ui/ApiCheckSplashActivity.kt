package com.nextinger.binancer.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nextinger.binancer.databinding.ActivityApiCheckSplashBinding
import com.nextinger.binancer.models.SettingsModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class ApiCheckSplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApiCheckSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApiCheckSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // check API key saved
        lifecycleScope.launch(Dispatchers.IO) {
            val apiKey = SettingsModel.apiKey.value
            val apiSecret = SettingsModel.apiSecret.value

            withContext(Dispatchers.Main) {
                if (apiKey.isEmpty() || apiSecret.isEmpty()) {
                    showApiInputLayout()
                } else {
                    startMainActivity()
                }
            }
        }
    }

    private fun showApiInputLayout() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.inputLayout.visibility = View.VISIBLE
        }, 700)

        val apiKeyEditText = binding.apiKeyTextField.editText!!
        val apiSecretEditText = binding.apiSecretTextField.editText!!
        val confirmButton = binding.confirmButton

        val apiInputTextWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                confirmButton.isEnabled = apiKeyEditText.text.isNotEmpty() && apiSecretEditText.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        apiKeyEditText.addTextChangedListener(apiInputTextWatcher)
        apiSecretEditText.addTextChangedListener(apiInputTextWatcher)

        binding.confirmButton.setOnClickListener {
            storeAPIKeys(
                apiKeyEditText.text.toString(),
                apiSecretEditText.text.toString()
            )
        }
    }

    private fun storeAPIKeys(key: String, secret: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            SettingsModel.apiKey.update(key)
            SettingsModel.apiSecret.update(secret)

            withContext(Dispatchers.Main) {
                startMainActivity()
            }
        }
    }

    /**
     * Start main activity
     */
    private fun startMainActivity() {
        val intent = Intent(this@ApiCheckSplashActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}