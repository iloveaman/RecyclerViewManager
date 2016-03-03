package space.sye.z.library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import space.sye.z.library.listener.LoadMoreRecyclerListener;
import space.sye.z.library.listener.OnBothRefreshListener;
import space.sye.z.library.listener.OnLoadMoreListener;
import space.sye.z.library.listener.OnPullDownListener;
import space.sye.z.library.manager.RecyclerMode;
import space.sye.z.library.widget.RefreshHeader;

/**
 * Created by Syehunter on 2015/11/22.
 */
public class RefreshRecyclerView extends PtrFrameLayout {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private PtrFrameLayout.LayoutParams params;
    private LoadMoreRecyclerListener mOnScrollListener;
    private RecyclerMode mode;
    private RefreshHeader mHeaderView;
    private float mDownY;

    public RefreshRecyclerView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        mRecyclerView = new RecyclerView(mContext);
        params = new PtrFrameLayout.LayoutParams(
                PtrFrameLayout.LayoutParams.MATCH_PARENT, PtrFrameLayout.LayoutParams.MATCH_PARENT);
        mRecyclerView.setLayoutParams(params);
        addView(mRecyclerView);

        setResistance(1.7f);
        setRatioOfHeaderHeightToRefresh(1.2f);
        setDurationToClose(200);
        setDurationToCloseHeader(1000);
        setKeepHeaderWhenRefresh(true);
        setPullToRefresh(false);
        //ViewPager滑动冲突
        disableWhenHorizontalMove(true);

        mHeaderView = new RefreshHeader(mContext);
        setHeaderView(mHeaderView);

