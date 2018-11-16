package cn.leo.banner;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author : Jarry Leo
 * @date : 2018/11/15 15:36
 */
public class RhombusLayoutManager extends RecyclerView.LayoutManager {
    /**
     * 现在第一个可见的view的在所有条目中的索引
     */
    private int mFirstVisiblePosition;
    /**
     * 现在最后一个可见的view的在所有条目中的索引
     */
    private int mLastVisiblePosition;
    /**
     * 滑动距离，notifyDataSetChanged时，保存位置
     */
    private int mHorizontalOffset;


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 1 在RecyclerView初始化时，会被调用两次。
     * 2 在调用adapter.notifyDataSetChanged()时，会被调用。
     * 3 在调用setAdapter替换Adapter时,会被调用。
     * 4 在RecyclerView执行动画时，它也会被调用。
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //没有Item，界面空着吧
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        //state.isPreLayout()是支持动画的
        if (getChildCount() == 0 && state.isPreLayout()) {
            return;
        }
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍,所以要把第一遍填充的轻回收
        detachAndScrapAttachedViews(recycler);
        //最后一个可见条目
        mLastVisiblePosition = getItemCount();

        //初始化时调用 填充childView
        fill(recycler, state, mHorizontalOffset);
    }


    /**
     * 返回值会被RecyclerView用来判断是否达到边界，
     * 如果返回值！=传入的dx，
     * 则会有一个边缘的发光效果，表示到达了边界。
     * 而且返回值还会被RecyclerView用于计算fling效果。
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //位移0、没有子View 当然不移动
        if (dx == 0 || getChildCount() == 0) {
            return 0;
        }
        //实际滑动的距离， 可能会在边界处被修复
        int realOffset = dx;
        //边界修复代码
        if (mHorizontalOffset + realOffset < 0) {
            //上边界
            realOffset = -mHorizontalOffset;
        } else if (realOffset > 0) {
            //下边界
            //利用最后一个子View比较修正
            View lastChild = getChildAt(getChildCount() - 1);
            if (getPosition(lastChild) == getItemCount() - 1) {
                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);
                if (gap > 0) {
                    realOffset = -gap;
                } else if (gap == 0) {
                    realOffset = 0;
                } else {
                    realOffset = Math.min(realOffset, -gap);
                }
            }
        }
        //先填充，再位移。
        realOffset = fill(recycler, state, realOffset);
        //累加实际滑动距离
        mHorizontalOffset += realOffset;
        //移动所有展示的子条目 , 但是不会自动回收或者出现新的条目，要自己处理
        offsetChildrenHorizontal(-realOffset);

        return realOffset;
    }

    /**
     * 填充新的view，回收越界的view
     *
     * @param dx 真实距离
     * @return 实际距离
     */
    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dx) {
        int leftOffset = getPaddingLeft();
        //先回收越界view
        if (getChildCount() > 0) {
            //滑动时进来的
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                if (dx > 0) {
                    //需要回收当前屏幕，上越界的View
                    if (getDecoratedBottom(child) - dx < leftOffset) {
                        removeAndRecycleView(child, recycler);
                        mFirstVisiblePosition++;
                    }
                } else if (dx < 0) {
                    //回收当前屏幕，下越界的View
                    if (getDecoratedTop(child) - dx > getHeight() - getPaddingBottom()) {
                        removeAndRecycleView(child, recycler);
                        mLastVisiblePosition--;
                    }
                }
            }
            //detachAndScrapAttachedViews(recycler);
        }

        //todo 添加新的view

        //todo 修正dx

        return 0;
    }

    /**
     * @return 是否允许纵向滑动
     */
    @Override
    public boolean canScrollVertically() {
        return false;
    }

    /**
     * @return 是否允许横向滑动
     */
    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view 子view
     * @return px
     */
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view 子view
     * @return px
     */
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    /**
     * 获取RecyclerView纵向可用空间
     *
     * @return px
     */
    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * 获取RecyclerView横向可用空间
     *
     * @return px
     */
    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

}