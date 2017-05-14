SalkBuffer {

    // adapted from RedFriks RedBuffer Ugen
	// offset and length in percent and seconds

	*new {|server, path, segmentOffset= 0, segmentLength= 10|

		var file, offsetFrames, lengthFrames;

		file= SoundFile.new;

		if(file.openRead(path).not, {

			("SalkBuffer: file "++path++" not found").warn;

			this.halt;

		});

		offsetFrames= (segmentOffset*file.numFrames).round;

		lengthFrames= (segmentLength*file.sampleRate).round;

		if(offsetFrames+lengthFrames>file.numFrames, {

			"SalkBuffer: selected segment out of buffer bounds".warn;

		});

		^Buffer.read(

			server,

			path,

			offsetFrames,

			lengthFrames,

			{("SalkBuffer: done loading segment from "++path).postln}

		);

	}

}
