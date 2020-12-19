package processedbeats

import java.io.File

import javax.sound.midi.MidiSystem
import javax.sound.midi.MetaMessage
import javax.sound.midi.ShortMessage
import com.sun.media.sound.MidiUtils.META_TEMPO_TYPE
import com.sun.media.sound.MidiUtils.getTempoMPQ
import java.time.LocalDateTime
import javax.sound.midi.MidiMessage
import kotlin.experimental.and


data class DrumEvent(
        val timeInSeconds: Double,
        val type: String
)

fun toMidiState(filePath: String, time: Double): MidiState{
    val drumEvents = fileToDrumEvents(filePath)
    val prevKicks = drumEvents
            .filter{ event -> event.timeInSeconds < time }
            .filter{ event -> event.type == "Kick" }
    val prevSnares = drumEvents
            .filter{ event -> event.timeInSeconds < time }
            .filter{ event -> event.type == "Snare" }
    val kickTimes = prevKicks.map{event -> event.timeInSeconds}
            .sorted()
    val lastKick = if (kickTimes.isEmpty()) {
        0.0
    } else {
        kickTimes.last()
    }
//    val lastSnare = prevSnares.map{event -> event.timeInSeconds}
//            .sorted()[-1]
    return MidiState(
            LocalDateTime.now().minusNanos(
                    ((time - lastKick) * 1e9).toLong()
            ),
            prevKicks.size,
            LocalDateTime.now(),
            prevSnares.size,
            0,
            LocalDateTime.now(),
            0,
            LocalDateTime.now(),
            LocalDateTime.now(),
            true
    )

}

fun fileToDrumEvents(filePath: String) : List<DrumEvent>{
    val NOTE_ON = 0x90
    val NOTE_OFF = 0x80
    val NOTE_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    val sequence = MidiSystem.getSequence(File("testmidi.mid"))

    val outlist = mutableListOf<DrumEvent>()
    var trackNumber = 0
    val ticksPerQuarterNote = sequence.resolution
    val millisperQuarter = sequence.tracks.map {
        track -> ((0 until track.size()).map{
        i -> val tempo = getTempoMPQ(track.get(i).message)
        if (tempo == -1) {0} else {tempo}
    })
    }[0].reduce{a,b -> a + b}
    val secondsPerQuarter = millisperQuarter.toDouble()/1000000.0
    val secondsperTick = secondsPerQuarter/ticksPerQuarterNote
    for (track in sequence.tracks) {
        trackNumber++
        for (i in 0 until track.size()) {
            val event = track.get(i)
            val message = event.message
            if (message is ShortMessage) {
                if (message.command == NOTE_ON) {
                    val key = message.data1
                    val note = Note(key)
                    val velocity = message.data2
                    if (message.channel == 0) {
                        if (note.name == "C"){
                            outlist.add(DrumEvent(event.tick.toDouble() * secondsperTick, "Kick"))
                        }
                        if (note.name == "C#"){
                            outlist.add(DrumEvent(event.tick.toDouble() * secondsperTick, "Snare"))
                        }
                    }
                } else if (message.command == NOTE_OFF) {
                    val key = message.data1
                    val octave = key / 12 - 1
                    val note = key % 12
                    val noteName = NOTE_NAMES[note]
                    val velocity = message.data2
                }
            }
        }
    }
    return outlist
}

fun main(args: Array<String>) {
    val NOTE_ON = 0x90
    val NOTE_OFF = 0x80
    val NOTE_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    val sequence = MidiSystem.getSequence(File("testmidi.mid"))

    val outlist = mutableListOf<DrumEvent>()
    var trackNumber = 0
    val ticksPerQuarterNote = sequence.resolution
    val millisperQuarter = sequence.tracks.map {
        track -> ((0 until track.size()).map{
            i -> val tempo = getTempoMPQ(track.get(i).message)
                if (tempo == -1) {0} else {tempo}
    })
    }[0].reduce{a,b -> a + b}
    println("millis per quarter: $millisperQuarter")
    val secondsPerQuarter = millisperQuarter.toDouble()/1000000.0
    val secondsperTick = secondsPerQuarter/ticksPerQuarterNote
    for (track in sequence.tracks) {
        trackNumber++
        println("Track " + trackNumber + ": size = " + track.size())
        println()
        for (i in 0 until track.size()) {
            val event = track.get(i)
            println("@" + event.tick + " ")
            val message = event.message
            if (message is MetaMessage){
                println("Metamessage: ${getTempoMPQ(message)}")
            }
            if (message is ShortMessage) {
                println("Channel: " + message.channel + " ")
                if (message.command == NOTE_ON) {
                    val key = message.data1
                    val note = Note(key)
                    val velocity = message.data2
                    if (message.channel == 0) {
                        if (note.name == "C"){
                            outlist.add(DrumEvent(event.tick.toDouble() * secondsperTick, "Kick"))
                        }
                        if (note.name == "C#"){
                            outlist.add(DrumEvent(event.tick.toDouble() * secondsperTick, "Snare"))
                        }
                    }
                    println("Note on, $note.name$note.octave key=$key velocity: $velocity")
                } else if (message.command == NOTE_OFF) {
                    val key = message.data1
                    val octave = key / 12 - 1
                    val note = key % 12
                    val noteName = NOTE_NAMES[note]
                    val velocity = message.data2
//                    println("Note off, $noteName$octave key=$key velocity: $velocity")
                } else {
//                    println("Command:" + message.command)
                }
            } else {
//                println("Other message: " + message.javaClass)
            }
        }

        println()
        println(outlist)
    }

}
