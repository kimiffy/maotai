package com.kimiffy.maotai;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Description:
 * Created by kimiffy on 2020/12/22.
 */
public class HelpService extends AccessibilityService {
    private static final String TAG = "HelpService";
    private String mCurrentPageName = "";
    boolean isNeedChickJDConfirm;//京东 是否需要点击提交订单，已经模拟点击后 置为false
    boolean buyClicked;
    boolean isNeedScroll = true; //苏宁是否需要自动下拉刷新 一下界面
    private Handler mHandler;
    boolean GMClicked1;//国美 商品页是否点击了 立即抢购
    boolean GMClicked2;//国美 商品页 dialog 是否点击了 立即抢购
    boolean GMConfirmClicked;//国美提交订单;
    boolean needRetry = false;//是否需要再次尝试点击立即抢购


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        if (null != accessibilityEvent) {
            CharSequence packageName = accessibilityEvent.getPackageName();
            if (App.getMyApplication().isSupportJD() && "com.jingdong.app.mall".equals(packageName.toString())) {
                JD(accessibilityEvent);
            } else if (App.getMyApplication().isSupportSN() && "com.suning.mobile.ebuy".equals(packageName.toString())) {
                SN(accessibilityEvent);
            } else if (App.getMyApplication().isSupportSN() && "com.gome.eshopnew".equals(packageName.toString())) {
                GM(accessibilityEvent);
            }
        }
    }


    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

    }

    /**
     * 京东
     *
     * @param event
     */
    private void JD(AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.d(TAG, "STATE_CHANGED");
                Log.d(TAG, "界面名：" + className);
                mCurrentPageName = className.toString();
                //商品详情页抢购页
                switch (mCurrentPageName) {
                    case "com.jd.lib.productdetail.ProductDetailActivity":
                        isNeedChickJDConfirm = true;
                        buyClicked = false;
                        checkBooking(event);
                        if (needRetry) {
                            readyToBuy(event);
                        }
                        break;
                    case "com.jingdong.app.mall.WebActivity":

                        break;
                    case "com.jingdong.common.ui.JDDialog":
                        if (buyClicked) {
                            closeDialog(event);
                        }
                        if (App.getMyApplication().isNeedBooking()) {
                            finishOrderJD(event);
                        }
                        break;
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.d(TAG, "CONTENT_CHANGED");
                if ("com.jingdong.app.mall.WebActivity".equals(mCurrentPageName)) {
                    Log.d(TAG, "web页 节点类型 " + className);
                    handJDWebActivityContent(event);
                } else if ("com.jd.lib.productdetail.ProductDetailActivity".equals(mCurrentPageName)) {
                    Log.d(TAG, "事件节点类名 " + className);
                    readyToBuy(event);
                }
                break;
        }
    }


    /**
     * 苏宁易购
     *
     * @param event
     */
    private void SN(final AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.d(TAG, "STATE_CHANGED");
                Log.d(TAG, "界面名：" + className);
                mCurrentPageName = className.toString();
                //商品详情页抢购页
                if ("com.suning.mobile.ebuy.commodity.CommodityMainActivity".equals(mCurrentPageName)) {
                    if (isNeedScroll) {
                        if (null == mHandler) {
                            mHandler = new Handler();
                        }
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isNeedScroll = false;
                                dispatchGestureScroll(200, 200, 200, 1000);
                            }
                        }, 1000);
                    }
                    checkBooking(event);
                }
                if (!"android.app.Dialog".equals(mCurrentPageName) && !"com.suning.mobile.ebuy.commodity.CommodityMainActivity".equals(mCurrentPageName)) {
                    isNeedScroll = true;
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if ("com.suning.mobile.ebuy.commodity.CommodityMainActivity".equals(mCurrentPageName)) {
                    Log.d(TAG, "CONTENT_CHANGED");
                    Log.d(TAG, "事件节点类名 " + className.toString());
                    if ("android.widget.RelativeLayout".equals(className.toString())) {
                        readyToBuy(event);
                    }
                }
                break;
        }

    }

    /**
     * 国美
     *
     * @param event
     */
    private void GM(AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.d(TAG, "STATE_CHANGED");
                Log.d(TAG, "界面名：" + className);
                mCurrentPageName = className.toString();
                //商品详情页抢购页
                switch (mCurrentPageName) {
                    case "com.gome.ecmall.product.ui.ProductDetailActivity": {
                        AccessibilityNodeInfo source = event.getSource();
                        if (null == source) {
                            return;
                        }
                        List<AccessibilityNodeInfo> qg = source.findAccessibilityNodeInfosByText("立即抢购");
                        for (AccessibilityNodeInfo accessibilityNodeInfo : qg) {
                            if (null != accessibilityNodeInfo && !TextUtils.isEmpty(accessibilityNodeInfo.getText().toString()) && accessibilityNodeInfo.getText().toString().equals("立即抢购") && !GMClicked1) {
                                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                GMClicked1 = true;
                                break;
                            }
                        }
                        break;
                    }
                    case "com.gome.ecmall.core.util.view.CustomDialog": {
                        AccessibilityNodeInfo source = event.getSource();
                        if (null == source) {
                            return;
                        }
                        List<AccessibilityNodeInfo> qg = source.findAccessibilityNodeInfosByText("立即抢购");
                        for (AccessibilityNodeInfo accessibilityNodeInfo : qg) {
                            if (null != accessibilityNodeInfo && !TextUtils.isEmpty(accessibilityNodeInfo.getText().toString()) && accessibilityNodeInfo.getText().toString().equals("立即抢购") && !GMClicked2) {
                                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                GMClicked2 = true;
                                break;
                            }
                        }
                        break;
                    }
                    case "com.gome.ecmall.shopping.orderfillordinaryfragment.ui.ShopCartOrdinaryOrderActivity": {
                        AccessibilityNodeInfo source = event.getSource();
                        if (null == source) {
                            return;
                        }
                        List<AccessibilityNodeInfo> qg = source.findAccessibilityNodeInfosByText("提交订单");
                        for (AccessibilityNodeInfo accessibilityNodeInfo : qg) {
                            if (null != accessibilityNodeInfo && !TextUtils.isEmpty(accessibilityNodeInfo.getText().toString()) && accessibilityNodeInfo.getText().toString().equals("提交订单") && !GMConfirmClicked) {
                                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                GMConfirmClicked = true;
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.d(TAG, "CONTENT_CHANGED");
                if ("com.gome.ecmall.product.ui.ProductDetailActivity".equals(mCurrentPageName)) {
                    Log.d(TAG, "事件节点类名 " + className);
                }
                break;
        }

    }


    /**
     * 京东完成预约
     *
     * @param event
     */
    private void finishOrderJD(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (null == source) {
            return;
        }
        List<AccessibilityNodeInfo> yy = source.findAccessibilityNodeInfosByText("知道啦");
        for (final AccessibilityNodeInfo accessibilityNodeInfo : yy) {
            if (null != accessibilityNodeInfo && !TextUtils.isEmpty(accessibilityNodeInfo.getText().toString()) && accessibilityNodeInfo.getText().toString().equals("知道啦")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        App.getMyApplication().setNeedBooking(false);
                    }
                }, 500);
                break;
            }
        }
    }


    /**
     * 点击立即抢购后 出现 活动太火爆的 弹窗 自动关闭
     *
     * @param event
     */
    private void closeDialog(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (null == source) {
            return;
        }
        List<AccessibilityNodeInfo> accessibilityNodeInfos = source.findAccessibilityNodeInfosByText("不等了");
        for (AccessibilityNodeInfo accessibilityNodeInfo : accessibilityNodeInfos) {
            if (null != accessibilityNodeInfo && !TextUtils.isEmpty(accessibilityNodeInfo.getText().toString()) && accessibilityNodeInfo.getText().toString().equals("不等了")) {
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                needRetry = true;
                break;
            }
        }
    }

    /**
     * 立即抢购
     *
     * @param event
     */
    private void readyToBuy(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (null == source) {
            return;
        }

        List<AccessibilityNodeInfo> qg = source.findAccessibilityNodeInfosByText("立即抢购");
        for (AccessibilityNodeInfo accessibilityNodeInfo : qg) {
            if (null != accessibilityNodeInfo && !TextUtils.isEmpty(accessibilityNodeInfo.getText().toString()) && accessibilityNodeInfo.getText().toString().equals("立即抢购") && !buyClicked) {
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                if (needRetry) {
                    needRetry = false;
                }
                buyClicked = true;
                break;
            }
        }
    }


    /**
     * 立即预约
     *
     * @param event
     */
    private void checkBooking(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (null == source) {
            return;
        }
        if (App.getMyApplication().isNeedBooking()) {
            List<AccessibilityNodeInfo> yuyue = source.findAccessibilityNodeInfosByText("立即预约");
            for (AccessibilityNodeInfo accessibilityNodeInfo : yuyue) {
                if (null != accessibilityNodeInfo && !TextUtils.isEmpty(accessibilityNodeInfo.getText().toString()) && accessibilityNodeInfo.getText().toString().equals("立即预约")) {
                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
        }
    }

    /**
     * 处理京东 webActivity
     *
     * @param event
     */
    private void handJDWebActivityContent(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (null == source) {
            return;
        }
        findConfirmButton(source);
    }


    /**
     * 遍历所有节点  找到提交订单按钮
     *
     * @param source
     */
    private void findConfirmButton(AccessibilityNodeInfo source) {
        if (!isNeedChickJDConfirm) {
            Log.d(TAG, "findConfirmButton:  return");
            return;
        }
        int childCount = source.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = source.getChild(i);
            if (null == child) {
                continue;
            }
            CharSequence text = child.getText();
            if (!TextUtils.isEmpty(text)) {
                if ("提交订单".equals(text.toString())) {
                    Rect rect = new Rect();
                    child.getBoundsInScreen(rect);
                    int i1 = rect.centerX();
                    int i2 = rect.centerY();
                    isNeedChickJDConfirm = false;
                    dispatchGestureView(i1, i2);
                    break;
                }
            }
            findConfirmButton(child);
        }
    }


    /**
     * 模拟点击屏幕
     *
     * @param x
     * @param y
     * @return 是否成功 点击了
     */
    private boolean dispatchGestureView(int x, int y) {
        boolean res = false;
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(x, y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 50L, 30L));
        GestureDescription gesture = builder.build();
        res = dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }
        }, null);
        return res;
    }


    /**
     * 模拟滑动屏幕
     *
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     */
    private void dispatchGestureScroll(int sx, int sy, int ex, int ey) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(sx, sy);
        p.lineTo(ex, ey);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 200L, 500L));
        GestureDescription gesture = builder.build();
        dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.d(TAG, "滑动: 完成");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.d(TAG, "滑动: 取消");
            }
        }, null);
    }
}