        addPtrUIHandler(mHeaderView);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (null == adapter) {
            throw new NullPointerException("adapter cannot be null");
        }
        mRecyclerView.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        if (null == itemAnimator) {
            return;
        }
        mRecyclerView.setItemAnimator(itemAnimator);
    }

    public void setMode(RecyclerMode mode) {
        this.mode = mode;
        if (RecyclerMode.NONE == mode || RecyclerMode.BOTTOM == mode) {

            setEnabled(false);
        } else {
            setEnabled(true);
        }

        if (null != mOnScrollListener) {
            mOnScrollListener.setMode(mode);
        }
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        if (null == listener) {
            return;
        }
        if (listener instanceof LoadMoreRecyclerListener) {
            mOnScrollListener = (LoadMoreRecyclerListener) listener;
            mRecyclerView.addOnScrollListener(mOnScrollListener);
        } else {
            mRecyclerView.addOnScrollListener(listener);
        }
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        if (null == decor) {
            return;
        }
        mRecyclerView.addItemDecoration(decor);
    }

    public void setOnBothRefreshListener(final OnBothRefreshListener listener) {
        if (RecyclerMode.NONE == mode || null == listener) {
            return;
        }

        if (RecyclerMode.BOTH == mode || RecyclerMode.TOP == mode) {
            //当前允许下拉刷新

            setPtrHandler(new PtrHandler() {
                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    listener.onPullDown();
                }
            });
        }

        if (RecyclerMode.BOTH == mode || RecyclerMode.BOTTOM == mode) {
            if (null != mOnScrollListener) {
                mOnScrollListener.setOnBothRefreshListener(listener);
            }
        }
    }

    public void setOnPullDownListener(final OnPullDownListener listener) {
        if (RecyclerMode.NONE == mode || null == listener) {
            return;
        }

        if (RecyclerMode.BOTH == mode || RecyclerMode.TOP == mode) {
            //当前允许下拉刷新
            setPtrHandler(new PtrHandler() {

                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    listener.onPullDown();
                }
            });
        }
    }

    public void setOnLoadMoreListener(final OnLoadMoreListener listener) {
        if (RecyclerMode.NONE == mode || null == listener) {
            return;
        }

        if (RecyclerMode.BOTH == mode || RecyclerMode.BOTTOM == mode) {
            if (null != mOnScrollListener) {
                mOnScrollListener.setOnLoadMoreListener(listener);
            }
        }
    }

    public RecyclerView real() {
        return mRecyclerView;
    }

    public void onRefreshCompleted() {
        if (RecyclerMode.BOTH == mode || RecyclerMode.TOP == mode) {
            refreshComplete();
        }
        if (RecyclerMode.BOTH == mode || RecyclerMode.BOTTOM == mode) {
            if (null != mOnScrollListener) {
                mOnScrollListener.onRefreshComplete();
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled()) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownY = e.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float currentY = e.getY();
                    if ((currentY - mDownY) > 0) {
                        //手指向下
                        mOnScrollListener.isLoadingMoreEnabled = false;
                    } else {
                        //手指向上
                        mOnScrollListener.isLoadingMoreEnabled = true;
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(e);
    }

    //---------------------------------------------------

//
//    private static final String TAG = PinnedRecyclerViewLayout.class.getSimpleName();
//
//    private View pinnedView;
//    private RefreshRecyclerView.OnRecyclerViewPinnedViewListener onRecyclerViewPinnedViewListener;
//    private int lastPosition = -1;
//
//    public void setOnRecyclerViewPinnedViewListener(RefreshRecyclerView.OnRecyclerViewPinnedViewListener onRecyclerViewPinnedViewListener) {
//        this.onRecyclerViewPinnedViewListener = onRecyclerViewPinnedViewListener;
//    }
//
//    private ABaseLinearLayoutManager layoutManager;
//
//    public void initRecyclerPinned(ABaseLinearLayoutManager layoutManager, View pinnedView) {
//        this.pinnedView = pinnedView;
//        PinnedRecyclerViewLayout pinnedRecyclerViewLayout = new PinnedRecyclerViewLayout(mContext);
//        pinnedRecyclerViewLayout.setLayoutParams(params);
//        removeView(mRecyclerView);
//        addView(pinnedRecyclerViewLayout);
//
//        this.layoutManager = layoutManager;
//        this.addView(this.pinnedView);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-1, -2);
//        this.pinnedView.setLayoutParams(lp);
//        layoutManager.getRecyclerViewScrollManager().addScrollListener(mRecyclerView, new OnRecyclerViewScrollListener() {
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            }
//
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                RefreshRecyclerView.this.refreshPinnedView();
//            }
//        });
//        pinnedView.setVisibility(GONE);
//    }
//
//    public void refreshPinnedView() {
//        if (null != this.pinnedView && null != this.layoutManager) {
//            if (VISIBLE != this.pinnedView.getVisibility()) {
//                this.pinnedView.setVisibility(VISIBLE);
//            }
//
//            int curPosition = this.layoutManager.findFirstVisibleItemPosition();
//            if (-1 != curPosition) {
//                View curItemView = this.layoutManager.findViewByPosition(curPosition);
//                if (null != curItemView) {
//                    if (curPosition != this.lastPosition) {
//                        if (null != this.onRecyclerViewPinnedViewListener) {
//                            this.onRecyclerViewPinnedViewListener.onPinnedViewRender(this, this.pinnedView, curPosition);
//                        }
//
//                        this.lastPosition = curPosition;
//                    }
//
//                    int itemHeight = curItemView.getHeight();
//                    int curTop = curItemView.getTop();
//                    int floatHeight = this.pinnedView.getHeight();
//                    int displayTop;
//                    if (curTop < floatHeight - itemHeight) {
//                        displayTop = itemHeight + curTop - floatHeight;
//                    } else {
//                        displayTop = 0;
//                    }
//
//                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.pinnedView.getLayoutParams();
//                    lp.topMargin = displayTop;
//                    this.pinnedView.setLayoutParams(lp);
//                    this.pinnedView.invalidate();
//                }
//            }
//        } else {
//            Logger.e(TAG, "Please init pinnedView and layoutManager with initRecyclerPinned method first!");
//        }
//    }
//
//    public interface OnRecyclerViewPinnedViewListener {
//        void onPinnedViewRender(RefreshRecyclerView var1, View var2, int var3);
//    }

}
