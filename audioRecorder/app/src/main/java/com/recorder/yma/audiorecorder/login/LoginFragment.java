package com.recorder.yma.audiorecorder.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.recorder.yma.audiorecorder.R;
import com.recorder.yma.audiorecorder.RecordingsActivity;
import com.recorder.yma.audiorecorder.util.EspressoIdlingResource;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements LoginContract.View{
    private static final String TAG = LoginFragment.class.getSimpleName();

    @BindView(R.id.userEditText)
    EditText userText;
    @BindView(R.id.passwordEditText) EditText pswText;

    @BindView(R.id.loginButton) Button mLoginButton;

    private LoginContract.Presenter mPresenter;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this ,view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showLoginFailed() {

    }

    @Override
    public void onLoginSucceded() {
        Log.d(TAG, "onLoginSucceded:" );
        EspressoIdlingResource.decrement();
        Intent intent = new Intent(getActivity(), RecordingsActivity.class);

        startActivity(intent);
    }

    @Override
    public void setPresenter(@NonNull LoginContract.Presenter presenter) {
        Log.d(TAG, "setPresenter:" );
        mPresenter = presenter;
    }

    @OnTextChanged(R.id.userEditText)
    public void onUserTextChanged(Editable editable){
        updateLoginButton();
    }

    @OnTextChanged(R.id.passwordEditText)
    public void onPasswordTextChanged(Editable editable){
        updateLoginButton();
    }

    private void updateLoginButton()
    {
        if(!TextUtils.isEmpty(userText.getText().toString())&& !TextUtils.isEmpty(pswText.getText().toString()))
        {
            mLoginButton.setEnabled(true);
           return;
        }
        mLoginButton.setEnabled(false);

    }

    @OnClick(R.id.loginButton)
    public void loginCLicked(Button button) {

        mPresenter.login(userText.getText().toString(), pswText.getText().toString());
        EspressoIdlingResource.increment();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
