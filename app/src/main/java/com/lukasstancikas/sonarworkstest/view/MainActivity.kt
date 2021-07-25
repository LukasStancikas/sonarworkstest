package com.lukasstancikas.sonarworkstest.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lukasstancikas.sonarworkstest.R
import com.lukasstancikas.sonarworkstest.bridge.WebBridge
import com.lukasstancikas.sonarworkstest.databinding.ActivityMainBinding
import com.lukasstancikas.sonarworkstest.model.User
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val webBridge: WebBridge by inject()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        loadHtmlAsset()
    }

    override fun onStart() {
        super.onStart()
        subscribeToWebListener()
    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    private fun setupViews() {
        binding.nativeUserSubmit.setOnClickListener { onNativeButtonClick() }
        binding.webView.addJavascriptInterface(webBridge, webBridge.nativeComponentName)
    }

    private fun subscribeToWebListener() {
        webBridge.getWebUserStream()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = ::onWebUserSubmitted,
                onError = {}
            )
            .addTo(compositeDisposable)
    }

    private fun onNativeButtonClick() {
        val user = User(
            name = binding.nativeUserName.text.toString(),
            age = binding.nativeUserAge.text.toString().toIntOrNull() ?: 0
        )

        binding.webView.evaluateJavascript(
            webBridge.getSubmitNativeUserEvaluation(user),
            null
        )
    }

    private fun loadHtmlAsset() {
        binding.webView.loadUrl(webBridge.htmlFilePath);
        binding.webView.settings.javaScriptEnabled = true
    }

    private fun onWebUserSubmitted(user: User) {
        binding.webUserName.text = getString(R.string.webview_user_name, user.name)
        binding.webUserAge.text = getString(R.string.webview_user_age, user.age.toString())
    }
}