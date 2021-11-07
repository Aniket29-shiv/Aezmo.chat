package com.aniket.aezmodate.rtmtutorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.rtm.RtmMessage;

/**
 * Receives and manages messages from RTM engine.
 */
public class RtmMessagePool {
    private Map<String, List<RtmMessage>> mOfflineMessageMap = new HashMap<>();
    private Map<String, List<RtmMessage>> mHistoryMessageMap = new HashMap<>();

    void insertOfflineMessage(RtmMessage rtmMessage, String peerId) {
        boolean contains = mOfflineMessageMap.containsKey(peerId);
        List<RtmMessage> list = contains ? mOfflineMessageMap.get(peerId) : new ArrayList<>();

        if (list != null) {
            list.add(rtmMessage);
        }

        if (!contains) {
            mOfflineMessageMap.put(peerId, list);
        }
    }

    List<RtmMessage> getAllOfflineMessages(String peerId) {
        return mOfflineMessageMap.containsKey(peerId) ?
                mOfflineMessageMap.get(peerId) : new ArrayList<>();
    }

    void removeAllOfflineMessages(String peerId) {
        mOfflineMessageMap.remove(peerId);
    }

//    Message History.
    void insertHistoryMessage(RtmMessage rtmMessage, String peerId) {
        boolean contains = mHistoryMessageMap.containsKey(peerId);
        List<RtmMessage> list = contains ? mHistoryMessageMap.get(peerId) : new ArrayList<>();

        if (list != null) {
            list.add(rtmMessage);
        }

        if (!contains) {
            mHistoryMessageMap.put(peerId, list);
        }
    }

    List<RtmMessage> getAllHistoryMessages(String peerId) {
        return mHistoryMessageMap.containsKey(peerId) ?
                mHistoryMessageMap.get(peerId) : new ArrayList<>();
    }
}
