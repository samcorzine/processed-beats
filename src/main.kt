package processedbeats

import processing.core.PApplet
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Transmitter

class MidiDrawing() : PApplet() {

    val color = 100f
    var radius = 100f
    val positionX = 240f
    val positionY = 240f
    val alpha = 100f

    val theCircle = Circle(Pair(positionX, positionY), radius)

    fun theTransmitter(): Transmitter {
        val midiDevices = MidiSystem.getMidiDeviceInfo()
        for (device in midiDevices) {
            println(device.name)
            println(device.description)
        }
        val device = MidiSystem.getMidiDevice(midiDevices[1])
        if (!device.isOpen) {
            println("Opening device")
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

    val midiHandler = MidiHandler(theCircle)


    override fun settings() {
        size(480, 480)
    }

    override fun setup() {
        this.transmitter.setReceiver(this.midiHandler)
        frameRate(30f)
        ellipseMode(RADIUS)
        background(255)
    }

    override fun draw() {
        background(255)
        if (theCircle.radius >= 1){
            theCircle.radius -= 1
        }
        stroke(color, color, color, alpha)
        fill(color, color, color, alpha)
        drawCircle(theCircle)
    }
}

fun main(args: Array<String>) {
    PApplet.main("processedbeats.MidiDrawing")
}