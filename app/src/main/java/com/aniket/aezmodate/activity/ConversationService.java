package com.aniket.aezmodate.activity;

import static android.content.ContentValues.TAG;
import static com.aniket.aezmodate.UserAdapter.setOnViewHolderClickedListener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aniket.aezmodate.api.ApiClient;
import com.aniket.aezmodate.api.ProfileApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.aniket.aezmodate.UserAdapter;
import com.aniket.aezmodate.model.ConversationBean;
import io.agora.rtm.RtmClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.aniket.aezmodate.rtmtutorial.AGApplication;
import com.aniket.aezmodate.rtmtutorial.ChatManager;
import com.aniket.aezmodate.R;
import com.aniket.aezmodate.utils.MessageUtil;


public class ConversationService extends Activity implements UserAdapter.OnCardInfoListener {
    private static final int CHAT_REQUEST_CODE = 1;

    private TextView mTitleTextView;
    private TextView mChatButton;
    private EditText mNameEditText;

    private boolean mIsPeerToPeerMode = true; // whether peer to peer mode or channel mode\
    private String mTargetName;
    private String mUserId;

    private ChatManager mChatManager;
    private List<ConversationBean> conversationList = new ArrayList<ConversationBean>();
    private RtmClient mRtmClient;
    private ImageView mBigImage;
    private ConversationBean mUserProfile;

    TextView chatTabButton, matchesTabButton;
    RecyclerView recyclerView;
    UserAdapter adapter;

    FloatingActionButton floatingActionButton;
    AppCompatCheckBox mOfflineMsgCheck;

    private ProfileApi profileApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        mChatManager = AGApplication.the().getChatManager();
        mNameEditText = findViewById(R.id.selection_name);
        mChatButton = findViewById(R.id.selection_chat_btn);
        AppCompatCheckBox mOfflineMsgCheck = findViewById(R.id.offline_msg_check);
        mOfflineMsgCheck.setChecked(mChatManager.isOfflineMessageEnabled());
        mOfflineMsgCheck.setOnCheckedChangeListener((buttonView, isChecked) -> mChatManager.enableOfflineMessage(isChecked));

        mNameEditText.setVisibility(View.GONE);
        mChatButton.setVisibility(View.GONE);
        mOfflineMsgCheck.setVisibility(View.GONE);


        initUIAndData();
    }

    private void getProfileData() {
        profileApi= ApiClient.getApiClient().create(ProfileApi.class);
        Call<ArrayList<ConversationBean>> call=profileApi.getProfileData();
        call.enqueue(new Callback<ArrayList<ConversationBean>>() {
            @Override
            public void onResponse(Call<ArrayList<ConversationBean>> call, Response<ArrayList<ConversationBean>> response) {
                conversationList =response.body();
                adapter=new UserAdapter(conversationList,ConversationService.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(ConversationService.this, LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(adapter);
                Log.e(TAG, "getProfileData recyclerview");
            }

            @Override
            public void onFailure(Call<ArrayList<ConversationBean>> call, Throwable t) {
            }
        });


    }


    public void initUIAndData() {
        Intent intent = getIntent();
        mUserId = intent.getStringExtra(MessageUtil.INTENT_EXTRA_USER_ID);
        mTitleTextView = findViewById(R.id.selection_title);

        floatingActionButton = findViewById(R.id.add_user);
        RadioGroup modeGroup = findViewById(R.id.mode_radio_group);
        modeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.peer_radio_button:
                    mIsPeerToPeerMode = true;
                    mTitleTextView.setText(getString(R.string.title_peer_msg));
                    mChatButton.setText(getString(R.string.btn_chat));
                    mNameEditText.setHint(getString(R.string.hint_friend));
                    break;
                case R.id.selection_tab_channel:
                    mIsPeerToPeerMode = false;
                    mTitleTextView.setText(getString(R.string.title_channel_message));
                    mChatButton.setText(getString(R.string.btn_join));
                    mNameEditText.setHint(getString(R.string.hint_channel));
                    break;
            }
        });
        RadioButton peerMode = findViewById(R.id.peer_radio_button);
        peerMode.setChecked(true);

        // New User list
        chatTabButton = findViewById(R.id.chatsTabButton);
        matchesTabButton = findViewById(R.id.matchesTabButton);
        recyclerView = findViewById(R.id.chatListRecyclerView);

        final Typeface tf = ResourcesCompat.getFont(this, R.font.sourcesanspro_semibold);
        chatTabButton.setTypeface(tf);
        matchesTabButton.setTypeface(tf);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.ic_divider_recyclerview)));
        recyclerView.addItemDecoration(itemDecorator);
        getProfileData();
