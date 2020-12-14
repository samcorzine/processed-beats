package processedbeats

import processing.core.PVector

class ChargedElements(val anchorPoints: List<Pair<PVector, PVector>>): FieldInterface {
    override fun fieldVec(point: PVector): PVector {
        return anchorPoints
                .map{anchorPoint ->
                    val difvec = PVector.mult(
                            sdSegment(point, anchorPoint.first, anchorPoint.second),
                            1f/PVector.sub(anchorPoint.first, anchorPoint.second).mag()
                    )
                    PVector.mult(difvec, -0.01f/((difvec.mag() * difvec.mag() + 0.01f)))
                }
                .reduce{vec1, vec2 -> PVector.add(vec1, vec2) }
    }
}