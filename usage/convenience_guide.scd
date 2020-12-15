// in addition to the help file code example

//Use Convenience as a Buffer Class
play{PlayBuf.ar(1, Convenience.get(\sounds, 0), 0.41)}


// load some soundfiles and play some buffers
(
z= Convenience;
fork{
	z.clearAll
	if(PathName(Platform.resourceDir +/+ "sounds").entries.isEmpty.not, {
		z.crawl(Platform.resourceDir +/+ "sounds");
		"sound will appear in 5 seconds".postln;
		5.reverseDo{|i|
			(i+1).asString.postln;
			1.wait;
		};
		play{PlayBuf.ar(1, Convenience.get(\sounds, 0), 0.41. loop:1)*0.5}
		play{PlayBuf.ar(1, Convenience.get(\sounds, 1), 0.62, loop:1)*0.5}
		play{PlayBuf.ar(1, Convenience.get(\sounds, 2), [0.123, 0.234, 0.345], loop:1)*0.5}
	}, {
		"somethings wrong with the resource directory".warn;
	});
}
)

// reference parameter values (simple numbers and patterns)
(
z.p(\numberOne,
	dur: 2, tuningOnOff:1, degree: Pseq([1,5,9], inf), octave: 5, ftype: 1, fgain: 2, cutoff: Pwhite(800,6100), attack: 0.01, sustain: 0.1, release: 0.1, tempo: 8,
	pan: Pseq([Pseries(-0.5, 0.01, 100)], inf)
);

z.p(\numberTwo,
	dur: 2, tuningOnOff:1, degree: Pseq([1,5,9], inf), octave: 4, cutoff: Pwhite(2000,2100), res: 0.93, attack: 0.01, sustain: 0.07,
	release: z.pp(\numberOne, \pan).linlin(-0.5,0.5,0.001,0.8)
);

z.p(\numberThree,
	dur: 2, rate: 0.3, pos: z.pp(\numberTwo, \degree)/10, bass: 3, attack: 0.01, 
	sustain: z.pp(\numberOne, \pan).linlin(-0.5,0.5,0.001,0.1), 
	release: z.pp(\numberOne, \pan).linlin(-0.5,0.5,0.001,0.8),
);
)

// Convenience also does simple input routing to effects, caution..
Convenience.ifx(\inns, 0, 0.99, 0, [\delay], \wet1, 2.0, \delayfb, 0.9);

(
Convenience.ifx(\inns, 
	inbus: 0, mul: 0.99, outbus: 0, fxs: [\delay, \smear], 
	args: [\wet1, 2.0, \delayfb, 0.99, \delay, Pwhite(0.02), \wet2, 2.0, \dur, 0.1])
);

Convenience.s(\inns);
