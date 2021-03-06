package com.lukasstancikas.sonarworkstest.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lukasstancikas.sonarworkstest.R
import com.lukasstancikas.sonarworkstest.bridge.UserJavascriptInterface
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModel()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    override fun onStart() {
        super.onStart()
        subscribeToViewModel()
        viewModel.dispatch(MainUIEvent.Init)
    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    private fun setupViews() {
        binding.nativeUserSubmit.setOnClickListener { onNativeButtonClick() }
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

    private fun onNewUiEffect(effect: MainUIEffect) =
        when (effect) {
            MainUIEffect.SameNativeUserMessage -> {
                Toast.makeText(this, R.string.native_user_same, Toast.LENGTH_SHORT).show()
            }
            MainUIEffect.SameWebUserMessage -> {
                Toast.makeText(this, R.string.web_user_same, Toast.LENGTH_SHORT).show()
            }
            is MainUIEffect.EvaluateJavascript -> {
                binding.webView.evaluateJavascript(effect.evaluation, null)
            }
            is MainUIEffect.LoadUrl -> loadUrlWithInterface(effect.url, effect.jsInterface)
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

    private fun onNativeButtonClick() {
        val user = User(
            name = binding.nativeUserName.text.toString(),
            age = binding.nativeUserAge.text.toString().toIntOrNull() ?: 0
        )
        viewModel.dispatch(MainUIEvent.NativeUserSubmitted(user))
    }

    private fun loadUrlWithInterface(url: String, jsInterface: UserJavascriptInterface) =
        with(binding) {
            webView.removeJavascriptInterface(jsInterface.nativeComponentName)
            webView.addJavascriptInterface(jsInterface, jsInterface.nativeComponentName)
            webView.loadUrl(url)
            webView.settings.javaScriptEnabled = true
        }
}