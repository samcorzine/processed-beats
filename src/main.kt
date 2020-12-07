package processedbeats

import processing.core.PApplet
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.Duration
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Transmitter

class MidiDrawing() : PApplet() {

    val color = 100f
    val alpha = 200f

    val fieldState = FieldState(LocalDateTime.now(), 0, LocalDateTime.now(), 0)
    val indices = 1..100
    val randomX = indices.map{Random().nextFloat()}// generated random from 1 to 9 included
    val randomY = indices.map{Random().nextFloat()}// generated random from 1 to 9 included


    val particleState = ParticleState(
            randomX.zip(randomY).map{pair -> Particle((pair.first - 0.5f) * 2, (pair.second - 0.5f) * 2)},
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

    val midiHandler = StateMidiHandler(fieldState)


    override fun settings() {
        size(1000, 1000)
    }

    override fun setup() {
        this.transmitter.setReceiver(this.midiHandler)
        frameRate(30f)
        ellipseMode(RADIUS)
        background(255)
    }

    override fun draw() {
        background(255)
        stroke(color, color, color, alpha)
        fill(color, color, color, alpha)
        updateState(particleState)
        drawState(fieldState)
        drawState(particleState)
    }
}

fun main(args: Array<String>) {
    PApplet.main("processedbeats.MidiDrawing")
}