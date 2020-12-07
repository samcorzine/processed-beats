package processedbeats

import processing.core.PApplet
import java.time.LocalDateTime
import java.time.Duration
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.tan

data class Particle(
        var x: Float,
        var y: Float,
        var group: Int,
        var vx:Float = 0f,
        var vy:Float = 0f
)

data class ParticleState(
        var particles: List<Particle>,
        var field: FieldState
)

fun partColor(t: Float): Triple<Float, Float, Float>{
    return Triple(
            0.5f + (0.5f * cos(2f * PI *(2f * t + 0.5f)).toFloat()),
            0.5f + (0.5f * cos(2f * PI *(1f * t + 0.2f)).toFloat()),
            0.5f + (0.5f * cos(2f * PI *(1f * t + 0.25)).toFloat())
    )
}


fun PApplet.updateState(particleStates: ParticleState) {
    for (i in particleStates.particles) {
        val fieldVal = particleStates.field.fieldVec(i.x, i.y, i.group)
        i.vx += fieldVal.first * 0.000001f
        i.vy += fieldVal.second * 0.000001f
        i.x += i.vx
        i.y += i.vy
    }
}

fun PApplet.drawState(particleStates: ParticleState) {
    for ((index, i) in particleStates.particles.withIndex()) {
        val particleColor = partColor(i.group.toFloat()/11f)
        fill(particleColor.first * 250, particleColor.second * 250, particleColor.third * 250 , 100f)
        this.ellipse(
                ((i.x * 0.5f) + 0.5f) * this.width,
                ((i.y * 0.5f) + 0.5f) * this.height,
                10.0f,
                10.0f
        )
    }
}