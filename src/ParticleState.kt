package processedbeats

import processing.core.PApplet
import java.time.LocalDateTime
import java.time.Duration
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.tan

data class Particle(
        var x: Float,
        var y: Float
)

data class ParticleState(
        var particles: List<Particle>,
        var field: FieldState
)

fun PApplet.updateState(particleStates: ParticleState) {
    for (i in particleStates.particles) {
        val fieldVal = particleStates.field.fieldVec(i.x, i.y)
        i.x += fieldVal.first * 0.0001f
        i.y += fieldVal.second * 0.0001f
    }
}

fun PApplet.drawState(particleStates: ParticleState) {
    for (i in particleStates.particles) {
        this.ellipse(
                ((i.x * 0.5f) + 0.5f) * this.width,
                ((i.y * 0.5f) + 0.5f) * this.height,
                10.0f,
                10.0f
        )
    }
}