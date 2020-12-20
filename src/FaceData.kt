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

fun faceToAnchors(faceData: FaceData): List<Pair<PVector, PVector>>{
        val featureLists = listOf(
                faceData.face_locations.left_eyebrow,
                faceData.face_locations.right_eyebrow,
                faceData.face_locations.nose_bridge,
                faceData.face_locations.nose_tip,
                faceData.face_locations.chin,
                faceData.face_locations.left_eye,
                faceData.face_locations.right_eye,
                faceData.face_locations.top_lip,
                faceData.face_locations.bottom_lip
        ).map{featlist -> featlist.map{
                        point ->
                PVector(
                        point[0].toFloat()/faceData.metadata.image_shape[1],
                        point[1].toFloat()/faceData.metadata.image_shape[0]
                ).add(-0.5f,-0.5f).mult(2.0f)
        }}
        val zippedFeatureLists = featureLists.map{featlist -> featlist.slice(0..(featlist.size -2)).zip(featlist.slice(1..featlist.size-1))}

        return zippedFeatureLists.reduce{featlist1, featlist2 -> featlist1 + featlist2}
}
