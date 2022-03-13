package com.nglauber.architecture_sample.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nglauber.architecture_sample.core.ErrorEntity
import com.nglauber.architecture_sample.core_android.ui.components.AsyncData
import com.nglauber.architecture_sample.core_android.ui.components.GenericError
import com.nglauber.architecture_sample.core_android.ui.theme.BookAppTheme
import com.nglauber.architecture_sample.login.R
import com.nglauber.architecture_sample.login.viewmodel.LoginViewModel
import com.nglauber.architecture_sample.core_android.R as CoreR

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
) {
    val loginState by viewModel.loginState.collectAsState()
    AsyncData(
        resultState = loginState,
        errorContent = {
            GenericError(
                error = ErrorEntity(message = stringResource(id = R.string.msg_login_error)),
                onDismissAction = viewModel::resetLoginState,
            )
        }
    ) { state ->
        LaunchedEffect(state) {
            if (state != null) {
                onLoginSuccess()
            }
        }
        LoginScreenContent(
            onLoginClick = viewModel::login
        )
    }
}

@Composable
fun LoginScreenContent(
    onLoginClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = CoreR.string.app_name),
            style = TextStyle(fontSize = 48.sp),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(onClick = onLoginClick) {
            Text(text = stringResource(id = R.string.button_google_sign_in))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoginScreen() {
    BookAppTheme {
        LoginScreenContent(
            onLoginClick = {}
        )
    }
}