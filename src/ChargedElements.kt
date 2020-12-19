package processedbeats

import processing.core.PVector
import kotlin.math.max

class ChargedElements(val anchorPoints: List<Pair<PVector, PVector>>): FieldInterface {
    override fun fieldVec(point: PVector): PVector {
        return anchorPoints
                .map{anchorPoint ->
                    val difvec = PVector.mult(
                            sdSegment(point, anchorPoint.first, anchorPoint.second),
                            PVector.sub(anchorPoint.first, anchorPoint.second).mag()
                    )
                    PVector.mult(difvec, -0.0001f/(((difvec.mag() * difvec.mag()) + 0.00001f)))
                }
                .sortedBy { t -> t.mag() }.last()
    }
}