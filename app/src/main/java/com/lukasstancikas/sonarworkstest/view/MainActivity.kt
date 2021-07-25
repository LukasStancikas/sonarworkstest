package com.lukasstancikas.sonarworkstest.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lukasstancikas.sonarworkstest.R
import com.lukasstancikas.sonarworkstest.bridge.WebBridge
import com.lukasstancikas.sonarworkstest.databinding.ActivityMainBinding
import com.lukasstancikas.sonarworkstest.extensions.asDriver
import com.lukasstancikas.sonarworkstest.model.User
import com.lukasstancikas.sonarworkstest.view.effect.MainUIEffect
import com.lukasstancikas.sonarworkstest.view.event.MainUIEvent
import com.lukasstancikas.sonarworkstest.view.state.MainUIState
import com.lukasstancikas.sonarworkstest.viewmodel.MainViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val webBridge: WebBridge by inject()
    private val viewModel: MainViewModel by viewModel()
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
        subscribeToWebBridge()
        subscribeToViewModel()
    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    private fun setupViews() {
        binding.nativeUserSubmit.setOnClickListener { onNativeButtonClick() }
        binding.webView.addJavascriptInterface(webBridge, webBridge.nativeComponentName)
    }

    private fun subscribeToViewModel() {
        viewModel.uiStates
            .asDriver()
            .subscribeBy(
                onNext = ::onNewUiState,
                onError = Timber::e
            )
            .addTo(compositeDisposable)
        viewModel.uiEffects
            .asDriver()
            .subscribeBy(
                onNext = ::onNewUiEffect,
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    private fun onNewUiEffect(effect: MainUIEffect) {
        val messageRes = when (effect) {
            MainUIEffect.SameNativeUserMessage -> R.string.native_user_same
            MainUIEffect.SameWebUserMessage -> R.string.web_user_same
        }
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
    }

    private fun onNewUiState(state: MainUIState) = with(binding) {
        state.currentWebUser?.let {
            webUserName.text = getString(R.string.webview_user_name, it.name)
            webUserAge.text = getString(R.string.webview_user_age, it.age.toString())
        }
        state.currentNativeUser?.let {
            nativeUserName.setText(it.name)
            nativeUserAge.setText(it.age.toString())
        }
        nativeUserCount.text = getString(R.string.native_user_count, state.nativeUserSubmitCount)
        webUserCount.text = getString(R.string.webview_user_count, state.webUserSubmitCount)
    }

    private fun subscribeToWebBridge() {
        webBridge.getWebUserStream()
            .asDriver()
            .subscribeBy(
                onNext = { viewModel.dispatch(MainUIEvent.WebUserReceived(it)) },
                onError = Timber::e
            )
            .addTo(compositeDisposable)
    }

    private fun onNativeButtonClick() {
        val user = User(
            name = binding.nativeUserName.text.toString(),
            age = binding.nativeUserAge.text.toString().toIntOrNull() ?: 0
        )
        viewModel.dispatch(MainUIEvent.NativeUserSubmitted(user))
        binding.webView.evaluateJavascript(webBridge.getSubmitNativeUserEvaluation(user), null)
    }

    private fun loadHtmlAsset() {
        binding.webView.loadUrl(webBridge.htmlFilePath);
        binding.webView.settings.javaScriptEnabled = true
    }
}