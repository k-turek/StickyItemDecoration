package <your_package_here>

import android.graphics.Canvas
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StickyItemDecoration(
    private val stickyView: View,
    private val adapterPosition: Int
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val layoutManager = parent.layoutManager as? LinearLayoutManager ?: return

        val topVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val topFullVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        val bottomVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        val bottomFullVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

        if (topVisibleItemPosition == RecyclerView.NO_POSITION
            || topFullVisibleItemPosition == adapterPosition
            || bottomVisibleItemPosition == RecyclerView.NO_POSITION
            || bottomFullVisibleItemPosition == adapterPosition
            || !isContentScrollable(parent)
        ) {
            return
        }

        if (canStickToTop(topVisibleItemPosition, layoutManager)) {
            fixLayoutSize(parent)
            drawStickyView(canvas, dx = parent.paddingLeft.toFloat(), dy = 0f)
        }

        if (canStickToBottom(bottomVisibleItemPosition, layoutManager, parent)) {
            fixLayoutSize(parent)
            drawStickyView(
                canvas,
                dx = parent.paddingLeft.toFloat(),
                dy = (parent.bottom - parent.top - stickyView.measuredHeight).toFloat()
            )
        }
    }

    private fun isContentScrollable(recyclerView: RecyclerView): Boolean {
        return recyclerView.canScrollVertically(DIRECTION_UP)
                || recyclerView.canScrollVertically(DIRECTION_DOWN)
    }

    private fun canStickToTop(
        topVisibleItemPosition: Int,
        layoutManager: LinearLayoutManager
    ): Boolean {
        return adapterPosition < topVisibleItemPosition
                || (adapterPosition == topVisibleItemPosition
                && isTopOffsetDisappeared(layoutManager))
    }

    private fun isTopOffsetDisappeared(layoutManager: LinearLayoutManager): Boolean {
        val view = layoutManager.findViewByPosition(adapterPosition) ?: return false
        val topDecorationHeight = layoutManager.getTopDecorationHeight(view)
        val decoratedTop = layoutManager.getDecoratedTop(view)
        return decoratedTop <= -topDecorationHeight
    }

    private fun canStickToBottom(
        bottomVisibleItemPosition: Int,
        layoutManager: LinearLayoutManager,
        parent: RecyclerView
    ): Boolean {
        return adapterPosition > bottomVisibleItemPosition
                || (adapterPosition == bottomVisibleItemPosition)
                && isBottomOffsetDisappeared(layoutManager, parent.top, parent.bottom)
    }

    private fun isBottomOffsetDisappeared(
        layoutManager: LinearLayoutManager,
        parentTop: Int,
        parentBottom: Int,
    ): Boolean {
        val view = layoutManager.findViewByPosition(adapterPosition) ?: return false
        val bottomDecorationHeight = layoutManager.getBottomDecorationHeight(view)
        val decoratedBottom = layoutManager.getDecoratedBottom(view)
        return decoratedBottom >= parentBottom - parentTop + bottomDecorationHeight
    }

    private fun drawStickyView(canvas: Canvas, dx: Float, dy: Float) {
        canvas.save()
        canvas.translate(dx, dy)
        stickyView.draw(canvas)
        canvas.restore()
    }

    private fun fixLayoutSize(parent: ViewGroup) {
        val widthSpec = MeasureSpec.makeMeasureSpec(parent.width, MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(parent.height, MeasureSpec.UNSPECIFIED)

        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            stickyView.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            stickyView.layoutParams.height
        )
        stickyView.measure(childWidthSpec, childHeightSpec)
        stickyView.layout(0, 0, stickyView.measuredWidth, stickyView.measuredHeight)
    }

    private companion object {
        const val DIRECTION_UP = -1
        const val DIRECTION_DOWN = 1
    }
}