package processedbeats


import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

class MidiHandler(var circle: Circle): Receiver {

    override fun send(message: MidiMessage?, timeStamp: Long) {
        println("ReceivedMessage")
        println(message)
        circle.radius += 100

    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
