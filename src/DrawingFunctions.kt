package processedbeats

import com.google.gson.GsonBuilder
import processing.core.PApplet
import processing.core.PVector
import java.io.BufferedReader
import java.io.FileReader
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun PApplet.arrow(x1: Float, y1: Float, x2: Float, y2: Float) {
    line(x1, y1, x2, y2);
    pushMatrix();
    translate(x2, y2);
    val a = atan2(x1 - x2, y2 - y1);
    rotate(a);
    line(0f, 0f, -10f, -10f);
    line(0f, 0f, 10f, -10f);
    popMatrix();
}

fun PApplet.drawField(field: FieldInterface, precision: Int = 25) {
    val floatHeight = this.height.toFloat()
    val floatWidth = this.width.toFloat()
    for (i in 0..precision) {
        for (j in 0..precision) {
            val x_val = (i.toFloat() * (floatHeight/precision.toFloat())) / floatHeight
            val y_val = (j.toFloat() * (floatWidth/precision.toFloat())) / floatWidth
            val field_val = field.fieldVec(PVector((x_val - 0.5f) * 2, (y_val - 0.5f) * 2))
            this.arrow(
                    (x_val * floatWidth) - (0.5f * field_val.x),
                    (y_val * floatHeight) - (0.5f * field_val.y),
                    (x_val * floatWidth) + (0.5f * field_val.x),
                    (y_val * floatHeight) + (0.5f * field_val.y)
            )
        }
    }
}

fun PApplet.pointFormat(point: PVector) : PVector {
    return PVector((point.x + 1f) * 0.5f * this.width.toFloat(), (point.y + 1f) * 0.5f * this.height.toFloat())
}

fun PApplet.drawElements(elements: ChargedElements){
    for (p in elements.anchorPoints.map{pairvec -> Pair(pointFormat(pairvec.first), pointFormat(pairvec.second))}){
        this.line(
                p.first.x,
                p.first.y,
                p.second.x,
                p.second.y
        )
    }
}
fun PApplet.drawElements(elements: ChargedElementsCheat){
    for (p in elements.anchorPoints.map{pairvec -> Pair(pointFormat(pairvec.first), pointFormat(pairvec.second))}){
        this.line(
                p.first.x,
                p.first.y,
                p.second.x,
                p.second.y
        )
    }
}

fun PApplet.drawTrangleMidiState(field: MidiState) {
    val smoothTrans = smoothstep(
            0f,
            1f,
            (Duration.between(field.currMelodyTime, LocalDateTime.now()).toMillis().toFloat() * 0.0001f)
    )
    val backColor = partColor(
            (1 - smoothTrans) * (field.prevMelodyNote/12f) + (smoothTrans) * (field.currMelodyNote/12f)
    , 400f)
    background(backColor.x, backColor.y, backColor.z, 5f)
    val numStartingPoints = 3
    val pointlists = (0..numStartingPoints).map {
        num -> mutableListOf(
            PVector(
                    0.05f * ((field.snareCount % 7) + 2) * cos(num * 2f * PI * (1f / numStartingPoints)).toFloat(),
                    0.05f * ((field.snareCount % 5) + 2) * sin(num * 2f * PI * (1f / numStartingPoints)).toFloat()
            )
        )
    }
    val len = 25
    for (i in 0..len) {
        for (j in 0..(pointlists.size - 1)) {
            pointlists[j].add(field.fieldFlowUpdate(pointlists[j][i]))
        }
    }
//    render
    val formattedPointLists = pointlists.map{
        list -> list.map{vec -> this.pointFormat(vec)}
    }
    for (i in 0..len) {
        val particleColor = partColor(i.toFloat()/len.toFloat())
        fill(particleColor.x, particleColor.y, particleColor.z , 50f)
        for (j in 0..(pointlists.size - 2)) {
            this.quad(
                    formattedPointLists[j][i].x,
                    formattedPointLists[j][i].y,
                    formattedPointLists[j][i + 1].x,
                    formattedPointLists[j][i + 1].y,
                    formattedPointLists[j+1][i].x,
                    formattedPointLists[j+1][i].y,
                    formattedPointLists[j+1][i + 1].x,
                    formattedPointLists[j+1][i + 1].y
            )
        }
    }
}

