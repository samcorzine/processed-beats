package processedbeats

import processing.core.PApplet
import java.time.LocalDateTime
import java.time.Duration
import kotlin.math.*


data class FieldState(
        var lastKick: LocalDateTime,
        var kickCount: Int,
        var lastSnare: LocalDateTime,
        var snareCount: Int
)

fun pulse(t: Float): Float {
    return max(1f - t, 0f)
}

fun rotate(x: Float, y: Float, theta: Float): Pair<Float, Float>{
    return Pair(cos(theta) * x + (-1 * sin(theta) * y), sin(theta) * y + cos(theta) * y)
}

fun FieldState.fieldVec(x: Float, y: Float): Pair<Float,Float>{
    val kickTimeDiff = (Duration.between(lastKick, LocalDateTime.now()).toMillis().toFloat() * 0.002f)
    val snareTimeDiff = (Duration.between(lastSnare, LocalDateTime.now()).toMillis().toFloat() * 0.002f)
    val radius = 20.0f - (200 * pulse(kickTimeDiff))
    val rotXY = rotate(x, y, snareCount.toFloat())
    return Pair(-radius * rotXY.first, -radius * rotXY.second)
}



fun PApplet.arrow(x1: Float, y1: Float, x2: Float, y2: Float) {
    line(x1, y1, x2, y2);
    pushMatrix();
    translate(x2, y2);
    val a = atan2(x1-x2, y2-y1);
    rotate(a);
    line(0f, 0f, -10f, -10f);
    line(0f, 0f, 10f, -10f);
    popMatrix();
}


fun PApplet.drawState(field: FieldState) {
    val precision = 20
    for (i in 0..precision) {
        for (j in 0..precision) {
            val floatHeight = this.height.toFloat()
            val floatWidth = this.width.toFloat()
            val x_val = (i.toFloat() * (floatHeight/precision.toFloat())) / floatHeight
            val y_val = (j.toFloat() * (floatWidth/precision.toFloat())) / floatWidth
            val field_val = field.fieldVec((x_val - 0.5f) * 2, (y_val - 0.5f) * 2)
            this.arrow(
                    (x_val * floatWidth) - (0.5f * field_val.first),
                    (y_val * floatHeight) - (0.5f * field_val.second),
                    (x_val * floatWidth) + (0.5f * field_val.first),
                    (y_val * floatHeight) + (0.5f * field_val.second)
            )
        }
    }
}