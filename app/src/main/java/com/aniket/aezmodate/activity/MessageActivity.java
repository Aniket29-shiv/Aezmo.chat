package com.aniket.aezmodate.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aniket.aezmodate.db.ChatDatabase;
import com.aniket.aezmodate.db.DatabaseClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.aniket.aezmodate.adapter.MessageAdapter;
import com.aniket.aezmodate.model.MessageBean;
import com.aniket.aezmodate.model.MessageListBean;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;
import com.aniket.aezmodate.rtmtutorial.AGApplication;
import com.aniket.aezmodate.rtmtutorial.ChatManager;
import com.aniket.aezmodate.R;
import com.aniket.aezmodate.utils.ImageUtil;
import com.aniket.aezmodate.utils.MessageUtil;

public class MessageActivity extends Activity {
    private final String TAG = MessageActivity.class.getSimpleName();

    private TextView mTitleTextView;
    private EditText mMsgEditText;
    private ImageView mBigImage;
    private RecyclerView mRecyclerView;
    private final List<MessageBean> mMessageBeanList = new ArrayList<>();
    private MessageAdapter mMessageAdapter;

    private boolean mIsPeerToPeerMode = true;
    private String mUserId = "";
    private String mPeerId = "";
    private String mChannelName = "";
    private int mChannelMemberCount = 1;

    private ChatManager mChatManager;
    private RtmClient mRtmClient;
    private RtmChannel mRtmChannel;

    private LinearLayout revealFooterLayout, viewFooterLayout, other_msgbox, my_msgbox, replyMyMsg, replyOtherMsg;
    private MaterialCardView materialCard;
    private View blurview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getTasks();

        init();
//        destroyMsgList();

    }

    private void init() {
//        loadMsgList();
        mChatManager = AGApplication.the().getChatManager();
        mRtmClient = mChatManager.getRtmClient();
        RtmClientListener mClientListener = new MyRtmClientListener();
        mChatManager.registerListener(mClientListener);

        mRecyclerView = findViewById(R.id.message_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMessageAdapter = new MessageAdapter(MessageActivity.this, mMessageBeanList, message -> {});
        mRecyclerView.setAdapter(mMessageAdapter);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.this);
//        layoutManager.setOrientation(RecyclerView.VERTICAL);




        Intent intent = getIntent();
        mIsPeerToPeerMode = intent.getBooleanExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, true);
        mUserId = intent.getStringExtra(MessageUtil.INTENT_EXTRA_USER_ID);
        String targetName = intent.getStringExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME);

        mTitleTextView = findViewById(R.id.message_title);
        if (mIsPeerToPeerMode) {
            mPeerId = targetName;
            mTitleTextView.setText(mPeerId);

//            // load history chat records
//            MessageListBean messageListBean = MessageUtil.getExistMessageListBean(mPeerId);
//            if (messageListBean != null) {
//                mMessageBeanList.addAll(messageListBean.getMessageBeanList());
//            }
//
//            // load offline messages since last chat with this peer.
//            // Then clear cached offline messages from message pool
//            // since they are already consumed.
//            MessageListBean offlineMessageBean = new MessageListBean(mPeerId, mChatManager);
//            mMessageBeanList.addAll(offlineMessageBean.getMessageBeanList());
//            //mChatManager.removeAllOfflineMessages(mPeerId);
        } else {
            mChannelName = targetName;
            mChannelMemberCount = 1;
            mTitleTextView.setText(MessageFormat.format("{0}({1})", mChannelName, mChannelMemberCount));
            createAndJoinChannel();
        }

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);





        mMsgEditText = findViewById(R.id.message_edittiext);
        mBigImage = findViewById(R.id.big_image);


        ImageView moreButton = findViewById(R.id.moreButton);

        TextView revealShortcut = findViewById(R.id.revealShortcutButton);
        revealFooterLayout = findViewById(R.id.revealFooterLayout);
        viewFooterLayout = findViewById(R.id.viewFooterLayout);
        ImageView closeButton = findViewById(R.id.closeButton);
        ImageView closeButton2 = findViewById(R.id.closeButton2);
        MaterialButton reveal_bt = findViewById(R.id.reveal_bt);
        MaterialButton reveal_bt2 = findViewById(R.id.reveal_bt2);
        materialCard = findViewById(R.id.materialCard);

        TextView cancelButton = findViewById(R.id.cancelButton);
        blurview = findViewById(R.id.blurview);

        TextView initmReply = findViewById(R.id.initmReply);
        TextView initoReply = findViewById(R.id.initoReply);


