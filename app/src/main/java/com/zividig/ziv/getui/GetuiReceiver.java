package com.zividig.ziv.getui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.zividig.ziv.bean.MessageBean;
import com.zividig.ziv.fragments.MessageFragment;
import com.zividig.ziv.utils.JsonUtils;

/**
 * 个推
 * Created by Administrator on 2016-07-27.
 */
public class GetuiReceiver extends BroadcastReceiver {

    public static final String GETUI_MESSAGE_ACTION = "com.zividig.ziv.getui.message";
    public static final String GETUI_MESSAGE_KEY = "message_baen";
    public static StringBuilder payloadData = new StringBuilder();
    private MessageFragment mMessageFragment;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)){
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                Log.d("getui","taskid" + taskid + "messageid" + messageid);

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
//                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
//                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);

                    Log.d("GetuiSdkDemo", "receiver payload : " + data);
                    MessageBean messageBean = JsonUtils.deserialize(data, MessageBean.class);

                    System.out.println("---"+ messageBean.getAlarmType() + "\n"
                            + "---" + messageBean.getAlarmContent() + "\n"
                            + "---" + messageBean.getAlarmTime());

                    //发送广播
                    intent=new Intent();
                    intent.setAction(GETUI_MESSAGE_ACTION);
                    intent.putExtra(GETUI_MESSAGE_KEY, messageBean);
                    context.sendBroadcast(intent);
                }
                break;
        }
    }
}
