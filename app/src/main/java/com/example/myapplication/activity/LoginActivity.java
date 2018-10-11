package com.example.myapplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.utils.AppUtil;
import com.example.myapplication.utils.NetworkConnectionUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by TienTruong on 10/6/2018.
 */

public class LoginActivity extends AppCompatActivity implements AppUtil, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1126;
    public static GoogleApiClient mGoogleApiClient;
    private ImageView mButtonFacebook;
    private ImageView mbuttonGoogle;
    private TextView mStatusInternet;
    //CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private ProgressDialog mAuthProgressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LoginActivity.NetworkChangeReceiver mNetworkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        initViews();
        setOnListener();
        firebaseAuthListener();
        processDialogAuth();
        showWarnningIternet();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // hideSoftKeyBoard();
        //discoveryKeyHarsh();

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mAuth.addAuthStateListener(mAuthListener);
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mNetworkChangeReceiver = new LoginActivity.NetworkChangeReceiver();
            this.registerReceiver(mNetworkChangeReceiver, filter);
        } catch (Exception e) {
            Log.w("Warn", " " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNetworkChangeReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mNetworkChangeReceiver = new LoginActivity.NetworkChangeReceiver();
            this.registerReceiver(mNetworkChangeReceiver, filter);
        } catch (Exception e) {
            Log.w("Warn", " " + e.getMessage());
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Request code: " + requestCode + ", result code: " + resultCode);

        if (requestCode == RC_SIGN_IN) {
            //Google
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        } else {
            // Facebook
            //mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void showWarnningIternet() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            mStatusInternet.setVisibility(View.GONE);
        } else {
            mStatusInternet.setVisibility(View.VISIBLE);
        }
    }

    private void processDialogAuth() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setMessage(getResources().getString(R.string.dialog_process_auth_message));
        mAuthProgressDialog.setCancelable(false);
    }

    private void firebaseAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mAuthProgressDialog.hide();
            }
        };
    }


    private void initViews() {
        mButtonFacebook = findViewById(R.id.login_button_facebook);
        mbuttonGoogle = findViewById(R.id.login_button_google);
        mStatusInternet = findViewById(R.id.no_internet_banner);
    }

    private void setOnListener() {
        mButtonFacebook.setOnClickListener(this);
        mbuttonGoogle.setOnClickListener(this);


    }


//    public void discoveryKeyHarsh() {
//        try {
//            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
//                    "com.coloralpha.shoppinglist",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
//    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
//            case R.id.login_button_facebook:
//                mCallbackManager = CallbackManager.Factory.create();
//                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
//                LoginManager.getInstance().registerCallback(mCallbackManager,
//                        new FacebookCallback<LoginResult>() {
//                            @Override
//                            public void onSuccess(LoginResult loginResult) {
//                                Log.d(TAG, "facebook:onSuccess:" + loginResult);
//                                handleFacebookAccessToken(loginResult.getAccessToken());
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                Log.d(TAG, "facebook:onCancel");
//                            }
//
//                            @Override
//                            public void onError(FacebookException error) {
//                                Log.d(TAG, "facebook:onError", error);
//                            }
//                        });
//                break;
            case R.id.login_button_google:
                signIn();
                break;
            default:
                break;
        }
    }

//    private void handleFacebookAccessToken(AccessToken token) {
//        mAuthProgressDialog.show();
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "signInWithCredential:success");
//                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
//                            alert.setMessage(getResources().getString(R.string.dialog_message_login_success));
//                            alert.setPositiveButton(getResources().getString(R.string.abc_ok), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                }
//                            });
//                            alert.show();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
//                            alert.setMessage(getResources().getString(R.string.dialog_message_login_failed));
//                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //activeFragment(new LoginActivity());
//
//                                }
//                            });
//                            alert.show();
//                        }
//
//                    }
//                });
//    }

    private void signIn() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mAuthProgressDialog.show();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                            alert.setMessage(getResources().getString(R.string.dialog_message_login_success));
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                            });
                            alert.show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                            alert.setMessage(getResources().getString(R.string.dialog_message_login_failed));
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Login error", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();

                                }
                            });
                            alert.show();
                        }
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = NetworkConnectionUtil.getConnectivityStatusString(context);
            Log.d(TAG, "Status internet change:" + status);
            showWarnningIternet();
        }
    }

}
