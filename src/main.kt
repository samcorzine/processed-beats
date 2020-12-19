package processedbeats

import com.google.gson.GsonBuilder
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector
import java.io.BufferedReader
import java.io.FileReader
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.Duration
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Transmitter
import kotlinx.coroutines.*


class MidiDrawing() : PApplet() {

    val color = 100f
    val alpha = 50f
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
        point ->
            PVector(
                    point[0].toFloat()/faceData.metadata.image_shape[1],
                    point[1].toFloat()/faceData.metadata.image_shape[0]
            ).add(-0.5f,-0.5f).mult(2.0f)
    }}
    val zippedFeatureLists = featureLists.map{featlist -> featlist.slice(0..(featlist.size -2)).zip(featlist.slice(1..featlist.size-1))}

    val featurePairs = zippedFeatureLists.reduce{featlist1, featlist2 -> featlist1 + featlist2}

    val testAnchorLines = listOf(
            Pair(PVector(0.5f, 0.5f), PVector(-0.5f, 0.5f)),
            Pair(PVector(0.5f, -0.5f), PVector(-0.5f, -0.5f)),
            Pair(PVector(-0.5f, 0.5f), PVector(-0.5f, -0.5f)),
            Pair(PVector(0.5f, 0.5f), PVector(0.5f, -0.5f))
    )

    val fieldState = ChargedElements(
            featurePairs
    )
    val midiState = MidiState(
            LocalDateTime.now(),
            1,
            LocalDateTime.now(),
            2,
            5,
            LocalDateTime.now(),
            0,
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
    )
    val indices = 1..100
    val randomX = indices.map{ t -> Random().nextFloat()}// generated random from 1 to 9 included
    val randomY = indices.map{Random().nextFloat()}// generated random from 1 to 9 included


    val particleState = ParticleState(
            randomX.zip(randomY).mapIndexed{
                index, pair -> Particle(
                    PVector((pair.first - 0.5f) * 2,
                    (pair.second - 0.5f) * 2),
                    index % 7,
                    Random().nextFloat() * 0.001f,
                    Random().nextFloat() * 0.001f)
            },
            fieldState
    )

    fun theTransmitter(): Transmitter {
        val midiDevices = MidiSystem.getMidiDeviceInfo()
        for (device in midiDevices) {
            println(device.name)
            println(device.description)
        }
        val device = MidiSystem.getMidiDevice(midiDevices[1])
        if (!device.isOpen) {
            println("Opening device ")
            try {
                device.open()
                println(device.deviceInfo)
                println(device.transmitter)
            } catch (e: MidiUnavailableException) {
                // Handle or throw exception...
                println(e)
            }

        }
        return device.transmitter
    }

    val transmitter = theTransmitter()

    val midiHandler = StateMidiHandler(midiState)


    override fun settings() {
        size(1000, 1000)
    }

    override fun setup() {
        this.transmitter.setReceiver(this.midiHandler)
        strokeCap(PConstants.PROJECT)
        strokeJoin(PConstants.MITER)
        frameRate(30f)
        ellipseMode(RADIUS)
        background(255)
//        noLoop()
    }

    override fun draw() {
        background(255, 10f)
        stroke(color, color, color, alpha)
//        noStroke()
        fill(color, color, color, alpha)
//        updateState(particleState)
        val field = ChargedElements(featurePairs)
//        drawElements(field)
//        drawField(field, 60)
//        drawPolyFieldState(fieldState, midiState.polygon(100), 5)
        drawGrid(field, 75)

//        drawFaceFlowState(fieldState)
//        drawState(particleState)
//        val framerate = 10
//        for (i in 0..100) {
//            val t = i.toDouble()/framerate.toDouble()
//            val midi = toMidiState("testmidi.mid", t)
//            drawGrid(ChargedElements(midi.pulseSquare()), 15)
//            saveFrame("output/frames${i}.png")
//        }
//        println("done")
    }
}

fun main(args: Array<String>) {
    PApplet.main("processedbeats.MidiDrawing")
}