//        List<ConversationBean> list = new ArrayList<>();
//        list.add(new ConversationBean(1,1,"Ani", "https://www.rd.com/wp-content/uploads/2017/09/01-shutterstock_476340928-Irina-Bg.jpg", "Insta pe follow kiya??", false, "7:49 am", "1"));
//        list.add(new ConversationBean(2,2,"Mohit", "https://www.rd.com/wp-content/uploads/2017/09/01-shutterstock_476340928-Irina-Bg.jpg", "Hii...", true, "7:49 am", "0"));
//        list.add(new ConversationBean(3,3,"Sagar", "https://www.rd.com/wp-content/uploads/2017/09/01-shutterstock_476340928-Irina-Bg.jpg", "Kaise hue??", true, "7:49 am", "0"));
//        list.add(new ConversationBean(4,4,"Shiv", "https://www.rd.com/wp-content/uploads/2017/09/01-shutterstock_476340928-Irina-Bg.jpg", "Let’s meet tom...", true, "7:49 am", "0"));
//        list.add(new ConversationBean(5,5,"Natasha", "https://www.rd.com/wp-content/uploads/2017/09/01-shutterstock_476340928-Irina-Bg.jpg", "Have you heard this song?", true, "7:49 am", "0"));

//        adapter = new UserAdapter(list, this);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        recyclerView.setAdapter(adapter);
//        Log.e(TAG, "recyclerview updated🗺");

        //Floating button
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameEditText.setVisibility(View.VISIBLE);
                mChatButton.setVisibility(View.VISIBLE);
                //mOfflineMsgCheck.setVisibility(View.VISIBLE);
            }


        });

        mChatButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mTargetName = mNameEditText.getText().toString();
                addNewProfile(mTargetName);
            }
        });
        // Reply My Msg
        setOnViewHolderClickedListener(new UserAdapter.OnViewHolderClickedListener() {
            @Override
            public void onViewHolderClicked() {
                Log.e(TAG,"LongClick in MainActivity");

            }
        });

        chatTabButton.setOnClickListener(view -> {
            animateTabs(matchesTabButton, chatTabButton);
        });

        matchesTabButton.setOnClickListener(view -> {
            animateTabs(chatTabButton, matchesTabButton);
        });


    }

    public void animateTabs(TextView from, TextView to) {
        from.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        to.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_tab_highlighter);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(from, "textSize", 30, 18);
        animator1.setDuration(500);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(to, "textSize", 18, 30);
        animator2.setDuration(500);
        animator1.start();
        animator2.start();
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                from.setTextColor(ContextCompat.getColor(ConversationService.this, R.color.tabUnSelected));
            }
        });
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                to.setTextColor(ContextCompat.getColor(ConversationService.this, R.color.tabSelected));
            }
        });
    }

    public void addNewProfile(String newProfileName){
        ConversationBean mNewProfile = new ConversationBean(5,5,"","","",false,"","");
        mNewProfile.setProfileName(newProfileName);
        mNewProfile.setLastMessage("Hey there!");
        mNewProfile.setLastMessageSeen(false);
        mNewProfile.setLastMessageTime("01:00 pm");
        mNewProfile.setProfileImage("https://selfie2anime.com/img/carousel/2.jpg");
        mNewProfile.setUnseenMessageCount("5");
        conversationList.add(mNewProfile);
        adapter=new UserAdapter(conversationList,ConversationService.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ConversationService.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }
    public void onClickChat(View v) {
//        MyListData[] myListData = new MyListData[]{};
        mTargetName = mNameEditText.getText().toString();
        if (mTargetName.equals("")) {
            showToast(getString(mIsPeerToPeerMode ? R.string.account_empty : R.string.channel_name_empty));
        } else if (mTargetName.length() >= MessageUtil.MAX_INPUT_NAME_LENGTH) {
            showToast(getString(mIsPeerToPeerMode ? R.string.account_too_long : R.string.channel_name_too_long));
        } else if (mTargetName.startsWith(" ")) {
            showToast(getString(mIsPeerToPeerMode ? R.string.account_starts_with_space : R.string.channel_name_starts_with_space));
        } else if (mTargetName.equals("null")) {
            showToast(getString(mIsPeerToPeerMode ? R.string.account_literal_null : R.string.channel_name_literal_null));
        } else if (mIsPeerToPeerMode && mTargetName.equals(mUserId)) {
            showToast(getString(R.string.account_cannot_be_yourself));
        } else {
            mChatButton.setEnabled(false);
            jumpToMessageActivity();
        }
    }

    private void jumpToMessageActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, mIsPeerToPeerMode);
        intent.putExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME, mTargetName);
        intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, mUserId);
        startActivityForResult(intent, CHAT_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatButton.setEnabled(true);
    }

    public void onClickFinish(View v) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHAT_REQUEST_CODE) {
            if (resultCode == MessageUtil.ACTIVITY_RESULT_CONN_ABORTED) {
                finish();
            }
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    

    @Override
    public void OnCardInfoListener(Intent intent) {
        Log.e("Data >>>>", intent.getStringExtra("targetname"));
        mTargetName = intent.getStringExtra("targetname");
        jumpToMessageActivity();
    }


}