//        Delete msg
        other_msgbox = findViewById(R.id.other_msgbox);
        my_msgbox = findViewById(R.id.my_msgbox);

//        Reply msg
        replyMyMsg = findViewById(R.id.replyMyMsg);
        replyOtherMsg = findViewById(R.id.replyOtherMsg);
        ImageButton closeMyReply = findViewById(R.id.closeMyReply);
        ImageButton closeOtherReply = findViewById(R.id.closeOtherReply);

        Context context = this;




        materialCard.setVisibility(View.GONE);
        blurview.setVisibility(View.GONE);

//        moreButton.setOnClickListener(view -> {
//            ChatProfileOptionMenuDialogFragment dialogFragment = new ChatProfileOptionMenuDialogFragment();
//            dialogFragment.show(getSupportFragmentManager(), "test");
//        });

        closeButton.setOnClickListener(view -> revealFooterLayout.setVisibility(View.GONE));
        closeButton2.setOnClickListener(view -> viewFooterLayout.setVisibility(View.GONE));

        reveal_bt.setOnClickListener(view -> {
            materialCard.setVisibility(View.VISIBLE);
            blurview.setVisibility(View.VISIBLE);
        });

        cancelButton.setOnClickListener(view -> {
            materialCard.setVisibility(View.GONE);
            blurview.setVisibility(View.GONE);

        });
        reveal_bt2.setOnClickListener(view -> {
            materialCard.setVisibility(View.GONE);
            revealFooterLayout.setVisibility(View.GONE);
            viewFooterLayout.setVisibility(View.VISIBLE);
            blurview.setVisibility(View.GONE);
        });

        // Reply/Delete popup
        initmReply.setOnClickListener(view -> {
            my_msgbox.setVisibility(View.GONE);
            blurview.setVisibility(View.GONE);
            replyMyMsg.setVisibility(View.VISIBLE);

        });
        initoReply.setOnClickListener(view -> {
            other_msgbox.setVisibility(View.GONE);
            blurview.setVisibility(View.GONE);
            replyOtherMsg.setVisibility(View.VISIBLE);
        });

        closeMyReply.setOnClickListener(view -> replyMyMsg.setVisibility(View.GONE));
        closeOtherReply.setOnClickListener(view -> replyOtherMsg.setVisibility(View.GONE));

        // Reply My Msg
        mMessageAdapter.setOnViewHolderClickedListener(() -> {
            Log.e(TAG,"LongClick in MainActivity");
            my_msgbox.setVisibility(View.VISIBLE);
            blurview.setVisibility(View.VISIBLE);
        });

        // Reply Other Msg
        mMessageAdapter.setOnViewHolderClickedListener2(() -> {
            Log.e(TAG,"LongClick in MainActivity");
            other_msgbox.setVisibility(View.VISIBLE);
            blurview.setVisibility(View.VISIBLE);
        });

    }

    public void getTasks() {

        @SuppressLint("StaticFieldLeak")
        class GetTasks extends AsyncTask<Void, Void, List<MessageBean>> {

            @Override
            protected List<MessageBean> doInBackground(Void... voids) {
                List<MessageBean> taskList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .msgDao()
                        .getAll();
                Log.e(TAG, taskList.toString() + "taskList");
                return taskList;
            }

            @Override
            protected void onPostExecute(List<MessageBean> tasks) {
                super.onPostExecute(tasks);

                Log.e(TAG,"tasks "+ tasks);
                mMessageAdapter = new MessageAdapter(MessageActivity.this, tasks, message -> {});
                mRecyclerView.setAdapter(mMessageAdapter);
//
//                MessageBean messageBean = new MessageBean(mUserId, message, true);
//                mMessageBeanList.add(messageBean);
//                mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                //mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();

    }

//    public void getTasks() {
//
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        Executor executor = executorService;
//
//// To send a command:
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.gc();
//            }
//
//
//            public List<ChatDatabaseHelper> getChatDatabaseHelper(){
//                List<ChatDatabaseHelper> tasks = DatabaseClient
//                    .getInstance(getApplicationContext())
//                    .getAppDatabase()
//                    .chatDao()
//                    .getAll();
//                return tasks;
//            }
//
//            runOnUiThread(new Runnable{
//
//                Log.e(TAG,"tasks "+ tasks);
//                mMessageAdapter = new MessageAdapter(MessageActivity.this, mMessageBeanList, tasks, message -> {});
//                mMessageAdapter.notifyItemRangeChanged(tasks.size(), 1);
//                mRecyclerView.setAdapter(mMessageAdapter);
//                mRecyclerView.scrollToPosition(tasks.size() - 1);
//                showToast("tasks called📡");
//            });
//
//        });
//
//        new Thread(new Runnable() {
//            @Override
//            public void run () {
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run () {
//                        Log.e(TAG,"tasks "+ tasks);
//                        mMessageAdapter = new MessageAdapter(MessageActivity.this, mMessageBeanList, tasks, message -> {});
//                        mMessageAdapter.notifyItemRangeChanged(tasks.size(), 1);
//                        mRecyclerView.setAdapter(mMessageAdapter);
//                        mRecyclerView.scrollToPosition(tasks.size() - 1);
//                        showToast("tasks called📡");
//                        // make operation on the UI - for example
//                        // on a progress bar.
//                    }
//                });
//            }
//        }).start();
//
//
//
//
//
//
//
//        List<ChatDatabaseHelper> tasks = DatabaseClient
//                .getInstance(getApplicationContext())
//                .getAppDatabase()
//                .chatDao()
//                .getAll();
//
//        Log.e(TAG,"tasks "+ tasks);
//        mMessageAdapter = new MessageAdapter(MessageActivity.this, mMessageBeanList, tasks, message -> {});
//        mMessageAdapter.notifyItemRangeChanged(tasks.size(), 1);
//        mRecyclerView.setAdapter(mMessageAdapter);
//        mRecyclerView.scrollToPosition(tasks.size() - 1);
//        showToast("tasks called📡");
//
//
////        class GetTasks extends AsyncTask<Void, Void, List<ChatDatabaseHelper>> {
////
////            @Override
////            protected List<ChatDatabaseHelper> doInBackground(Void... voids) {
////                List<ChatDatabaseHelper> tasks = DatabaseClient
////                        .getInstance(getApplicationContext())
////                        .getAppDatabase()
////                        .chatDao()
////                        .getAll();
////                return tasks;
////            }
////
////            @Override
////            protected void onPostExecute(List<ChatDatabaseHelper> tasks) {
////                super.onPostExecute(tasks);
////
////                Log.e(TAG,"tasks "+ tasks);
////                mMessageAdapter = new MessageAdapter(MessageActivity.this, mMessageBeanList, tasks, message -> {});
////                mMessageAdapter.notifyItemRangeChanged(tasks.size(), 1);
////                mRecyclerView.setAdapter(mMessageAdapter);
////                mRecyclerView.scrollToPosition(tasks.size() - 1);
////                showToast("tasks called📡");
//////
//////                MessageBean messageBean = new MessageBean(mUserId, message, true);
//////                mMessageBeanList.add(messageBean);
//////                mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
////                //mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
////
////            }
////        }
////
////        GetTasks gt = new GetTasks();
////        gt.execute();
//
//    }

    public void loadMsgList(){
        ChatDatabase db = ChatDatabase.getDbInstance(this.getApplicationContext());
        List<MessageBean> msgList = db.msgDao().getAll();
        mMessageAdapter.setMsgList(msgList);
    }

    private void destroyMsgList(){
        DatabaseClient
                .getInstance(getApplicationContext())
                .getAppDatabase()
                .msgDao()
                .nukeTable();
    }

    private void saveTask(String userId, String peerId, RtmMessage chatmsg) {


//        if (sTask.isEmpty()) {
//            editTextTask.setError("Task required");
//            editTextTask.requestFocus();
//            return;
//        }
//
//        if (sDesc.isEmpty()) {
//            editTextDesc.setError("Desc required");
//            editTextDesc.requestFocus();
//            return;
//        }
//
//        if (sFinishBy.isEmpty()) {
//            editTextFinishBy.setError("Finish by required");
//            editTextFinishBy.requestFocus();
//            return;
//        }


        @SuppressLint("StaticFieldLeak")
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a message
                MessageBean msg = new MessageBean();
                msg.setUserId(userId);
                msg.setPeerId(peerId);
                msg.setMessage(chatmsg.getText());


                //adding to database
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .msgDao()
                        .insert(msg);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                finish();
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageUtil.addMessageListBeanList(new MessageListBean(mPeerId, mMessageBeanList));

//        if (mIsPeerToPeerMode) {
//            MessageUtil.addMessageListBeanList(new MessageListBean(mPeerId, mMessageBeanList));
//        } else {
//            leaveAndReleaseChannel();
//        }
//        mChatManager.unregisterListener(mClientListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                final String file = resultUri.getPath();
                ImageUtil.uploadImage(this, mRtmClient, file, new ResultCallback<RtmImageMessage>() {
                    @Override
                    public void onSuccess(final RtmImageMessage rtmImageMessage) {
                        runOnUiThread(() -> {
                            MessageBean messageBean = new MessageBean(rtmImageMessage.getText(), mUserId, mPeerId);
                            messageBean.setCacheFile(file);
                            mMessageBeanList.add(messageBean);
                            mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                            mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);

                            if (mIsPeerToPeerMode) {
                                sendPeerMessage(rtmImageMessage);
                            } else {
                                sendChannelMessage(rtmImageMessage);
                            }
                        });
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {

                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.getError().printStackTrace();
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selection_chat_btn:
                String msg = mMsgEditText.getText().toString();
                if (!msg.equals("")) {
                    RtmMessage message = mRtmClient.createMessage();
                    message.setText(msg);
                    Log.e(TAG,"Message ''"+ message.getText() + "'' from >" + mUserId + "< to *" + mPeerId+"*");
                    saveTask(mUserId, mPeerId, message);

                    MessageBean messageBean = new MessageBean(message.getText(), mUserId, mPeerId);
                    mMessageBeanList.add(messageBean);
                    mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                    mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);

                    if (mIsPeerToPeerMode) {
                        sendPeerMessage(message);
                    } else {
                        sendChannelMessage(message);
                    }
                }
                mMsgEditText.setText("");
                break;
//            case R.id.selection_img_btn:
//                CropImage.activity().start(this);
//                break;
            case R.id.big_image:
                mBigImage.setVisibility(View.GONE);
                break;
        }
    }




    public void onClickFinish(View v) {
        finish();
    }

    /**
     * API CALL: send message to peer
     */
    private void sendPeerMessage(final RtmMessage message) {
        mRtmClient.sendMessageToPeer(mPeerId, message, mChatManager.getSendMessageOptions(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // do nothing
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                // refer to RtmStatusCode.PeerMessageState for the message state
                final int errorCode = errorInfo.getErrorCode();
                runOnUiThread(() -> {
                    switch (errorCode) {
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_TIMEOUT:
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_FAILURE:
                            showToast(getString(R.string.send_msg_failed));
                            break;
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_PEER_UNREACHABLE:
                            showToast(getString(R.string.peer_offline));
                            break;
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_CACHED_BY_SERVER:
                            showToast(getString(R.string.message_cached));
                            break;
                    }
                });
            }
        });
        Log.e(TAG,"sendPeerMessage "+ mPeerId + " message " + message.getText() + " mUserId " + mUserId);
    }

    /**
     * API CALL: create and join channel
     */
    private void createAndJoinChannel() {
        // step 1: create a channel instance
        mRtmChannel = mRtmClient.createChannel(mChannelName, new MyChannelListener());
        if (mRtmChannel == null) {
            showToast(getString(R.string.join_channel_failed));
            finish();
            return;
        }

        Log.e("channel", mRtmChannel + "");

        // step 2: join the channel
        mRtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.i(TAG, "join channel success");
                getChannelMemberList();
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "join channel failed");
                runOnUiThread(() -> {
                    showToast(getString(R.string.join_channel_failed));
                    finish();
                });
            }
        });
    }

    /**
     * API CALL: get channel member list
     */
    private void getChannelMemberList() {
        mRtmChannel.getMembers(new ResultCallback<List<RtmChannelMember>>() {
            @Override
            public void onSuccess(final List<RtmChannelMember> responseInfo) {
                runOnUiThread(() -> {
                    mChannelMemberCount = responseInfo.size();
                    refreshChannelTitle();
                });
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "failed to get channel members, err: " + errorInfo.getErrorCode());
            }
        });
    }

    /**
     * API CALL: send message to a channel
     */
    private void sendChannelMessage(RtmMessage message) {
        mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                // refer to RtmStatusCode.ChannelMessageState for the message state
                final int errorCode = errorInfo.getErrorCode();
                runOnUiThread(() -> {
                    switch (errorCode) {
                        case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_TIMEOUT:
                        case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_FAILURE:
                            showToast(getString(R.string.send_msg_failed));
                            break;
                    }
                });
            }
        });
    }

    /**
     * API CALL: leave and release channel
     */
    private void leaveAndReleaseChannel() {
        if (mRtmChannel != null) {
            mRtmChannel.leave(null);
            mRtmChannel.release();
            mRtmChannel = null;
        }
    }

    /**
     * API CALLBACK: rtm event listener
     */
    class MyRtmClientListener implements RtmClientListener {

        @Override
        public void onConnectionStateChanged(final int state, int reason) {
            runOnUiThread(() -> {
                switch (state) {
                    case RtmStatusCode.ConnectionState.CONNECTION_STATE_RECONNECTING:
                        showToast(getString(R.string.reconnecting));
                        break;
                    case RtmStatusCode.ConnectionState.CONNECTION_STATE_ABORTED:
                        showToast(getString(R.string.account_offline));
                        setResult(MessageUtil.ACTIVITY_RESULT_CONN_ABORTED);
                        finish();
                        break;
                }
            });
        }

        @Override
        public void onMessageReceived(final RtmMessage message, final String peerId) {
            runOnUiThread(() -> {
                if (peerId.equals(mPeerId)) {
                    MessageBean messageBean = new MessageBean(message.getText(), mUserId, peerId);
                    messageBean.setBackground(getMessageColor(peerId));
                    mMessageBeanList.add(messageBean);
                    mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                    mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
                } else {
                    MessageUtil.addMessageBean(message, mUserId, peerId);
                }
            });
        }

        @Override
        public void onImageMessageReceivedFromPeer(final RtmImageMessage rtmImageMessage, final String peerId) {
            runOnUiThread(() -> {
                if (peerId.equals(mPeerId)) {
                    MessageBean messageBean = new MessageBean(rtmImageMessage.getText(), mUserId, peerId);
                    messageBean.setBackground(getMessageColor(peerId));
                    mMessageBeanList.add(messageBean);
                    mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                    mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
                } else {
                    MessageUtil.addMessageBean(rtmImageMessage, mUserId, peerId);
                }
            });
        }

        @Override
        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {

        }

        @Override
        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onTokenExpired() {

        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {

        }
    }

    /**
     * API CALLBACK: rtm channel event listener
     */
    class MyChannelListener implements RtmChannelListener {
        @Override
        public void onMemberCountUpdated(int i) {

        }

        @Override
        public void onAttributesUpdated(List<RtmChannelAttribute> list) {

        }

        @Override
        public void onMessageReceived(final RtmMessage message, final RtmChannelMember fromMember) {
            runOnUiThread(() -> {
                String account = fromMember.getUserId();
                Log.i(TAG, "onMessageReceived account = " + account + " msg = " + message);
                MessageBean messageBean = new MessageBean(message.getText(), mUserId, mPeerId);
                messageBean.setBackground(getMessageColor(account));
                mMessageBeanList.add(messageBean);
                mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
            });
        }

        @Override
        public void onImageMessageReceived(final RtmImageMessage rtmImageMessage, final RtmChannelMember rtmChannelMember) {
            runOnUiThread(() -> {
                String account = rtmChannelMember.getUserId();
                Log.i(TAG, "onMessageReceived account = " + account + " msg = " + rtmImageMessage);
                MessageBean messageBean = new MessageBean(rtmImageMessage.getText(), mUserId, mPeerId);
                messageBean.setBackground(getMessageColor(account));
                mMessageBeanList.add(messageBean);
                mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
            });
        }

        @Override
        public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

        }

        @Override
        public void onMemberJoined(RtmChannelMember member) {
            runOnUiThread(() -> {
                mChannelMemberCount++;
                refreshChannelTitle();
            });
        }

        @Override
        public void onMemberLeft(RtmChannelMember member) {
            runOnUiThread(() -> {
                mChannelMemberCount--;
                refreshChannelTitle();
            });
        }
    }

    private int getMessageColor(String account) {
        for (int i = 0; i < mMessageBeanList.size(); i++) {
            if (account.equals(mMessageBeanList.get(i).getAccount())) {
                return mMessageBeanList.get(i).getBackground();
            }
        }
        return MessageUtil.COLOR_ARRAY[MessageUtil.RANDOM.nextInt(MessageUtil.COLOR_ARRAY.length)];
    }

    private void refreshChannelTitle() {
        String titleFormat = getString(R.string.channel_title);
        String title = String.format(titleFormat, mChannelName, mChannelMemberCount);
        mTitleTextView.setText(title);
    }

    private void showToast(final String text) {
        runOnUiThread(() -> Toast.makeText(MessageActivity.this, text, Toast.LENGTH_SHORT).show());
    }
}
