package processedbeats

import processing.core.PApplet

data class Circle(
        var position: Pair<Float, Float>,
        var radius: Float
)

fun PApplet.drawCircle(circle: Circle) {
    this.ellipse(
            circle.position.first,
            circle.position.second,
            circle.radius,
            circle.radius
    )
}