fun PApplet.drawPolyFieldState(field: FieldInterface, poly: List<PVector>, depth: Int) {
    val pointlists = poly.map {
        point -> mutableListOf(
            point
        )
    }
    for (i in 0..depth) {
        for (j in 0..(pointlists.size - 1)) {
            pointlists[j].add(field.fieldFlowUpdate(pointlists[j][i]))
        }
    }
//    render
    val formattedPointLists = pointlists.map{
        list -> list.map{vec -> this.pointFormat(vec)}
    }
    for (i in 0..depth) {
        val particleColor = partColor(i.toFloat()/depth.toFloat())
        fill(particleColor.x, particleColor.y, particleColor.z , 50f)
        for (j in 0..(pointlists.size - 2)) {
            this.quad(
                    formattedPointLists[j][i].x,
                    formattedPointLists[j][i].y,
                    formattedPointLists[j][i + 1].x,
                    formattedPointLists[j][i + 1].y,
                    formattedPointLists[j+1][i].x,
                    formattedPointLists[j+1][i].y,
                    formattedPointLists[j+1][i + 1].x,
                    formattedPointLists[j+1][i + 1].y
            )
        }
    }
}

fun PApplet.drawFaceFlowState(field: MidiState) {
    val smoothTrans = smoothstep(
            0f,
            1f,
            (Duration.between(field.currMelodyTime, LocalDateTime.now()).toMillis().toFloat() * 0.0001f)
    )
    val backColor = partColor(
            (1 - smoothTrans) * (field.prevMelodyNote/12f) + (smoothTrans) * (field.currMelodyNote/12f)
            , 400f)
    background(backColor.x, backColor.y, backColor.z, 5f)
    val gson = GsonBuilder().create()
    val bufferedReader = BufferedReader(FileReader("facedata.json"))
    val faceData = gson.fromJson(bufferedReader, FaceData::class.java)
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
        point -> mutableListOf(
            PVector(
                    point[0].toFloat() / faceData.metadata.image_shape[1],
                    point[1].toFloat() / faceData.metadata.image_shape[0]
            ).add(-0.5f,-0.5f).mult(2.0f)
    )
    }}
    val len = 25
    for (i in 0..len) {
        for (featurelist in featureLists){
            for (j in 0..(featurelist.size - 1)) {
                featurelist[j].add(field.fieldFlowUpdate(featurelist[j][i]))
        }
    }}
//    render
    val formattedFeatureLists = featureLists.map{
        pointlist -> pointlist.map{
        pointpath -> pointpath.map{
        vec -> this.pointFormat(vec)}
    }}
    for (i in 0..len) {
        val particleColor = partColor(i.toFloat()/len.toFloat())
        fill(particleColor.x, particleColor.y, particleColor.z , 50f)
        for (featurelist in formattedFeatureLists){
            for (j in 0..(featurelist.size - 2)) {
                this.quad(
                        featurelist[j][i].x,
                        featurelist[j][i].y,
                        featurelist[j][i + 1].x,
                        featurelist[j][i + 1].y,
                        featurelist[j+1][i].x,
                        featurelist[j+1][i].y,
                        featurelist[j+1][i + 1].x,
                        featurelist[j+1][i + 1].y
                )
            }
        }
    }
}

fun rotate(point: PVector, theta: Float): PVector {
    return PVector(cos(theta) * point.x + (-1 * sin(theta) * point.y), sin(theta) * point.x + cos(theta) * point.y)
}

fun PApplet.drawGrid(field: FieldInterface, precision: Int) {
    val points = mutableListOf<PVector>()
    val grid = (0..precision).map { x ->
        (0..precision).map { y ->
            PVector(
                    ((x / precision.toFloat()) - 0.5f) * 2,
                    ((y / precision.toFloat()) - 0.5f) * 2
            )
        }
    }
    val movedPoints = grid.map{
//        point -> pointFormat(field.fieldFlowUpdate(point))
        col -> col.map{
            point -> pointFormat(field.fieldFlowUpdate(point))
        }
    }
//    val color = partColor(1f, 100f)
//    fill(color.x, color.y, color.z)
    (0 until precision).map{ i->
        (0 until precision).map{ j->
//            val color = partColor((LocalDateTime.now().second.toFloat() * 0.1f) + (i * 0.01f), 50f)
//            fill(color.x, color.y, color.z)
             this.quad(
                    movedPoints[i][j].x,
                    movedPoints[i][j].y,
                    movedPoints[i+1][j].x,
                    movedPoints[i+1][j].y,
                    movedPoints[i+1][j+1].x,
                    movedPoints[i+1][j+1].y,
                    movedPoints[i][j+1].x,
                    movedPoints[i][j+1].y
            )
        }
    }
//    val xaxis = movedPoints.map{point -> point.x}
//    val yaxis = movedPoints.map{point -> point.y}
//
//    movedPoints.map{
//            this.quad(
//                xaxis[x],
//                yaxis[y],
//                xaxis[x + 1],
//                yaxis[y],
//                xaxis[x + 1],
//                yaxis[y + 1],
//                xaxis[x],
//                yaxis[y + 1]
//            )
//        }
//    }

}