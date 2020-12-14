package processedbeats

import processing.core.PApplet
import processing.core.PVector
import java.time.LocalDateTime
import java.time.Duration
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.tan

data class Particle(
        var pos : PVector,
        var group: Int,
        var vx:Float = 0f,
        var vy:Float = 0f
)

data class ParticleState(
        var particles: List<Particle>,
        var field: FieldInterface
)

fun PApplet.partColor(t: Float, intensity: Float =255f): PVector{
    return PVector(
            0.5f + (0.5f * cos(2f * PI *(1.0f * t + 0.0f)).toFloat()),
            0.5f + (0.5f * cos(2f * PI *(0.7f * t + 0.15f)).toFloat()),
            0.5f + (0.5f * cos(2f * PI *(0.4f * t + 0.20)).toFloat())
    ).mult(intensity)
}

fun PApplet.updateState(particleStates: ParticleState) {
    for (i in particleStates.particles) {
        val fieldVal = particleStates.field.fieldVec(i.pos)
        i.vx += fieldVal.x * 0.0001f
        i.vy += fieldVal.y * 0.0001f
        i.pos.x += i.vx
        i.pos.y += i.vy
    }
}

fun PApplet.drawState(particleStates: ParticleState) {
    for ((index, i) in particleStates.particles.withIndex()) {
        val particleColor = partColor(i.group.toFloat()/11f)
        fill(particleColor.x, particleColor.y, particleColor.z , 30f)
        val formatted = pointFormat(i.pos)
        this.ellipse(
                formatted.x,
                formatted.y,
                10.0f,
                10.0f
        )
    }
}