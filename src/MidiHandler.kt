package processedbeats


import java.time.LocalDateTime
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver
//import com.sun.tools.javap.TypeAnnotationWriter.Note
import javax.sound.midi.ShortMessage.NOTE_OFF
import javax.sound.midi.ShortMessage.NOTE_ON
import javax.sound.midi.ShortMessage


class Note(private val key: Int) {

    val name: String
    val octave: Int
    val note: Int

    init {
        this.octave = key / 12 - 1
        this.note = key % 12
        this.name = NOTE_NAMES[note]
    }

//    override fun equals(obj: Any?): Boolean {
//        return obj is Note && this.key == obj.key
//    }

    override fun toString(): String {
        return "Note -> " + this.name + this.octave + " key=" + this.key
    }

    companion object {

        private val NOTE_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    }
}




class StateMidiHandler(var state: MidiState): Receiver {

    override fun send(message: MidiMessage, timeStamp: Long) {
        if (message is ShortMessage) {
            val channel = message.channel

            if (message.command == NOTE_ON) {
                val key = message.data1
                val velocity = message.data2
                val note = Note(key)
//                drums
                if (channel == 0) {
                    if (note.name == "C"){
                        state.lastKick = LocalDateTime.now()
                        state.kickCount += 1
                    }
                    if (note.name == "C#"){
                        state.lastSnare = LocalDateTime.now()
                        state.snareCount += 1
                    }
                }
//                synths
                if (channel == 1) {
                    state.prevMelodyNote = state.currMelodyNote
                    state.prevMelodyTime = state.currMelodyTime
                    state.currMelodyNote = note.note
                    state.currMelodyTime = LocalDateTime.now()
                }
                println(note)
            } else if (message.command == NOTE_OFF) {

                val key = message.data1
                val velocity = message.data2
                val note = Note(key)
//                println(note)
            } else {
//                println("Command:" + message.command)
            }
        }
    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
