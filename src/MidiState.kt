package processedbeats

import processing.core.PVector
import java.beans.PropertyVetoException
import java.time.LocalDateTime
import java.time.Duration
import kotlin.math.*


class MidiState(
        var lastKick: LocalDateTime,
        var kickCount: Int,
        var lastSnare: LocalDateTime,
        var snareCount: Int,
        var prevMelodyNote: Int,
        var prevMelodyTime: LocalDateTime,
        var currMelodyNote: Int,
        var currMelodyTime: LocalDateTime,
        var now: LocalDateTime,
        var inRenderMode: Boolean
): FieldInterface {
    override fun fieldVec(point: PVector): PVector {
        val snareTimeDiff = (Duration.between(lastSnare, now()).toMillis().toFloat() * 0.002f)
        val radius = 100.0f - (200 * pulse(kickTimeDiff()))
        val rotXY = rotate(point, snareCount.toFloat())
        val shifted = PVector.add( rotXY,
                PVector(-1f * (((snareCount) % 11)/22f) * 0.5f, -1f* (((snareCount) % 11)/22f) * 0.5f))
        return PVector.mult(shifted, -radius)
    }
    fun polygon(nSides: Int): List<PVector>{
        return (1..nSides+1).map{
            num ->
                PVector.mult(
                        PVector(
                        0.1f * (snareCount % 7 + 1) * cos(num * 2f * PI * (1f / nSides)).toFloat(),
                        0.1f * (snareCount % 5 + 1) * sin(num * 2f * PI * (1f / nSides)).toFloat()),
                        pulse(kickTimeDiff()) + 0.5f
                )

        }
    }
    fun polygonSides(nSides: Int): List<Pair<PVector, PVector>>{
        return this.polygon(nSides).slice(0..nSides-2).zip(this.polygon(nSides).slice(1 until nSides))
    }
    fun pulseSquare(): List<Pair<PVector, PVector>>{
        val size = (pulse(kickTimeDiff()) * -1.8F) + 0.8F
        return listOf(
                Pair(PVector(size, size), PVector(-size, size)),
                Pair(PVector(size, -size), PVector(-size, -size)),
                Pair(PVector(-size, size), PVector(-size, -size)),
                Pair(PVector(size, size), PVector(size, -size))
        )
    }
    fun kickTimeDiff(): Float {
        return Duration.between(lastKick, now()).toMillis().toFloat() * 0.002f
    }
    fun now(): LocalDateTime {
        return if (inRenderMode) now else LocalDateTime.now()
    }
}

interface FieldInterface {
    fun fieldVec(point: PVector): PVector
}

fun pulse(t: Float): Float {
    return max(1f - t, 0f)
}

//this probably doesn't handle recentering the vectors at 0
fun sdSegment(point: PVector, a: PVector, b: PVector ): PVector {
    val pa = PVector.sub(point, a)
    val ba = PVector.sub(b, a)
    val h = clamp(PVector.dot(pa,ba)/PVector.dot(ba, ba), 0.0f, 1.0f);
    return PVector.sub(pa, PVector.mult(ba, h))
}

fun FieldInterface.fieldFlowUpdate(point: PVector) : PVector{
    val field_val = this.fieldVec(point)
    return field_val.add(point)
}


fun smoothstep(edge0: Float, edge1: Float, x: Float): Float {
    var x = x
    // Scale, bias and saturate x to 0..1 range
    x = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f)
    // Evaluate polynomial
    return x * x * (3 - 2 * x)
}

fun clamp(x: Float, lowerlimit: Float, upperlimit: Float): Float {
    var x = x
    if (x < lowerlimit)
        x = lowerlimit
    if (x > upperlimit)
        x = upperlimit
    return x
}


