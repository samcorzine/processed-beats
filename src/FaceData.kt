package processedbeats

import processing.core.PVector


data class FaceData(
    val face_locations: FaceLocations,
    val metadata: Metadata
)
data class Metadata(
        val image_shape: List<Int>
)
data class FaceLocations(
        val bottom_lip: List<List<Int>>,
        val chin: List<List<Int>>,
        val left_eye: List<List<Int>>,
        val left_eyebrow: List<List<Int>>,
        val nose_bridge: List<List<Int>>,
        val nose_tip: List<List<Int>>,
        val right_eye: List<List<Int>>,
        val right_eyebrow: List<List<Int>>,
        val top_lip: List<List<Int>>
)