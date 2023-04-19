package com.qboxus.gograbdriver.activitiesandfragments.accounts;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.qboxus.gograbdriver.activitiesandfragments.accounts.signInfragment.SigninByEmailPhoneF;
import com.qboxus.gograbdriver.activitiesandfragments.accounts.signupfragment.SignupPhoneF;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.RequestRegisterUserModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

public class SignInA extends AppCompatActivity implements View.OnClickListener {

    TextView tvSignUp;
    LinearLayout btnSignup;
    RelativeLayout btnLogin, btnFacebookLogin, btnGoogleLogin;
    private CallbackManager callbackManager;
    boolean isExit=false;

    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_);

        initControl();
        actionControl();
    }

    private void actionControl() {
        btnSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnFacebookLogin.setOnClickListener(this);
        btnGoogleLogin.setOnClickListener(this);
    }

    private void initControl() {
        preferences=new Preferences(SignInA.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setLocale(""+preferences.getKeyLocale());
        btnLogin =findViewById(R.id.btn_login);
        tvSignUp =findViewById(R.id.tv_signUp);
        btnSignup =findViewById(R.id.btn_signup);
        btnFacebookLogin =findViewById(R.id.btn_facebook_login);
        btnGoogleLogin =findViewById(R.id.btn_google_login);
        setUpScreenData();
    }

    private void setUpScreenData() {
        tvSignUp.setText(Html.fromHtml(getString(R.string.dont_have_account)+" "+"<font color='#000000'>" + getString(R.string.signup) + "</font>"));

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btn_signup:
            {
                showSignUpScreen();
            }
            break;
            case R.id.btn_login:
            {
                showSignInScreen();
            }
            break;
            case R.id.btn_facebook_login:
            {
                methodFacebooksignin();
            }
            break;
            case R.id.btn_google_login:
            {
                methodGoogleSignin();
            }
            break;

        }
    }

    private void showSignUpScreen() {
        SignupPhoneF f = new SignupPhoneF(false);
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.account_container, f,"SignupPhone_F").addToBackStack("SignupPhone_F").commit();
    }

    private void showSignInScreen() {
        SigninByEmailPhoneF f = new SigninByEmailPhoneF();
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.account_container, f,"SigninByEmailPhone_F").addToBackStack("SigninByEmailPhone_F").commit();
    }

    //google Implimentation
    GoogleSignInClient mGoogleSignInClient;
    public void methodGoogleSignin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        try {
            mGoogleSignInClient.signOut();
        }catch (Exception e){}

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SignInA.this);

        if (account != null) {

            String id=account.getId();
            String fname=""+account.getGivenName();
            String lname=""+account.getFamilyName();
            String email = account.getEmail();
            String image = ""+account.getPhotoUrl();
            String type = "google";

            String auth_token = ""+account.getIdToken();
            Functions.logDMsg( "signInResult:auth_token===" + auth_token);
            registerUserIntoApp(id,fname,lname,email,image,auth_token,type);



        } else {

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            resultCallbackForGoogle.launch(signInIntent);

        }

    }

    ActivityResultLauncher<Intent> resultCallbackForGoogle = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                    }
                }
            });



    String firebasetoken;
    private void registerUserIntoApp(String user_id, String firstname, String lastname, String email, String image, String token, String type) {


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    firebasetoken = task.getResult();
                });

        RequestRegisterUserModel registerModel=new RequestRegisterUserModel();

        registerModel.setSocialId(""+user_id);
        registerModel.setSocialType(""+type);
        registerModel.setFirstName(""+firstname);
        registerModel.setLastName(""+lastname);
        registerModel.setEmail(""+email);
        registerModel.setImage(""+image);
        registerModel.setAuthToken(""+token);
        registerModel.setDeviceToken(firebasetoken);

        callApiForLogin(registerModel);

    }

    private void callApiForLogin(RequestRegisterUserModel model) {

        JSONObject parameters = new JSONObject();
        try {

            parameters.put("social_id", model.getSocialId());
            parameters.put("social",""+model.getSocialType());
            parameters.put("auth_token", ""+model.getAuthToken());
            parameters.put("role", "driver");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(SignInA.this,false,false);
        ApiRequest.callApi(SignInA.this, ApisList.registerUser, parameters, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                parseLoginData(resp,model);
                Functions.logDMsg("resp : "+resp);
            }
        });
    }

    public void parseLoginData(String loginData, RequestRegisterUserModel model){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){

                parseLoginResponce(loginData);

            }else if(code.equals("201")){
                showSignUpPhoneScreen(model);
            }else{
                Functions.showToast(SignInA.this, ""+jsonObject.optString("msg"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showSignUpPhoneScreen(RequestRegisterUserModel model) {
        SignupPhoneF f = new SignupPhoneF(true);
        Bundle bundle=new Bundle();
        bundle.putSerializable("UserData",model);
        f.setArguments(bundle);
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.account_container, f,"SignupPhone_F").addToBackStack("SignupPhone_F").commit();
    }

    private void parseLoginResponce(String resp) {
        if (resp!=null){
            try {
                JSONObject respobj = new JSONObject(resp);
                if(respobj.getString("code").equals("200"))
                {

                    JSONObject msgobj = respobj.getJSONObject("msg") ;

                    JSONObject userObj = msgobj.getJSONObject("User") ;
                    JSONObject countryObj = msgobj.getJSONObject("Country") ;
                    JSONArray vehicleArray = msgobj.optJSONArray("Vehicle") ;

                    preferences.setKeyUserId(userObj.optString("id",""));
                    preferences.setKeyUserFirstName(userObj.optString("first_name",""));
                    preferences.setKeyUserLastName( userObj.optString("last_name",""));
                    preferences.setKeyUserName( userObj.optString("username",""));
                    preferences.setKeyUserEmail(userObj.optString("email",""));
                    preferences.setKeyUserPhone(userObj.optString("phone",""));
                    preferences.setKeyUserRole(userObj.optString("role",""));
                    preferences.setKeySocialId(userObj.optString("social_id",""));
                    preferences.setKeySocialType(userObj.optString("social",""));
                    preferences.setKeyUserToken(userObj.optString("auth_token",""));

                    preferences.setKeyPhoneCountryCode(countryObj.optString("phonecode",""));
                    preferences.setKeyPhoneCountryName(countryObj.optString("native",""));
                    preferences.setKeyPhoneCountryIOS(countryObj.optString("iso",""));
                    preferences.setKeyPhoneCountryId(countryObj.optString("id",""));
                    preferences.setKeyUserCountryId(countryObj.optString("id",""));
                    preferences.setKeyUserCountry(countryObj.optString("native",""));

                    preferences.setKeyCurrencyName(countryObj.optString("currency",""));
                    preferences.setKeyDOB(userObj.optString("dob",""));
                    preferences.setKeyGender(userObj.optString("gender",""));
                    preferences.setKeyUserImage(userObj.optString("image",""));
                    preferences.setKeyUserRole(userObj.optString("role",""));
                    preferences.setKeyUserActive(userObj.optString("online",""));
                    preferences.setKeyWallet(userObj.optString("wallet",""));
                    preferences.setKeyUserAuthToken(userObj.optString("token",""));
                    preferences.setKeyIsLogin(true);
                    preferences.setKeyUserPhone(preferences.getKeyUserPhone().replace(("+"+preferences.getKeyPhoneCountryCode()),""));

                    try {
                        if (vehicleArray.length()>0)
                        {
                            preferences.setKeyIsVehicleSet(true);
                            JSONObject InnerObj = vehicleArray.getJSONObject(0) ;
                            preferences.setKeyVehicleId(InnerObj.optString("ride_type_id"));
                        }
                    }
                    catch (Exception e)
                    {
                        Functions.logDMsg("Exception"+e);
                    }

                    Intent intent=new Intent(SignInA.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else
                {
                    Functions.showAlert(SignInA.this, SignInA.this.getString(R.string.login_status),""+respobj.optString("msg"));
                }


            } catch (Exception e) {
                Functions.logDMsg("Exception "+e);
            }
        }
    }


    public void methodFacebooksignin(){
        LoginManager.getInstance().logOut();
        LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile","email"));
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                Functions.logDMsg( ""+object.toString());

                                try {
                                    String firstname = object.getString("first_name");
                                    String lastname = object.getString("last_name");
                                    String email = object.getString("email");
                                    String user_id = object.getString("id");
                                    String type = "facebook";

                                    String image = "https://graph.facebook.com/"+user_id+"/picture?width=500";


                                    registerUserIntoApp(user_id,firstname,lastname,email,image,""+loginResult.getAccessToken().getToken(),type);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                Bundle parametrs = new Bundle();
                parametrs.putString("fields","first_name,last_name,email,id");
                request.setParameters(parametrs);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Functions.logDMsg( "cancel" );
                Functions.showToast(SignInA.this, getString(R.string.facebook_login_cancel));
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("resp", "error" );
                Functions.showToast(SignInA.this, getString(R.string.facebook_login_cancel));
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null)
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String id=account.getId();
                String fname=""+account.getGivenName();
                String lname=""+account.getFamilyName();
                String auth_token =  account.getIdToken();
                String email = account.getEmail();
                String image= ""+account.getPhotoUrl();
                String type= "google";

                              Functions.logDMsg( "signInResult:auth_token =" + auth_token);
                // if we do not get the picture of user then we will use default profile picture


                registerUserIntoApp(id,fname,lname,email,image,auth_token,type);
            }
        } catch (ApiException e) {
            Functions.logDMsg( "" + e.getStatusCode());
        }

    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
    }
}