package za.co.varsitycollege.st10215473.rvmtimesolutions.Decorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CircularImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    private val path = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val radius = Math.min(w, h) / 2f
        path.reset()
        path.addCircle(w / 2f, h / 2f, radius, Path.Direction.CCW)
        path.close()
    }

    override fun onDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.clipPath(path)
        super.onDraw(canvas)
        canvas.restoreToCount(saveCount)
    }
}
