package processedbeats

import processing.core.PApplet
import java.time.LocalDateTime

data class Circle(
        var position: Pair<Float, Float>,
        var radius: Float,
        var lastevent: LocalDateTime
)

fun PApplet.drawCircle(circle: Circle) {
    for (i in 1..4) {
        for (j in 1..4) {
            this.ellipse(
                    2 * (circle.position.first - (i * circle.position.first / 5)),
                    2 * (circle.position.second - (j * circle.position.second / 5)),
                    circle.radius,
                    circle.radius
            )
        }
    }
}